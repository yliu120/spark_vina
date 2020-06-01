#include "java/jni/jni_utils.h"

namespace jni {

std::string JStringToString(JNIEnv* env, jstring j_str) {
  if (j_str == nullptr) return "";
  const char* chars = env->GetStringUTFChars(j_str, nullptr);
  std::string result(chars);
  env->ReleaseStringUTFChars(j_str, chars);
  return result;
}

jbyteArray StringToJByteArray(JNIEnv* env, const std::string& str) {
  jbyteArray arr = env->NewByteArray(str.size());
  env->SetByteArrayRegion(arr, 0, str.size(),
                          reinterpret_cast<const jbyte*>(str.data()));
  return arr;
}

jclass GetArrayListClass(JNIEnv* env) {
  static const jclass kArrayListClass = static_cast<jclass>(
      env->NewGlobalRef(env->FindClass("java/util/ArrayList")));
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