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
// org.spark.tools.ZincUtils.getMetadataFromSmileString
JNIEXPORT jbyteArray JNICALL
Java_org_spark_tools_ZincUtils_getMetadataFromSmileString(
    JNIEnv* env, jobject clazz, jstring smile_string) {
  Compound compound =
      zinc::GetMetadataFromSmileString(JStringToString(env, smile_string));
  return jni::StringToJByteArray(env, compound.SerializeAsString());
}

}  // extern "C"