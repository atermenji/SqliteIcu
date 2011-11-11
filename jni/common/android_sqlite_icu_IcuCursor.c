#include <jni.h>
#include "sqlite3.h"
#include <unistd.h>

#include "android_sqlite_icu.h"

int Java_com_mlsdev_sqlite_IcuCursor_step(JNIEnv* env, jobject object, int statementHandle) {
	sqlite3_stmt* handle = (sqlite3_stmt*)statementHandle;

	int errcode = 0 ;

    errcode = sqlite3_step(handle);
	if (errcode==SQLITE_ROW) 
	{
		return 0;
	}else if(errcode == SQLITE_DONE)
	{
		return 1;
	} 
	else if(errcode == SQLITE_BUSY)
	{
		return -1;
	}

	throw_sqlite3_exception(env, sqlite3_db_handle(handle), errcode);
}

int Java_com_mlsdev_sqlite_IcuCursor_columnType(JNIEnv* env, jobject object, int statementHandle, int columnIndex) {
	sqlite3_stmt* handle = (sqlite3_stmt*)statementHandle;

	return sqlite3_column_type(handle, columnIndex);
}

int Java_com_mlsdev_sqlite_IcuCursor_columnIsNull(JNIEnv* env, jobject object, int statementHandle, int columnIndex) {
	sqlite3_stmt* handle = (sqlite3_stmt*)statementHandle;

	int valType = sqlite3_column_type(handle, columnIndex);

	return SQLITE_NULL == valType;
}

int Java_com_mlsdev_sqlite_IcuCursor_columnIntValue(JNIEnv* env, jobject object, int statementHandle, int columnIndex) {
	sqlite3_stmt* handle = (sqlite3_stmt*)statementHandle;

	int valType = sqlite3_column_type(handle, columnIndex);
	if (SQLITE_NULL == valType) {
		return 0;
	}

	return sqlite3_column_int(handle, columnIndex);
}

int Java_com_mlsdev_sqlite_IcuCursor_columnCount(JNIEnv* env, jobject object, int statementHandle) {
	sqlite3_stmt* handle = (sqlite3_stmt*)statementHandle;
	
	return sqlite3_column_count(handle);
}

jdouble Java_com_mlsdev_sqlite_IcuCursor_columnDoubleValue(JNIEnv* env, jobject object, int statementHandle, int columnIndex) {
	sqlite3_stmt* handle = (sqlite3_stmt*)statementHandle;

	int valType = sqlite3_column_type(handle, columnIndex);
	if (SQLITE_NULL == valType) {
		return 0;
	}

	return sqlite3_column_double(handle, columnIndex);
}

jstring Java_com_mlsdev_sqlite_IcuCursor_columnStringValue(JNIEnv* env, jobject object, int statementHandle, int columnIndex) {
	sqlite3_stmt* handle = (sqlite3_stmt*)statementHandle;

	const char* str = sqlite3_column_text(handle, columnIndex);
	if (str) {
		return (*env)->NewStringUTF(env, str);
	}
	else 
	{
		return (*env)->NewStringUTF(env, "");
	}

	return 0;
}

jobject Java_com_mlsdev_sqlite_IcuCursor_columnByteBufferValue(JNIEnv* env, jobject object, int statementHandle, int columnIndex) {
	sqlite3_stmt* handle = (sqlite3_stmt*)statementHandle;

	void *buf = (void*)sqlite3_column_blob(handle, columnIndex);
	int length = sqlite3_column_bytes(handle, columnIndex);

	if (buf != 0 && length > 0 ) {
		return (*env)->NewDirectByteBuffer(env, buf, length);
	}

	return 0;
}

jstring Java_com_mlsdev_sqlite_IcuCursor_columnName(JNIEnv* env, jobject object, int statementHandle, int columnIndex) {
	sqlite3_stmt* handle = (sqlite3_stmt*)statementHandle;
	
	const char* str = sqlite3_column_name(handle, columnIndex);
	if (str) {
		return (*env)->NewStringUTF(env, str);
	}
	else 
	{
		return (*env)->NewStringUTF(env, "");
	}
	
	return 0;
}
/*
	native int columnIntValue(int statementHandle, int columnIndex);
	native String columnStringValue(int statementHandle, int columnIndex);
	native double columnDoubleValue(int statementHandle, int columnIndex);
	native ByteBuffer columnByteBufferValue(int statementHandle, int columnIndex);
	native int step(int statementHandle);
*/
