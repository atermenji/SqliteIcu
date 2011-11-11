package com.mlsdev.sqlite;

import java.nio.ByteBuffer;

public class IcuPreparedStatement {

    private SQLiteIcuDatabase mDatabase;
    private boolean mIsFinalized = false;

    private int mSQLiteStatementHandle;
    private String mQuerySql;
    public int queryArgsCount;
    private boolean mFinalizeAfterQuery = false;

    public IcuPreparedStatement(SQLiteIcuDatabase db, String sql, boolean finalize) throws SQLiteIcuException {
	mDatabase = db;
	mQuerySql = sql;
	mFinalizeAfterQuery = finalize;

	mSQLiteStatementHandle = prepare(mDatabase.getSQLiteHandle(), sql);
    }

    public int getStatementHandle() {
	return mSQLiteStatementHandle;
    }

    //TODO this should really be refactored
    public IcuCursor query(Object[] args) throws SQLiteIcuException {
	if ((args == null) || (args.length != queryArgsCount))
	    throw new IllegalArgumentException();

	checkFinalized();

	String[] names = reset(mSQLiteStatementHandle);

	int i = 1;
	for (Object obj : args) {
	    if (obj == null) {
		bindNull(mSQLiteStatementHandle, i);
	    } else if ((obj instanceof Integer)) {
		bindInt(mSQLiteStatementHandle, i, ((Integer) obj).intValue());
	    } else if ((obj instanceof Double)) {
		bindDouble(mSQLiteStatementHandle, i, ((Double) obj).doubleValue());
	    } else if ((obj instanceof String)) {
		bindString(mSQLiteStatementHandle, i, (String) obj);
	    } else if ((obj instanceof ByteBuffer)) {
		ByteBuffer buf = (ByteBuffer) obj;
		
		if (!buf.isDirect()) 
		    throw new IllegalArgumentException("Only direct ByteBuffers are supported");
		
		bindByteBuffer(mSQLiteStatementHandle, i, (ByteBuffer) obj);
	    } else {
		throw new IllegalArgumentException();
	    }
	    
	    i++;
	}

	return new IcuCursor(this, names);
    }

    public IcuCursor requery() throws SQLiteIcuException {
	checkFinalized();

	String[] names = reset(mSQLiteStatementHandle);

	return new IcuCursor(this, names);
    }

    public void dispose() throws SQLiteIcuException {
	if (mFinalizeAfterQuery) {
	    finalizeQuery();
	}
    }

    void checkFinalized() throws SQLiteIcuException {
	if (mIsFinalized)
	    throw new SQLiteIcuException("Prepared query finalized");
    }

    public void finalizeQuery() throws SQLiteIcuException {
	mIsFinalized = true;
	finalize(mSQLiteStatementHandle);
    }

    native void bindByteBuffer(int paramInt1, int paramInt2, ByteBuffer paramByteBuffer) throws SQLiteIcuException;

    native void bindString(int paramInt1, int paramInt2, String paramString) throws SQLiteIcuException;

    native void bindInt(int paramInt1, int paramInt2, int paramInt3) throws SQLiteIcuException;

    native void bindDouble(int paramInt1, int paramInt2, double paramDouble) throws SQLiteIcuException;

    native void bindNull(int paramInt1, int paramInt2) throws SQLiteIcuException;

    native String[] reset(int paramInt) throws SQLiteIcuException;

    native int prepare(int paramInt, String paramString) throws SQLiteIcuException;

    native void finalize(int paramInt) throws SQLiteIcuException;
}
