"""This file runs vina with spark for parallel processes."""

import argparse
import logging
import os
import sys

from pyspark import SparkConf
from pyspark import SparkContext

__author__ = 'Yunlong Liu (davislong198833@gmail.com)'

_DESCRIPTION = "Spark Vina"
_EPILOG = "Run 'spark_vina help' to get all commands and options."

# Parameters from external cmd flags
FLAGS = None

# Setup global logger
LOGGER = logging.getLogger('spark_vina')
LOGGER.setLevel(logging.INFO)
CONSOLE_HANDLER = logging.StreamHandler()
CONSOLE_HANDLER.setLevel(logging.INFO)
CONSOLE_HANDLER.setFormatter(
    logging.Formatter('[%(levelname)s %(name)s %(asctime)s] %(message)s'))
LOGGER.addHandler(CONSOLE_HANDLER)


def _fit_ligands_in_one_shard(ligand_iter):
    # pylint: disable=no-name-in-module, import-error
    from python.vina_wrap import VinaDock
    dock = VinaDock(FLAGS.receptor_path, FLAGS.center_x, FLAGS.center_y,
                    FLAGS.center_z, FLAGS.size_x, FLAGS.size_y, FLAGS.size_z,
                    FLAGS.cpu, FLAGS.num_modes)
    results = dock.vina_fit([ligand
                             for ligand in ligand_iter], FLAGS.threshold)

    # in the future, we may remove this and make VinaResult type pickleable.
    yield [(result.affinity, result.ligand_str) for result in results]


def _wrap_read_ligand_to_strings(item):
    # pylint: disable=no-name-in-module, import-error
    from python.vina_wrap import read_ligand_to_strings
    return read_ligand_to_strings(item)


def _output_results(results):
    if not os.path.exists(FLAGS.output_dir):
        os.mkdir(FLAGS.output_dir)
    for res_id, result in enumerate(results):
        output = "REMARK  AFFINITY %f\n" % result[0]
        output += result[1]
        output_file_name = os.path.join(FLAGS.output_dir,
                                        "ligand_%05d.pdbqt" % res_id)
        with open(output_file_name, 'w+') as output_file:
            output_file.write(output)


def main():
    """Main Entry of the spark app."""
    # Sanity checks for the paths
    if not os.path.exists(FLAGS.receptor_path):
        raise ValueError("Receptor PDBQT not found")
    if not os.path.exists(FLAGS.ligand_dir):
        raise ValueError("Ligand base directory not exists.")

    LOGGER.info('Found receptor in %s', FLAGS.receptor_path)

    ligand_shards = []
    for root, _, files in os.walk(FLAGS.ligand_dir):
        for file_name in files:
            if file_name.endswith('.pdbqt') or file_name.endswith('.pdbqt.gz'):
                ligand_shards.append(os.path.join(root, file_name))

    if not ligand_shards:
        raise ValueError("Zero ligand shard is found.")

    if FLAGS.output_num > 99999:
        LOGGER.error("Cannot output more than 99999 ligands.")
        FLAGS.output_num = 1000
        LOGGER.info("changed output number to default 1000.")

    LOGGER.info('Found %d shards of ligands in %s',
                len(ligand_shards), FLAGS.ligand_dir)

    conf = SparkConf()
    conf.setMaster(FLAGS.spark_master)
    conf.setAppName('SparkVina')

    context = SparkContext(conf=conf)
    context.setLogLevel("DEBUG")

    results = context.parallelize(ligand_shards).flatMap(
        _wrap_read_ligand_to_strings)                            \
        .repartition(FLAGS.num_tasks)                            \
        .mapPartitions(_fit_ligands_in_one_shard)                \
        .flatMap(lambda x: x)                                    \
        .takeOrdered(FLAGS.output_num, key=lambda x: x[0])

    _output_results(results)


if __name__ == '__main__':
    # Parse command line args and then feed it to main.
    PARSER = argparse.ArgumentParser(
        prog='spark_vina', description=_DESCRIPTION, epilog=_EPILOG)

    PARSER.add_argument(
        '--spark_master',
        nargs='?',
        default='local[*]',
        help='Spark Master Address.')

    PARSER.add_argument(
        '--receptor_path',
        nargs='?',
        required=True,
        help='A path in string of the receptor pdbqt.')

    PARSER.add_argument(
        '--ligand_dir',
        nargs='?',
        required=True,
        help='A dir in string of the ligand pdbqt or pdbqt.gz.')

    PARSER.add_argument(
        '--output_dir',
        nargs='?',
        default='.',
        help='A dir in string of the ligand pdbqt or pdbqt.gz.')

    PARSER.add_argument(
        '--center_x', nargs='?', type=float, default=0.0, help='center_x')

    PARSER.add_argument(
        '--center_y', nargs='?', type=float, default=0.0, help='center_y')

    PARSER.add_argument(
        '--center_z', nargs='?', type=float, default=0.0, help='center_z')

    PARSER.add_argument(
        '--size_x', nargs='?', type=float, default=30.0, help='size_x')

    PARSER.add_argument(
        '--size_y', nargs='?', type=float, default=30.0, help='size_y')

    PARSER.add_argument(
        '--size_z', nargs='?', type=float, default=30.0, help='size_z')

    PARSER.add_argument(
        '--num_modes', nargs='?', type=int, default=8, help='num_modes')

    PARSER.add_argument(
        '--output_num', nargs='?', type=int, default=1000, help='output_num')

    PARSER.add_argument(
        '--num_tasks', nargs='?', type=int, default=1, help='num_tasks')

    PARSER.add_argument('--cpu', nargs='?', type=int, default=1, help='cpu')

    PARSER.add_argument(
        '--threshold', nargs='?', type=float, default=1.0, help='threshold')

    FLAGS, UNPARSED = PARSER.parse_known_args()
    if UNPARSED:
        LOGGER.warning('The following args are not used: %s',
                       ' '.join(UNPARSED))

    if not vars(FLAGS):
        PARSER.print_help()
        sys.exit(1)

    main()
