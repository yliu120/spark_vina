#ifndef JAVA_JNI_JNI_UTILS_H_
#define JAVA_JNI_JNI_UTILS_H_

#include <jni.h>

#include <string>
#include <vector>

namespace jni {

std::string JStringToString(JNIEnv* env, jstring j_str);

jstring StringToJString(JNIEnv* env, const std::string& str);

jclass GetArrayListClass(JNIEnv* env);

jmethodID GetArrayListMethodInit(JNIEnv* env);

jmethodID GetArrayListMethodAdd(JNIEnv* env);

}  // namespace jni

#endif  // JAVA_JNI_JNI_UTILS_H_