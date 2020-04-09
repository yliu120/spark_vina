// This file wraps the native C++ vina library.

#include <jni.h>
#include <memory>
#include <string>
#include <vector>

#include "vina.h"
#include "vina.pb.h"

namespace {

std::string ToString(JNIEnv *env, jstring j_str) {
  const char *chars = env->GetStringUTFChars(j_str, nullptr);
  std::string result(chars);
  env->ReleaseStringUTFChars(j_str, chars);
  return result;
}

jclass GetArrayListClass(JNIEnv *env) {
  static const jclass kArrayListClass = env->FindClass("java/util/ArrayList");
  return kArrayListClass;
}

jmethodID GetArrayListMethodInit(JNIEnv *env) {
  static const jmethodID kArrayListMethodInit =
      env->GetMethodID(GetArrayListClass(env), "<init>", "(I)V");
  return kArrayListMethodInit;
}

jmethodID GetArrayListMethodAdd(JNIEnv *env) {
  static const jmethodID kArrayListMethodAdd =
      env->GetMethodID(GetArrayListClass(env), "add", "(Ljava/lang/Object;)Z");
  return kArrayListMethodAdd;
}
} // namespace

extern "C" {
// Corresponding to VinaDock.nativeCreate
JNIEXPORT jlong JNICALL Java_org_spark_vina_VinaDock_nativeCreate(
    JNIEnv *env, jobject clazz, jstring receptor_path, jdouble center_x,
    jdouble center_y, jdouble center_z, jdouble size_x, jdouble size_y,
    jdouble size_z, jint cpu, jint num_modes) {
  auto vina_dock = std::make_unique<VinaDock>(
      ToString(env, receptor_path), center_x, center_y, center_z, size_x,
      size_y, size_z, cpu, num_modes);
  return reinterpret_cast<jlong>(vina_dock.release());
}

// Corresponding to VinaDock.nativeVinaFit
JNIEXPORT jobject JNICALL Java_org_spark_vina_VinaDock_nativeVinaFit(
    JNIEnv *env, jobject clazz, jlong handle, jobjectArray ligand_string_array,
    jdouble filter_limit) {
  // Parse ligand_string_array to std::vector<std::string>
  std::vector<std::string> cc_ligand_strings;
  jsize size = env->GetArrayLength(ligand_string_array);
  for (int i = 0; i < size; i++) {
    jstring ligand_string = static_cast<jstring>(
        env->GetObjectArrayElement(ligand_string_array, i));
    cc_ligand_strings.push_back(ToString(env, ligand_string));
    env->DeleteLocalRef(ligand_string);
  }

  std::vector<VinaResult> docking_results =
      reinterpret_cast<VinaDock *>(handle)->vina_fit(cc_ligand_strings,
                                                     filter_limit);

  // Pack results into jobject (List<byte>)
  jobject result =
      env->NewObject(GetArrayListClass(env), GetArrayListMethodInit(env),
                     static_cast<jint>(docking_results.size()));
  for (const auto &docking_result : docking_results) {
    std::string serialized_result = docking_result.SerializeAsString();
    jbyteArray docking_result_jbytes =
        env->NewByteArray(serialized_result.size());
    env->SetByteArrayRegion(
        docking_result_jbytes, 0, serialized_result.size(),
        reinterpret_cast<const jbyte*>(serialized_result.data()));
    env->CallObjectMethod(result, GetArrayListMethodAdd(env),
                          docking_result_jbytes);
  }
  return result;
}

// Corresponding to VinaDock.nativeDelete
JNIEXPORT void JNICALL Java_org_spark_vina_VinaDock_nativeDelete(JNIEnv *env,
                                                                 jobject clazz,
                                                                 jlong handle) {
  if (handle == 0)
    return;
  delete reinterpret_cast<VinaDock *>(handle);
}
} // extern "C"