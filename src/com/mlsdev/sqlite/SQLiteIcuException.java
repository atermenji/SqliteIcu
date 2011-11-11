package com.mlsdev.sqlite;

public class SQLiteIcuException extends Exception {

    private static final long serialVersionUID = -2398298479089615621L;

    private final int mErrorCode;

    public SQLiteIcuException(int errCode, String msg) {
	super(msg);
	mErrorCode = errCode;
    }

    public SQLiteIcuException(String msg) {
	this(0, msg);
    }

    public SQLiteIcuException() {
	mErrorCode = 0;
    }
    
    public int getErrorCode() {
	return mErrorCode;
    }
}
