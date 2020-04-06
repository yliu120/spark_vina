"""This file shows how to use vina python bindings."""

import argparse

import fiesta3.spark_vina.vina_wrap as vina  # pylint:disable=import-error

__author__ = 'Yunlong Liu (davislong198833@gmail.com)'


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "-cpu", type=int, default=4, help="number of cpus used.")
    args = parser.parse_args()

    ligand_paths = [
        "spark_vina/data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt.gz",
        "spark_vina/data/ligands/HB/AAMN/HBAAMN.xaa.pdbqt.gz"
    ]
    ligand_strs = []
    for path in ligand_paths:
        ligand_strs.extend(vina.read_ligand_to_strings(path))

    dock = vina.VinaDock("spark_vina/data/protein/4ZPH-docking.pdb.pdbqt", 0,
                         0, 0, 30, 30, 30, args.cpu, 5)

    results = dock.vina_fit(ligand_strs, 1.0)

    for result in results:
        print("ligand_str: ", result.ligand_str, " affinity: ",
              result.affinity)


if __name__ == '__main__':
    main()
