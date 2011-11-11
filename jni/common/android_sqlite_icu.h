#ifndef __SQLITE_H__
#define __SQLITE_H__

#include <jni.h>
#include "sqlite3.h"
#include <android/log.h>

#define LOG_TAG "android_sqlite_icu"
#ifdef ANDROID
#  define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#  define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#else
#  define QUOTEME_(x) #x
#  define QUOTEME(x) QUOTEME_(x)
#  define LOGI(...) printf("I/" LOG_TAG " (" __FILE__ ":" QUOTEME(__LINE__) "): " __VA_ARGS__)
#  define LOGE(...) printf("E/" LOG_TAG "(" ")" __VA_ARGS__)
#endif

void throw_sqlite3_exception(JNIEnv* env, sqlite3* handle, int errcode);

#endif
