package org.spark_vina.util;

import static com.google.protobuf.TextFormat.merge;
import static com.google.common.truth.Truth.assertThat;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.truth.extensions.proto.ProtoTruth;
import com.google.protobuf.TextFormat.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.spark_vina.CompoundProtos.Compound;

@RunWith(JUnit4.class)
public final class PdbqtParserHelperTest {

  @Test
  public void parsePdbqtSuccessfully() throws ParseException {
    final String testPdbqtString =
        Joiner.on("\n")
            .join(
                ImmutableList.of(
                    "MODEL       54",
                    "REMARK  Name = ZINC000008419115",
                    "REMARK                            x       y       z     vdW  Elec       q    Type",
                    "REMARK                         _______ _______ _______ _____ _____    ______ ____",
                    "ROOT",
                    "ATOM      1  C   LIG    1       -0.019   1.526   0.010  0.00  0.00    +0.060 C",
                    "ATOM      2  C   LIG    1        0.002  -0.004   0.002  0.00  0.00    +0.180 C",
                    "ATOM      3  O   LIG    1        0.616  -0.481  -1.218  0.00  0.00    -0.740 OA",
                    "ATOM      4  P   LIG    1        1.675   0.378  -2.046  0.00  0.00    +2.270 P",
                    "ATOM      5  O   LIG    1        2.928   0.520  -1.026  0.00  0.00    -1.090 OA",
                    "ATOM      6  P   LIG    1        3.344   1.471   0.176  0.00  0.00    +2.260 P",
                    "ATOM      7  O   LIG    1        1.986   1.643   1.069  0.00  0.00    -0.750 OA",
                    "ENDROOT",
                    "BRANCH  13  14"));
    final String expectedResultString =
        Joiner.on("\n")
            .join(
                ImmutableList.of(
                    "name: 'ZINC000008419115'",
                    "molecular_weight: 133",
                    "atom_features {",
                    "  atom_type: PHOSPHORUS",
                    "  count: 2",
                    "}",
                    "atom_features {",
                    "  atom_type: CARBON",
                    "  count: 2",
                    "}",
                    "atom_features {",
                    "  atom_type: OXYGEN",
                    "  count: 3",
                    "}"));
    Compound.Builder expectedResultProtoBuilder = Compound.newBuilder();
    merge(expectedResultString, expectedResultProtoBuilder);

    Optional<Compound> result = PdbqtParserHelper.parseFeaturesFromPdbqtString(testPdbqtString);

    assertThat(result).isPresent();
    ProtoTruth.assertThat(result.get())
        .isEqualTo(expectedResultProtoBuilder.setOriginalPdbqt(testPdbqtString).build());
  }
}
