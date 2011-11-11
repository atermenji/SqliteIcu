#include "sqlite3.h"
#include "android_sqlite_icu.h"

void throw_sqlite3_exception(JNIEnv* env, sqlite3* handle, int errcode) {
	if (SQLITE_OK == errcode) {
		errcode = sqlite3_errcode(handle);
	}

	const char* errmsg = sqlite3_errmsg(handle);

	jclass exClass = (*env)->FindClass(env, "com/mlsdev/sqlite/SQLiteIcuException");

	(*env)->ThrowNew(env, exClass, errmsg);
}
