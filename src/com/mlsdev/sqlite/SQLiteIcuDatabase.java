package com.mlsdev.sqlite;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that implements {@link import android.database.sqlite.SQLiteDatabase} functionality to
 * work with custom SQLite database library, that contains ICU patch.
 * 
 * @author Artur Termenji, MLS-Automatization
 */
public class SQLiteIcuDatabase {

    private final int mSQLiteHandle;
    private final String mOpenedFileName;
    private final Map<String, IcuPreparedStatement> mPreparedMap;
    private boolean mIsOpen = false;

    static {
	ByteBuffer.allocate(0);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private SQLiteIcuDatabase(String fileName) throws SQLiteIcuException {
	mOpenedFileName = fileName;
	mSQLiteHandle = opendb(fileName);
	mIsOpen = true;
	mPreparedMap = new HashMap();
    }

    /**
     * Opens or creates database using SQLite with ICU extension. This method has no flags param,
     * like original method from {@link import android.database.sqlite.SQLiteDatabase}, since
     * database is opened with SQLITE_OPEN_READWRITE | SQLITE_OPEN_CREATE flags in native code. This
     * could be fixed in future.
     */
    public static SQLiteIcuDatabase openDatabase(String path) throws SQLiteIcuException {
	return new SQLiteIcuDatabase(path);
    }

    public int getSQLiteHandle() {
	return mSQLiteHandle;
    }

    public boolean tableExists(String tableName) throws SQLiteIcuException {
	checkOpened();
	String s = "SELECT rowid FROM sqlite_master WHERE type='table' AND name=?;";

	return executeInt(s, new Object[] { tableName }) != null;
    }

    public void execute(String sql, Object[] args) throws SQLiteIcuException {
	checkOpened();

	IcuCursor cursor = query(sql, args);

	try {
	    cursor.next();
	} finally {
	    cursor.dispose();
	}
    }

    public Integer executeInt(String sql, Object[] args) throws SQLiteIcuException {
	checkOpened();

	IcuCursor cursor = query(sql, args);

	try {
	    if (!cursor.next())
		return null;
	    return Integer.valueOf(cursor.intValue(0));
	} finally {
	    cursor.dispose();
	}
    }

    public int executeIntOrThrow(String sql, Object[] args) throws SQLiteIcuException, SQLiteIcuNoRowException {
	checkOpened();

	Integer val = executeInt(sql, args);

	if (val != null) {
	    return val.intValue();
	}

	throw new SQLiteIcuNoRowException();
    }

    public String executeString(String sql, Object[] args) throws SQLiteIcuException {
	checkOpened();

	IcuCursor cursor = query(sql, args);

	try {
	    if (!cursor.next()) {
		return null;
	    }

	    return cursor.stringValue(0);
	} finally {
	    cursor.dispose();
	}
    }

    public IcuCursor query(String sql, Object[] args) throws SQLiteIcuException {
	checkOpened();

	IcuPreparedStatement statement = (IcuPreparedStatement) mPreparedMap.get(sql);

	if (statement == null) {
	    statement = new IcuPreparedStatement(this, sql, false);
	    mPreparedMap.put(sql, statement);
	}

	return statement.query(args);
    }

    public IcuCursor queryFinalized(String sql, Object[] args) throws SQLiteIcuException {
	checkOpened();
	return new IcuPreparedStatement(this, sql, true).query(args);
    }

    public void close() throws SQLiteIcuException {
	if (mIsOpen) {
	    for (IcuPreparedStatement statement : mPreparedMap.values())
		statement.finalizeQuery();

	    closedb(mSQLiteHandle);
	    mIsOpen = false;
	}
    }

    public boolean isOpen() {
	return mIsOpen;
    }

    public void finalize() throws SQLiteIcuException {
	close();
    }

    void checkOpened() throws SQLiteIcuException {
	if (!mIsOpen)
	    throw new SQLiteIcuException("Database closed");
    }

    native int opendb(String paramString) throws SQLiteIcuException;

    native void closedb(int paramInt) throws SQLiteIcuException;
}
