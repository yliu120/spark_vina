#include "jni/jni_utils.h"

namespace jni {

std::string JStringToString(JNIEnv* env, jstring j_str) {
  const char* chars = env->GetStringUTFChars(j_str, nullptr);
  std::string result(chars);
  env->ReleaseStringUTFChars(j_str, chars);
  return result;
}

jclass GetArrayListClass(JNIEnv* env) {
  static const jclass kArrayListClass = env->FindClass("java/util/ArrayList");
  return kArrayListClass;
}

jmethodID GetArrayListMethodInit(JNIEnv* env) {
  static const jmethodID kArrayListMethodInit =
      env->GetMethodID(GetArrayListClass(env), "<init>", "(I)V");
  return kArrayListMethodInit;
}

jmethodID GetArrayListMethodAdd(JNIEnv* env) {
  static const jmethodID kArrayListMethodAdd =
      env->GetMethodID(GetArrayListClass(env), "add", "(Ljava/lang/Object;)Z");
  return kArrayListMethodAdd;
}

}  // namespace jni