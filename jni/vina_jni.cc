// This file wraps the native C++ vina library.

#include <jni.h>
#include <memory>
#include <string>

#include "vina.h"

namespace {

std::string ToString(JNIEnv* env, jstring j_str) {
  const char* chars = env->GetStringUTFChars(j_str, nullptr);
  std::string result(chars);
  env->ReleaseStringUTFChars(j_str, chars);
  return result;
}
  
}  // namespace

extern "C" {
// Corresponding to VinaDock.nativeCreate
JNIEXPORT jlong JNICALL
Java_org_spark_vina_VinaDock_nativeCreate(
    JNIEnv* env, jobject clazz, jstring receptor_path, jdouble center_x,
    jdouble center_y, jdouble center_z, jdouble size_x, jdouble size_y,
    jdouble size_z, jint cpu, jint num_modes) {
  auto vina_dock = std::make_unique<VinaDock>(
      ToString(env, receptor_path), center_x, center_y, center_z, size_x,
      size_y, size_z, cpu, num_modes);
  return reinterpret_cast<jlong>(vina_dock.release());
}

// Corresponding to VinaDock.nativeVinaFit
JNIEXPORT void JNICALL
Java_org_spark_vina_VinaDock_nativeDelete(
    JNIEnv* env, jobject clazz, jlong handle) {
  if (handle == 0) return;
  delete reinterpret_cast<VinaDock*>(handle);
}
}  // extern "C"