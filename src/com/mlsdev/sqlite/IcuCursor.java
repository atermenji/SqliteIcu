package com.mlsdev.sqlite;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class IcuCursor {

    public static final int FIELD_TYPE_INT = 1;
    public static final int FIELD_TYPE_FLOAT = 2;
    public static final int FIELD_TYPE_STRING = 3;
    public static final int FIELD_TYPE_BYTEBUFFER = 4;
    public static final int FIELD_TYPE_NULL = 5;

    private IcuPreparedStatement mPreparedStatement;

    private boolean mInRow = false;
    private HashMap<String, Integer> mColumnNames = new HashMap<String, Integer>();

    public IcuCursor(IcuPreparedStatement statement, String[] names) {
	mPreparedStatement = statement;

	for (int i = 0; i < names.length; i++)
	    mColumnNames.put(names[i], Integer.valueOf(i));
    }

    public Iterable<String> fields() {
	return mColumnNames.keySet();
    }

    public int columnIndex(String column) throws SQLiteIcuException {
	Integer colIndex = (Integer) mColumnNames.get(column);

	if (colIndex == null)
	    throw new SQLiteIcuException();

	return colIndex.intValue();
    }

    public boolean isNull(String column) throws SQLiteIcuException {
	return isNull(columnIndex(column));
    }

    public int intValue(String column) throws SQLiteIcuException {
	return intValue(columnIndex(column));
    }

    public String stringValue(String column) throws SQLiteIcuException {
	return stringValue(columnIndex(column));
    }

    public double doubleValue(String column) throws SQLiteIcuException {
	return doubleValue(columnIndex(column));
    }

    public ByteBuffer byteBufferValue(String column) throws SQLiteIcuException {
	return byteBufferValue(columnIndex(column));
    }

    public int getTypeOf(String column) throws SQLiteIcuException {
	return getTypeOf(columnIndex(column));
    }

    public Object objectValue(String column) throws SQLiteIcuException {
	return objectValue(columnIndex(column));
    }

    public boolean isNull(int columnIndex) throws SQLiteIcuException {
	checkRow();

	return columnIsNull(mPreparedStatement.getStatementHandle(), columnIndex) == 1;
    }

    public int intValue(int columnIndex) throws SQLiteIcuException {
	checkRow();

	return columnIntValue(mPreparedStatement.getStatementHandle(), columnIndex);
    }

    public double doubleValue(int columnIndex) throws SQLiteIcuException {
	checkRow();

	return columnDoubleValue(mPreparedStatement.getStatementHandle(), columnIndex);
    }

    public String stringValue(int columnIndex) throws SQLiteIcuException {
	checkRow();

	return columnStringValue(mPreparedStatement.getStatementHandle(), columnIndex);
    }

    public String columnName(int columnIndex) throws SQLiteIcuException {
	checkRow();

	return columnName(mPreparedStatement.getStatementHandle(), columnIndex);
    }

    public ByteBuffer byteBufferValue(int columnIndex) throws SQLiteIcuException {
	checkRow();

	ByteBuffer buf = columnByteBufferValue(mPreparedStatement.getStatementHandle(), columnIndex);

	if (buf != null)
	    return buf.asReadOnlyBuffer();

	return null;
    }

    public int getTypeOf(int columnIndex) throws SQLiteIcuException {
	checkRow();

	return columnType(mPreparedStatement.getStatementHandle(), columnIndex);
    }

    public Object objectValue(int columnIndex) throws SQLiteIcuException {
	checkRow();

	int type = columnType(mPreparedStatement.getStatementHandle(), columnIndex);

	switch (type) {
	case 1:
	    return Integer.valueOf(intValue(columnIndex));
	case 4:
	    return byteBufferValue(columnIndex);
	case 2:
	    return Double.valueOf(doubleValue(columnIndex));
	case 3:
	    return stringValue(columnIndex);
	}
	
	return null;
    }

    public boolean next() {
	int res = step(mPreparedStatement.getStatementHandle());
	mInRow = (res == 0);
	return mInRow;
    }

    public IcuCursor reset() throws SQLiteIcuException {
	return mPreparedStatement.requery();
    }

    public int getStatementHandle() {
	return mPreparedStatement.getStatementHandle();
    }

    public int count() {
	return columnCount(mPreparedStatement.getStatementHandle());
    }

    public void dispose() throws SQLiteIcuException {
	mPreparedStatement.dispose();
    }

    void checkRow() throws SQLiteIcuException {
	if (!mInRow)
	    throw new SQLiteIcuException("You must call next before");
    }

    native int columnCount(int paramInt);

    native int columnType(int paramInt1, int paramInt2);

    native int columnIsNull(int paramInt1, int paramInt2);

    native int columnIntValue(int paramInt1, int paramInt2);

    native double columnDoubleValue(int paramInt1, int paramInt2);

    native String columnStringValue(int paramInt1, int paramInt2);

    native ByteBuffer columnByteBufferValue(int paramInt1, int paramInt2);

    native String columnName(int paramInt1, int paramInt2);

    native int step(int paramInt);
}
