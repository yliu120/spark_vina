// This file wraps the native C++ vina tools library.

#include <jni.h>
#include <string>
#include <vector>

#include "java/jni/jni_utils.h"
#include "vina.h"

using ::jni::JStringToString;

extern "C" {
// Corresponding to VinaDock.nativeCreate
JNIEXPORT jobject JNICALL Java_org_spark_vina_VinaTools_readLigandsToStrings(
    JNIEnv* env, jobject clazz, jstring ligand_path) {
  std::vector<std::string> cc_results =
      read_ligand_to_strings(JStringToString(env, ligand_path));
  // Packs cc results to Java objects and return.
  jobject result = env->NewObject(jni::GetArrayListClass(env),
                                  jni::GetArrayListMethodInit(env),
                                  static_cast<jint>(cc_results.size()));
  for (const auto& result_str : cc_results) {
    env->CallObjectMethod(result, jni::GetArrayListMethodAdd(env),
                          env->NewStringUTF(result_str.c_str()));
  }
  return result;
}
}  // extern "C"