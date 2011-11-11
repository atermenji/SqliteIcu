#include <jni.h>

#include "sqlite3.h"
#include <math.h>
#include "android_sqlite_icu.h"

#define EARTH_RADIUS    6371.2

static double radians(double degrees){
    return degrees * (M_PI/180.0);
}

static void calculateDistance(sqlite3_context *ctx, int argc, sqlite3_value **argv){
    double distance = 0;
    double lat1 = radians(sqlite3_value_double(argv[0]));
    double lon1 = radians(sqlite3_value_double(argv[1]));
    double lat2 = radians(sqlite3_value_double(argv[2]));
    double lon2 = radians(sqlite3_value_double(argv[3]));
    distance = acos( sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon2 - lon1) );
    distance = (distance < 0 ? distance + M_PI : distance ) * EARTH_RADIUS;
    sqlite3_result_double(ctx, distance);
}

void Java_com_mlsdev_sqlite_SQLiteIcuDatabase_closedb(JNIEnv* env, jobject object, int sqliteHandle) {
	sqlite3* handle = (sqlite3*) sqliteHandle;

	int err = sqlite3_close(handle);

	if (SQLITE_OK != err) {
		throw_sqlite3_exception(env, handle, err);
	}
}

int Java_com_mlsdev_sqlite_SQLiteIcuDatabase_opendb(JNIEnv* env, jobject object, jstring fileName) {
    char const * fileNameStr = (*env)->GetStringUTFChars(env, fileName, 0);

    sqlite3 * handle = 0;

    int err = sqlite3_open_v2(fileNameStr, &handle, SQLITE_OPEN_READWRITE, 0);
    if (SQLITE_OK != err) {
    	throw_sqlite3_exception(env, handle, err);
    }

	sqlite3_create_function(handle, "geodistance", 4, SQLITE_UTF8, NULL, &calculateDistance, NULL, NULL);
    if (fileNameStr != 0) (*env)->ReleaseStringUTFChars(env, fileName, fileNameStr);

    return (int)handle;
}
