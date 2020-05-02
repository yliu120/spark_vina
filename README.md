# Spark Vina

> *If I have seen further it is by standing on the shoulders of Giants.*
>
> -- Sir Issac Newton in 1675

**Spark Vina** is not an invention but a collection of great work invented by
awesome people. It is a marriage between a well-known virtual screening tool
[Vina](http://vina.scripps.edu/) and a popular big data batch processing
framework [Apache Spark](https://spark.apache.org/). Equipped with **Spark**,
the virtual screening tool **Vina** can be easily deployed to cloud computing
infrastructures.

**Spark Vina** doesn't add any optimization on the original virtual screening
algorithm so far. Anyone who knows **Vina** well is able to quickly understand
how **Spark Vina** works. For those who don't understand virtual screening,
please read about *Autodock Vina* from the above link, where a tutorial can be
found.

## Supporting Platforms

Currently, **Spark Vina** only plans to support the following platforms:
+ Linux
+ Mac OS

## Build Tool

The only build tool used by **Spark Vina** is [Bazel](https://bazel.build/). All
build targets in this repository should only be built with Bazel, including
Docker images.

## How to Build

### Prerequisites

+ C++ Compiler: Both `clang` and `gcc` are supported.
+ Java JDK: version = 8
  + Let's stick to Java 8 until Spark officially supports Java 11.
  + For Linux users, packages can be installed through package managers binding
    with the specific Linux distributions. For instance, for Debian/Ubuntu users,
    they can be installed through `sudo apt install g++ unzip zip openjdk-11-jdk`.
  + For mac users, XCode needs to be installed for building any software. XCode
    can be downloaded from Apple Store. However, to use Bazel, you might need to
    agree on some license:
    ```bash
    sudo xcode-select -s /Applications/Xcode.app/Contents/Developer
    sudo xcodebuild -license
    ```
    In addition, please reference to Oracle's website for installing Java SDK.
    [Download Java for Mac OS X](https://www.java.com/en/download/mac_download.jsp)
+ Bazel: version > 2.0
  + [How to install Bazel?](https://docs.bazel.build/versions/master/install.html)
+ Docker: version > 17.03
  + [How to install Docker?](https://docs.docker.com/get-docker/)
  + You will probably need to start your Docker daemon before you can use it.

### Major Build Targets

To build any target specified in this repository, you are recommended to be
always at the root directory, where places the `WORKSPACE` file.

+ The major JAR file for **Spark Vina**: `spark_vina_main_deploy.jar`

  ```bash
  bazel build //:spark_vina_main_deploy.jar
  ```
  For people who want to enable AVX2 SIMD instruction set:
  ```bash
  bazel build --config=avx2 //:spark_vina_main_deploy.jar
  ```
  In addition, we also support SSE4_2 instruction set if you are running on an
  older CPU. More instruction sets can be supported by editing the Bazel build
  flags. The JAR file is a self-containing JAR file that carries all its
  dependencies.
  
+ The docker image running the above JAR file only.
  ```bash
  sudo bazel build //:spark_vina_image
  sudo bazel-bin/spark_vina_image.executable
  ```
  `sudo` is added here to avoid root permissions issues since under some
  platforms, docker has to be run with root privilege.
  Please note that the compilation of the JAR file living inside this distroless
  image happens inside [another Bazel container](l.gcr.io/google/bazel:latest).
  The second command will load the built image to your docker daemon.
  ```bash
  $ sudo docker images
  REPOSITORY               TAG                 IMAGE ID       ...      SIZE
  spark_vina/spark_vina    spark_vina_image    23cee7cd8528   ...      303MB
  ```
  
+ Examples for Python bindings:
  ```bash
  bazel build //python:vina_example_py
  ```
  We are intended to provide less support for Python bindings because it is
  gonna be hard for python applications to be deployed to Spark clusters.
  However, Python bindings might be important for interactive data analysis.
  
## Test Drive Spark Vina

As we build targets with Bazel, there will be multiple ways to run Spark Vina
targets. We ship some a small test dataset (See `//data` for details) for
testing as well as test driving the software. The common goal of all test runs
is to screen compounds with good estimated free energy of binding (Vina scores)
against the receptor (protein: `HIF2a` in our example).

+ **Help Message**: Will show if running with no parameters.
+ Run with `bazel run`:
  ```bash
    bazel run //:spark_vina_main                                                \
         --receptor_path=./data/protein/4ZPH-docking.pdb.pdbqt                  \
         --ligand_dir=/Users/yunlongl/Downloads/spark_vina/data/ligands/HB/AAMP \
         --center_x=170.0 --center_y=-110.0 --center_z=-110.0                   \
         --size_x=10.0 --size_y=10.0 --size_z=10.0                              \
         --num_modes=5 --output_dir=$HOME/output/
    ```
+ Run with `java -jar':
  ```bash
  java -jar bazel-bin/spark_vina_main_deploy.jar                              \
       --receptor_path=./data/protein/4ZPH-docking.pdb.pdbqt                  \
       --ligand_dir=/Users/yunlongl/Downloads/spark_vina/data/ligands/HB/AAMP \
       --center_x=170.0 --center_y=-110.0 --center_z=-110.0                   \
       --size_x=10.0 --size_y=10.0 --size_z=10.0                              \
       --num_modes=5 --output_dir=$HOME/output/
  ```
+ Run with Docker:
  ```bash
  docker run -v $PWD/data:/workspace/data                               \
             -v $HOME/output:/workspace/output                          \
             -it -p 4040:4040 spark_vina/spark_vina:spark_vina_image    \
             --receptor_path=/workspace/data/protein/4ZPH-docking.pdb.pdbqt \
             --ligand_dir=/workspace/data/ligands/HB/AAMP                   \
             --center_x=170.0 --center_y=-110.0 --center_z=-110.0           \
             --size_x=10.0 --size_y=10.0 --size_z=10.0 --num_modes=5        \
             --output_dir=/workspace/output/
  ```
+ Spark WebUI Monitoring: Please visit `localhost:4040` in your browser for 
  visualizing the Spark workflow.
+ The result of the test drives is materialized to the above example output
  directory, in a format of [Apache Parquet](https://parquet.apache.org/). They
  can be easily load into Jupyter notebooks. We show [an example](
  colab/SparkVinaResults.ipynb) of how to read the parquet columnar table.
  
We would recommend you deploying spark_vina_main_deploy.jar in
Kubernetes clusters for large scale virtual screen tasks. The instructions are
among our TODO list.
  

  

