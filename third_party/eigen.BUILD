# Description:
#   Eigen is a C++ template library for linear algebra: vectors,
#   matrices, and related algorithms.

load("@rules_cc//cc:defs.bzl", "cc_library")

# Files known to be under MPL2 license.
EIGEN_MINIMAL_FILES = glob(
    [
        "Eigen/Cholesky",
        "Eigen/Dense",
        "Eigen/src/Cholesky/**",
        "Eigen/Core",
        "Eigen/src/Core/**",
        "Eigen/Eigenvalues",
        "Eigen/src/Eigenvalues/**",
        "Eigen/Geometry",
        "Eigen/src/Geometry/**",
        "Eigen/HouseHolder",
        "Eigen/src/HouseHolder/**",
        "Eigen/Jacobi",
        "Eigen/src/Jacobi/**",
        "Eigen/LU",
        "Eigen/src/LU/**",
        "Eigen/src/misc/**",
        "Eigen/src/plugins/**",
        "Eigen/QR",
        "Eigen/src/QR/**",
        "Eigen/SVD",
        "Eigen/src/SVD/**",
    ],
    exclude = [
        # Guarantees that any non-MPL2 file added to the list above will fail to
        # compile.
        "Eigen/src/Core/util/NonMPL2.h",
        "Eigen/**/CMakeLists.txt",
    ],
)

cc_library(
    name = "eigen_minimal",
    hdrs = EIGEN_MINIMAL_FILES,
    defines = [
        # This define (mostly) guarantees we don't link any problematic
        # code. We use it, but we do not rely on it, as evidenced above.
        "EIGEN_MPL2_ONLY",
        "EIGEN_MAX_ALIGN_BYTES=64",
        "EIGEN_HAS_TYPE_TRAITS=0",
    ],
    includes = ["."],
    visibility = ["//visibility:public"],
)

filegroup(
    name = "eigen_header_files",
    srcs = EIGEN_MINIMAL_FILES,
    visibility = ["//visibility:public"],
)
