// This file wraps the native C++ zinc utils library.

#include <jni.h>

#include "cc/zinc/utils.h"
#include "java/jni/jni_utils.h"
#include "protos/compound.pb.h"

using ::jni::JStringToString;

extern "C" {

// Corresponds to Java function:
// org.spark.tools.ZincUtils.convertMol2StringToPdbqtCompoundBytes
JNIEXPORT jbyteArray JNICALL
Java_org_spark_tools_ZincUtils_convertMol2StringToPdbqtCompoundBytes(
    JNIEnv* env, jobject clazz, jstring mol2_string) {
  Compound compound =
      zinc::ConvertMol2StringToPdbqtCompound(JStringToString(env, mol2_string));
  return jni::StringToJByteArray(env, compound.SerializeAsString());
}

// Corresponds to Java function:
// org.spark.tools.ZincUtils.getMetadataBytesFromSmileString
JNIEXPORT jbyteArray JNICALL
Java_org_spark_tools_ZincUtils_getMetadataBytesFromSmileString(
    JNIEnv* env, jobject clazz, jstring smile_string) {
  Compound compound =
      zinc::GetMetadataFromSmileString(JStringToString(env, smile_string));
  return jni::StringToJByteArray(env, compound.SerializeAsString());
}

// Corresponds to Java function:
// org.spark.tools.ZincUtils.getMetadataBytesFromMol2String
JNIEXPORT jbyteArray JNICALL
Java_org_spark_tools_ZincUtils_getMetadataBytesFromMol2String(
    JNIEnv* env, jobject clazz, jstring mol2_string) {
  Compound compound =
      zinc::GetMetadataFromMol2String(JStringToString(env, mol2_string));
  return jni::StringToJByteArray(env, compound.SerializeAsString());
}

}  // extern "C"