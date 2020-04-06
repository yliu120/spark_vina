#!/bin/bash -l
#SBATCH
#SBATCH --job-name=test_node
#SBATCH --time=48:0:0
#SBATCH -N 2
#SBATCH --ntasks-per-node=1
#SBATCH --cpus-per-task=28
#SBATCH --partition=gpu
#SBATCH --reservation=sparkbazel

module r tensorflow

export FIESTA_HOME=$HOME/fiesta
export PYSPARK_PYTHON=python3
export WORKDIR=$HOME/scratch/spark_vina
export SPARK_MASTER_PORT=10004
export SPARK_WORKER_DIR=/local/work
export SPARK_LOCAL_DIRS=/local
export PARTITION=JD

cd $FIESTA_HOME
bazel build script/bash/spark_cluster:all
bazel build spark_vina:spark_vina_app.par.py

source script/bash/spark_cluster/start-spark-cluster.sh

$SPARK_HOME/bin/spark-submit                                            \
  --py-files bazel-bin/spark_vina/spark_vina_app.par.py                 \
  --verbose                                                             \
  --master spark://${SLURMD_NODENAME}:${SPARK_MASTER_PORT}              \
  --driver-java-options "-Djava.io.tmpdir=/local"                       \
  --conf "spark.executorEnv.PATH=$PATH"                                 \
  --conf "spark.executorEnv.PYTHONPATH=$FIESTA_HOME/bazel-bin"          \
  --conf "spark.executor.extraJavaOptions=-Djava.io.tmpdir=/local"      \
  --driver-memory 8G                                                    \
  --executor-memory 8G                                                  \
  --num-executors 14                                                    \
  --conf "spark.executor.instances=14"                                  \
  --conf "spark.local.dir=/local/"                                      \
  --conf "spark.task.cpus=4"                                            \
  --conf "spark.executor.cores=4"                                       \
  --conf "spark.driver.cores=4"                                         \
  --conf "spark.executor.memory=8g"                                     \
  --total-executor-cores 56                                             \
  bazel-bin/spark_vina/spark_vina_app.par.py                            \
  --spark_master=spark://${SLURMD_NODENAME}:${SPARK_MASTER_PORT}        \
  --receptor_path=$WORKDIR/4ZPH-docking.pdb.pdbqt                       \
  --ligand_dir=$WORKDIR/ligands/${PARTITION}                            \
  --output_dir=$WORKDIR/output/${PARTITION}                             \
  --center_x 170.0 --center_y -110.0 --center_z -110.0                  \
  --num_tasks=280 --cpu 4 --threshold -6.0

source script/bash/spark_cluster/stop-spark-cluster.sh
