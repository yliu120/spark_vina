package(default_visibility = ["//visibility:public"])

load("//:openbabel.bzl", "ob_cc_library")

ob_cc_library(
    name = "tokenst",
    srcs = ["src/tokenst.cpp"],
    hdrs = [
        "include/openbabel/tokenst.h",
    ],
)

ob_cc_library(
    name = "oberror",
    srcs = ["src/oberror.cpp"],
    hdrs = [
        "include/openbabel/oberror.h",
    ],
    alwayslink = 1,
)

ob_cc_library(
    name = "base",
    srcs = ["src/base.cpp"],
    hdrs = [
        "include/openbabel/base.h",
    ],
    deps = [
        ":tokenst",
    ],
    alwayslink = 1,
)

ob_cc_library(
    name = "bitvec",
    srcs = ["src/bitvec.cpp"],
    hdrs = [
        "include/openbabel/bitvec.h",
    ],
    deps = [
        ":oberror",
    ],
)

ob_cc_library(
    name = "dlhandler",
    srcs = ["src/dlhandler_unix.cpp"],
    hdrs = [
        "include/openbabel/dlhandler.h",
    ],
    deps = [
        ":oberror",
    ],
)

ob_cc_library(
    name = "format",
    srcs = ["src/format.cpp"],
    hdrs = [
        "include/openbabel/format.h",
    ],
    deps = [
        ":plugin",
    ],
)

ob_cc_library(
    name = "elements",
    srcs = ["src/elements.cpp"],
    hdrs = [
        "include/openbabel/elements.h",
        "src/elementtable.h",
    ],
)

ob_cc_library(
    name = "locale",
    srcs = ["src/locale.cpp"],
    hdrs = [
        "include/openbabel/locale.h",
    ],
    alwayslink = 1,
)

ob_cc_library(
    name = "op",
    srcs = ["src/op.cpp"],
    hdrs = [
        "include/openbabel/op.h",
    ],
    deps = [
        ":plugin",
    ],
)

ob_cc_library(
    name = "obconversion",
    srcs = ["src/obconversion.cpp"],
    hdrs = [
        "include/openbabel/lineend.h",
        "include/openbabel/obconversion.h",
    ],
    deps = [
        ":format",
        ":locale",
        ":oberror",
    ],
)

ob_cc_library(
    name = "plugin",
    srcs = ["src/plugin.cpp"],
    hdrs = [
        "include/openbabel/plugin.h",
    ],
    deps = [
        ":dlhandler",
    ],
)

ob_cc_library(
    name = "rand",
    srcs = [
        "src/rand.cpp",
        "src/rand.h"
    ],
)

ob_cc_library(
    name = "core",
    srcs = [
        "src/alias.cpp",
        "src/atom.cpp",
        "src/bond.cpp",
        "src/bondtyper.cpp",
        "src/builder.cpp",
        "src/chains.cpp",
        "src/data.cpp",
        "src/data_utilities.cpp",
        "src/descriptor.cpp",
        "src/distgeom.cpp",
        "src/generic.cpp",
        "src/graphsym.cpp",
        "src/isomorphism.cpp",
        "src/kekulize.cpp",
        "src/math/matrix3x3.cpp",
        "src/math/spacegroup.cpp",
        "src/math/transform3d.cpp",
        "src/math/vector3.cpp",
        "src/mol.cpp",
        "src/molchrg.cpp",
        "src/mcdlutil.cpp",
        "src/obfunctions.cpp",
        "src/obiter.cpp",
        "src/obutil.cpp",
        "src/parsmart.cpp",
        "src/phmodel.cpp",
        "src/query.cpp",
        "src/residue.cpp",
        "src/rotamer.cpp",
        "src/rotor.cpp",
        "src/ring.cpp",
        "src/stereo/cistrans.cpp",
        "src/stereo/facade.cpp",
        "src/stereo/perception.cpp",
        "src/stereo/squareplanar.cpp",
        "src/stereo/stereo.cpp",
        "src/stereo/stereoutil.h",
        "src/stereo/tetrahedral.cpp",
        "src/stereo/tetraplanar.cpp",
        "src/stereo/tetranonplanar.cpp",
        "src/transform.cpp",
        "src/typer.cpp",
        "data/atomtyp.h",
        "data/bondtyp.h",
        # The content of phmodeldata could be inaccurate but we don't care in our use cases.
        "data/phmodeldata.h",
        "data/resdata.h",
        "data/types.h",
        "data/torlib.h",
    ],
    hdrs = [
        "include/openbabel/alias.h",
        "include/openbabel/atom.h",
        "include/openbabel/bond.h",
        "include/openbabel/bondtyper.h",
        "include/openbabel/builder.h",
        "include/openbabel/chains.h",
        "include/openbabel/data.h",
        "include/openbabel/data_utilities.h",
        "include/openbabel/descriptor.h",
        "include/openbabel/distgeom.h",
        "include/openbabel/generic.h",
        "include/openbabel/graphsym.h",
        "include/openbabel/internalcoord.h",
        "include/openbabel/isomorphism.h",
        "include/openbabel/kekulize.h",
        "include/openbabel/lineend.h",
        "include/openbabel/math/matrix3x3.h",
        "include/openbabel/math/spacegroup.h",
        "include/openbabel/math/transform3d.h",
        "include/openbabel/math/vector3.h",
        "include/openbabel/mol.h",
        "include/openbabel/molchrg.h",
        "include/openbabel/mcdlutil.h",
        "include/openbabel/obfunctions.h",
        "include/openbabel/obiter.h",
        "include/openbabel/obutil.h",
        "include/openbabel/parsmart.h",
        "include/openbabel/phmodel.h",
        "include/openbabel/query.h",
        "include/openbabel/rotamer.h",
        "include/openbabel/rotor.h",
        "include/openbabel/residue.h",
        "include/openbabel/ring.h",
        "include/openbabel/shared_ptr.h",
        "include/openbabel/stereo/cistrans.h",
        "include/openbabel/stereo/squareplanar.h",
        "include/openbabel/stereo/stereo.h",
        "include/openbabel/stereo/tetrahedral.h",
        "include/openbabel/stereo/tetraplanar.h",
        "include/openbabel/stereo/tetranonplanar.h",
        "include/openbabel/typer.h",
    ],
    deps = [
        ":base",
        ":bitvec",
        ":elements",
        ":format",
        ":locale",
        ":rand",
        ":obconversion",
        ":oberror",
        ":op",
    ],
    alwayslink = 1,
)

ob_cc_library(
    name = "obmolecformat",
    srcs = ["src/obmolecformat.cpp"],
    hdrs = [
        "include/openbabel/obmolecformat.h",
        "include/openbabel/reaction.h",
    ],
    deps = [
        ":core",
    ],
)

ob_cc_library(
    name = "formats",
    srcs = [
        "src/formats/mdlvalence.h",
        "src/formats/mdlformat.cpp",
        "src/formats/mol2format.cpp",
        "src/formats/pdbqtformat.cpp",
    ],
    deps = [
        ":core",
        ":obmolecformat",
    ],
    alwayslink = 1,
)

