/*
 * Java GPX Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.jpx.jdbc.internal.querily;

import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Row {

	private final ResultSet _rs;

	private Row(final ResultSet result) {
		_rs = requireNonNull(result);
	}


	public boolean next() throws SQLException {
		return _rs.next();
	}

	public void close() throws SQLException {
		_rs.close();
	}

	public boolean wasNull() throws SQLException {
		return _rs.wasNull();
	}

	public String getString(final int columnIndex) throws SQLException {
		return _rs.getString(columnIndex);
	}

	public boolean getBoolean(final int columnIndex) throws SQLException {
		return _rs.getBoolean(columnIndex);
	}

	public byte getByte(final int columnIndex) throws SQLException {
		return _rs.getByte(columnIndex);
	}

	public short getShort(final int columnIndex) throws SQLException {
		return _rs.getShort(columnIndex);
	}

	public int getInt(final int columnIndex) throws SQLException {
		return _rs.getInt(columnIndex);
	}

	public long getLong(final int columnIndex) throws SQLException {
		return _rs.getLong(columnIndex);
	}

	public float getFloat(final int columnIndex) throws SQLException {
		return _rs.getFloat(columnIndex);
	}

	public double getDouble(final int columnIndex) throws SQLException {
		return _rs.getDouble(columnIndex);
	}

	@Deprecated
	public BigDecimal getBigDecimal(final int columnIndex, final int scale)
		throws SQLException
	{
		return _rs.getBigDecimal(columnIndex, scale);
	}

	public byte[] getBytes(final int columnIndex) throws SQLException {
		return _rs.getBytes(columnIndex);
	}

	public Date getDate(final int columnIndex) throws SQLException {
		return _rs.getDate(columnIndex);
	}

	public Time getTime(final int columnIndex) throws SQLException {
		return _rs.getTime(columnIndex);
	}

	public Timestamp getTimestamp(final int columnIndex) throws SQLException {
		return _rs.getTimestamp(columnIndex);
	}

	public InputStream getAsciiStream(final int columnIndex)
		throws SQLException
	{
		return _rs.getAsciiStream(columnIndex);
	}

	@Deprecated
	public InputStream getUnicodeStream(final int columnIndex)
		throws SQLException
	{
		return _rs.getUnicodeStream(columnIndex);
	}

	public InputStream getBinaryStream(final int columnIndex)
		throws SQLException
	{
		return _rs.getBinaryStream(columnIndex);
	}

	public String getString(final String columnLabel) throws SQLException {
		return _rs.getString(columnLabel);
	}

	public boolean getBoolean(final String columnLabel) throws SQLException {
		return _rs.getBoolean(columnLabel);
	}

	public byte getByte(final String columnLabel) throws SQLException {
		return _rs.getByte(columnLabel);
	}

	public short getShort(final String columnLabel) throws SQLException {
		return _rs.getShort(columnLabel);
	}

	public int getInt(final String columnLabel) throws SQLException {
		return _rs.getInt(columnLabel);
	}

	public long getLong(final String columnLabel) throws SQLException {
		return _rs.getLong(columnLabel);
	}

	public float getFloat(final String columnLabel) throws SQLException {
		return _rs.getFloat(columnLabel);
	}

	public double getDouble(final String columnLabel) throws SQLException {
		return _rs.getDouble(columnLabel);
	}

	@Deprecated
	public BigDecimal getBigDecimal(final String columnLabel, final int scale)
		throws SQLException
	{
		return _rs.getBigDecimal(columnLabel, scale);
	}

	public byte[] getBytes(final String columnLabel) throws SQLException {
		return _rs.getBytes(columnLabel);
	}

	public Date getDate(final String columnLabel) throws SQLException {
		return _rs.getDate(columnLabel);
	}

	public Time getTime(final String columnLabel) throws SQLException {
		return _rs.getTime(columnLabel);
	}

	public Timestamp getTimestamp(final String columnLabel) throws SQLException {
		return _rs.getTimestamp(columnLabel);
	}

	public InputStream getAsciiStream(final String columnLabel)
		throws SQLException
	{
		return _rs.getAsciiStream(columnLabel);
	}

	@Deprecated
	public InputStream getUnicodeStream(final String columnLabel)
		throws SQLException
	{
		return _rs.getUnicodeStream(columnLabel);
	}

	public InputStream getBinaryStream(final String columnLabel)
		throws SQLException
	{
		return _rs.getBinaryStream(columnLabel);
	}

	public SQLWarning getWarnings() throws SQLException {
		return _rs.getWarnings();
	}

	public void clearWarnings() throws SQLException {
		_rs.clearWarnings();
	}

	public String getCursorName() throws SQLException {
		return _rs.getCursorName();
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return _rs.getMetaData();
	}

	public Object getObject(final int columnIndex) throws SQLException {
		return _rs.getObject(columnIndex);
	}

	public Object getObject(final String columnLabel) throws SQLException {
		return _rs.getObject(columnLabel);
	}

	public int findColumn(final String columnLabel) throws SQLException {
		return _rs.findColumn(columnLabel);
	}

	public Reader getCharacterStream(final int columnIndex)
		throws SQLException
	{
		return _rs.getCharacterStream(columnIndex);
	}

	public Reader getCharacterStream(final String columnLabel)
		throws SQLException
	{
		return _rs.getCharacterStream(columnLabel);
	}

	public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
		return _rs.getBigDecimal(columnIndex);
	}

	public BigDecimal getBigDecimal(final String columnLabel)
		throws SQLException
	{
		return _rs.getBigDecimal(columnLabel);
	}

	public boolean isBeforeFirst() throws SQLException {
		return _rs.isBeforeFirst();
	}

	public boolean isAfterLast() throws SQLException {
		return _rs.isAfterLast();
	}

	public boolean isFirst() throws SQLException {
		return _rs.isFirst();
	}

	public boolean isLast() throws SQLException {
		return _rs.isLast();
	}

	public void beforeFirst() throws SQLException {
		_rs.beforeFirst();
	}

	public void afterLast() throws SQLException {
		_rs.afterLast();
	}

	public boolean first() throws SQLException {
		return _rs.first();
	}

	public boolean last() throws SQLException {
		return _rs.last();
	}

	public int getRow() throws SQLException {
		return _rs.getRow();
	}

	public boolean absolute(final int row) throws SQLException {
		return _rs.absolute(row);
	}

	public boolean relative(final int rows) throws SQLException {
		return _rs.relative(rows);
	}

	public boolean previous() throws SQLException {
		return _rs.previous();
	}

	public void setFetchDirection(final int direction) throws SQLException {
		_rs.setFetchDirection(direction);
	}

	public int getFetchDirection() throws SQLException {
		return _rs.getFetchDirection();
	}

	public void setFetchSize(final int rows) throws SQLException {
		_rs.setFetchSize(rows);
	}

	public int getFetchSize() throws SQLException {
		return _rs.getFetchSize();
	}

	public int getType() throws SQLException {
		return _rs.getType();
	}

	public int getConcurrency() throws SQLException {
		return _rs.getConcurrency();
	}

	public boolean rowUpdated() throws SQLException {
		return _rs.rowUpdated();
	}

	public boolean rowInserted() throws SQLException {
		return _rs.rowInserted();
	}

	public boolean rowDeleted() throws SQLException {
		return _rs.rowDeleted();
	}

	public void updateNull(final int columnIndex) throws SQLException {
		_rs.updateNull(columnIndex);
	}

	public void updateBoolean(final int columnIndex, final boolean x)
		throws SQLException
	{
		_rs.updateBoolean(columnIndex, x);
	}

	public void updateByte(final int columnIndex, final byte x)
		throws SQLException
	{
		_rs.updateByte(columnIndex, x);
	}

	public void updateShort(final int columnIndex, final short x)
		throws SQLException
	{
		_rs.updateShort(columnIndex, x);
	}

	public void updateInt(final int columnIndex, final int x)
		throws SQLException
	{
		_rs.updateInt(columnIndex, x);
	}

	public void updateLong(final int columnIndex, final long x)
		throws SQLException
	{
		_rs.updateLong(columnIndex, x);
	}

	public void updateFloat(final int columnIndex, final float x)
		throws SQLException
	{
		_rs.updateFloat(columnIndex, x);
	}

	public void updateDouble(final int columnIndex, final double x)
		throws SQLException
	{
		_rs.updateDouble(columnIndex, x);
	}

	public void updateBigDecimal(final int columnIndex, final BigDecimal x)
		throws SQLException
	{
		_rs.updateBigDecimal(columnIndex, x);
	}

	public void updateString(final int columnIndex, final String x)
		throws SQLException
	{
		_rs.updateString(columnIndex, x);
	}

	public void updateBytes(final int columnIndex, final byte[] x)
		throws SQLException
	{
		_rs.updateBytes(columnIndex, x);
	}

	public void updateDate(final int columnIndex, final Date x)
		throws SQLException
	{
		_rs.updateDate(columnIndex, x);
	}

	public void updateTime(final int columnIndex, final Time x)
		throws SQLException
	{
		_rs.updateTime(columnIndex, x);
	}

	public void updateTimestamp(final int columnIndex, final Timestamp x)
		throws SQLException
	{
		_rs.updateTimestamp(columnIndex, x);
	}

	public void updateAsciiStream(
		final int columnIndex,
		final InputStream x,
		final int length
	)
		throws SQLException
	{
		_rs.updateAsciiStream(columnIndex, x, length);
	}

	public void updateBinaryStream(
		final int columnIndex,
		final InputStream x,
		final int length
	)
		throws SQLException
	{
		_rs.updateBinaryStream(columnIndex, x, length);
	}

	public void updateCharacterStream(
		final int columnIndex,
		final Reader x,
		final int length
	)
		throws SQLException
	{
		_rs.updateCharacterStream(columnIndex, x, length);
	}

	public void updateObject(
		final int columnIndex,
		final Object x,
		final int scaleOrLength
	)
		throws SQLException
	{
		_rs.updateObject(columnIndex, x, scaleOrLength);
	}

	public void updateObject(final int columnIndex, final Object x)
		throws SQLException
	{
		_rs.updateObject(columnIndex, x);
	}

	public void updateNull(final String columnLabel) throws SQLException {
		_rs.updateNull(columnLabel);
	}

	public void updateBoolean(final String columnLabel, final boolean x)
		throws SQLException
	{
		_rs.updateBoolean(columnLabel, x);
	}

	public void updateByte(final String columnLabel, final byte x)
		throws SQLException
	{
		_rs.updateByte(columnLabel, x);
	}

	public void updateShort(final String columnLabel, final short x)
		throws SQLException
	{
		_rs.updateShort(columnLabel, x);
	}

	public void updateInt(final String columnLabel, final int x)
		throws SQLException
	{
		_rs.updateInt(columnLabel, x);
	}

	public void updateLong(final String columnLabel, final long x)
		throws SQLException
	{
		_rs.updateLong(columnLabel, x);
	}

	public void updateFloat(final String columnLabel, final float x)
		throws SQLException
	{
		_rs.updateFloat(columnLabel, x);
	}

	public void updateDouble(final String columnLabel, final double x)
		throws SQLException
	{
		_rs.updateDouble(columnLabel, x);
	}

	public void updateBigDecimal(final String columnLabel, final BigDecimal x)
		throws SQLException
	{
		_rs.updateBigDecimal(columnLabel, x);
	}

	public void updateString(final String columnLabel, final String x)
		throws SQLException
	{
		_rs.updateString(columnLabel, x);
	}

	public void updateBytes(final String columnLabel, final byte[] x)
		throws SQLException
	{
		_rs.updateBytes(columnLabel, x);
	}

	public void updateDate(final String columnLabel, final Date x)
		throws SQLException
	{
		_rs.updateDate(columnLabel, x);
	}

	public void updateTime(final String columnLabel, final Time x)
		throws SQLException
	{
		_rs.updateTime(columnLabel, x);
	}

	public void updateTimestamp(final String columnLabel, final Timestamp x)
		throws SQLException
	{
		_rs.updateTimestamp(columnLabel, x);
	}

	public void updateAsciiStream(
		final String columnLabel,
		final InputStream x,
		final int length
	)
		throws SQLException
	{
		_rs.updateAsciiStream(columnLabel, x, length);
	}

	public void updateBinaryStream(
		final String columnLabel,
		final InputStream x,
		final int length
	)
		throws SQLException
	{
		_rs.updateBinaryStream(columnLabel, x, length);
	}

	public void updateCharacterStream(
		final String columnLabel,
		final Reader reader,
		final int length
	)
		throws SQLException
	{
		_rs.updateCharacterStream(columnLabel, reader, length);
	}

	public void updateObject(
		final String columnLabel,
		final Object x,
		final int scaleOrLength
	)
		throws SQLException
	{
		_rs.updateObject(columnLabel, x, scaleOrLength);
	}

	public void updateObject(final String columnLabel, final Object x)
		throws SQLException
	{
		_rs.updateObject(columnLabel, x);
	}

	public void insertRow() throws SQLException {
		_rs.insertRow();
	}

	public void updateRow() throws SQLException {
		_rs.updateRow();
	}

	public void deleteRow() throws SQLException {
		_rs.deleteRow();
	}

	public void refreshRow() throws SQLException {
		_rs.refreshRow();
	}

	public void cancelRowUpdates() throws SQLException {
		_rs.cancelRowUpdates();
	}

	public void moveToInsertRow() throws SQLException {
		_rs.moveToInsertRow();
	}

	public void moveToCurrentRow() throws SQLException {
		_rs.moveToCurrentRow();
	}

	public Statement getStatement() throws SQLException {
		return _rs.getStatement();
	}

	public Object getObject(
		final int columnIndex,
		final Map<String, Class<?>> map
	)
		throws SQLException
	{
		return _rs.getObject(columnIndex, map);
	}

	public Ref getRef(final int columnIndex) throws SQLException {
		return _rs.getRef(columnIndex);
	}

	public Blob getBlob(final int columnIndex) throws SQLException {
		return _rs.getBlob(columnIndex);
	}

	public Clob getClob(final int columnIndex) throws SQLException {
		return _rs.getClob(columnIndex);
	}

	public Array getArray(final int columnIndex) throws SQLException {
		return _rs.getArray(columnIndex);
	}

	public Object getObject(
		final String columnLabel,
		final Map<String, Class<?>> map
	)
		throws SQLException
	{
		return _rs.getObject(columnLabel, map);
	}

	public Ref getRef(final String columnLabel) throws SQLException {
		return _rs.getRef(columnLabel);
	}

	public Blob getBlob(final String columnLabel) throws SQLException {
		return _rs.getBlob(columnLabel);
	}

	public Clob getClob(final String columnLabel) throws SQLException {
		return _rs.getClob(columnLabel);
	}

	public Array getArray(final String columnLabel) throws SQLException {
		return _rs.getArray(columnLabel);
	}

	public Date getDate(final int columnIndex, final Calendar cal)
		throws SQLException
	{
		return _rs.getDate(columnIndex, cal);
	}

	public Date getDate(final String columnLabel, final Calendar cal)
		throws SQLException
	{
		return _rs.getDate(columnLabel, cal);
	}

	public Time getTime(final int columnIndex, final Calendar cal)
		throws SQLException
	{
		return _rs.getTime(columnIndex, cal);
	}

	public Time getTime(final String columnLabel, final Calendar cal)
		throws SQLException
	{
		return _rs.getTime(columnLabel, cal);
	}

	public Timestamp getTimestamp(final int columnIndex, final Calendar cal)
		throws SQLException
	{
		return _rs.getTimestamp(columnIndex, cal);
	}

	public Timestamp getTimestamp(final String columnLabel, final Calendar cal)
		throws SQLException
	{
		return _rs.getTimestamp(columnLabel, cal);
	}

	public URL getURL(final int columnIndex) throws SQLException {
		return _rs.getURL(columnIndex);
	}

	public URL getURL(final String columnLabel) throws SQLException {
		return _rs.getURL(columnLabel);
	}

	public void updateRef(final int columnIndex, final Ref x)
		throws SQLException
	{
		_rs.updateRef(columnIndex, x);
	}

	public void updateRef(final String columnLabel, final Ref x)
		throws SQLException
	{
		_rs.updateRef(columnLabel, x);
	}

	public void updateBlob(final int columnIndex, final Blob x)
		throws SQLException
	{
		_rs.updateBlob(columnIndex, x);
	}

	public void updateBlob(final String columnLabel, final Blob x)
		throws SQLException
	{
		_rs.updateBlob(columnLabel, x);
	}

	public void updateClob(final int columnIndex, final Clob x)
		throws SQLException
	{
		_rs.updateClob(columnIndex, x);
	}

	public void updateClob(final String columnLabel, final Clob x)
		throws SQLException
	{
		_rs.updateClob(columnLabel, x);
	}

	public void updateArray(final int columnIndex, final Array x)
		throws SQLException
	{
		_rs.updateArray(columnIndex, x);
	}

	public void updateArray(final String columnLabel, final Array x)
		throws SQLException
	{
		_rs.updateArray(columnLabel, x);
	}

	public RowId getRowId(final int columnIndex) throws SQLException {
		return _rs.getRowId(columnIndex);
	}

	public RowId getRowId(final String columnLabel) throws SQLException {
		return _rs.getRowId(columnLabel);
	}

	public void updateRowId(final int columnIndex, final RowId x)
		throws SQLException
	{
		_rs.updateRowId(columnIndex, x);
	}

	public void updateRowId(final String columnLabel, final RowId x)
		throws SQLException
	{
		_rs.updateRowId(columnLabel, x);
	}

	public int getHoldability() throws SQLException {
		return _rs.getHoldability();
	}

	public boolean isClosed() throws SQLException {
		return _rs.isClosed();
	}

	public void updateNString(final int columnIndex, final String nString)
		throws SQLException
	{
		_rs.updateNString(columnIndex, nString);
	}

	public void updateNString(final String columnLabel, final String nString)
		throws SQLException
	{
		_rs.updateNString(columnLabel, nString);
	}

	public void updateNClob(final int columnIndex, final NClob nClob)
		throws SQLException
	{
		_rs.updateNClob(columnIndex, nClob);
	}

	public void updateNClob(final String columnLabel, final NClob nClob)
		throws SQLException
	{
		_rs.updateNClob(columnLabel, nClob);
	}

	public NClob getNClob(final int columnIndex) throws SQLException {
		return _rs.getNClob(columnIndex);
	}

	public NClob getNClob(final String columnLabel) throws SQLException {
		return _rs.getNClob(columnLabel);
	}

	public SQLXML getSQLXML(final int columnIndex) throws SQLException {
		return _rs.getSQLXML(columnIndex);
	}

	public SQLXML getSQLXML(final String columnLabel) throws SQLException {
		return _rs.getSQLXML(columnLabel);
	}

	public void updateSQLXML(final int columnIndex, final SQLXML xmlObject)
		throws SQLException
	{
		_rs.updateSQLXML(columnIndex, xmlObject);
	}

	public void updateSQLXML(final String columnLabel, final SQLXML xmlObject)
		throws SQLException
	{
		_rs.updateSQLXML(columnLabel, xmlObject);
	}

	public String getNString(final int columnIndex) throws SQLException {
		return _rs.getNString(columnIndex);
	}

	public String getNString(final String columnLabel) throws SQLException {
		return _rs.getNString(columnLabel);
	}

	public Reader getNCharacterStream(final int columnIndex)
		throws SQLException
	{
		return _rs.getNCharacterStream(columnIndex);
	}

	public Reader getNCharacterStream(final String columnLabel)
		throws SQLException
	{
		return _rs.getNCharacterStream(columnLabel);
	}

	public void updateNCharacterStream(
		final int columnIndex,
		final Reader x,
		final long length
	)
		throws SQLException
	{
		_rs.updateNCharacterStream(columnIndex, x, length);
	}

	public void updateNCharacterStream(
		final String columnLabel,
		final Reader reader,
		final long length
	)
		throws SQLException
	{
		_rs.updateNCharacterStream(columnLabel, reader, length);
	}

	public void updateAsciiStream(
		final int columnIndex,
		final InputStream x,
		final long length
	)
		throws SQLException
	{
		_rs.updateAsciiStream(columnIndex, x, length);
	}

	public void updateBinaryStream(
		final int columnIndex,
		final InputStream x,
		final long length
	)
		throws SQLException
	{
		_rs.updateBinaryStream(columnIndex, x, length);
	}

	public void updateCharacterStream(
		final int columnIndex,
		final Reader x,
		final long length
	)
		throws SQLException
	{
		_rs.updateCharacterStream(columnIndex, x, length);
	}

	public void updateAsciiStream(
		final String columnLabel,
		final InputStream x,
		final long length
	)
		throws SQLException
	{
		_rs.updateAsciiStream(columnLabel, x, length);
	}

	public void updateBinaryStream(
		final String columnLabel,
		final InputStream x,
		final long length
	)
		throws SQLException
	{
		_rs.updateBinaryStream(columnLabel, x, length);
	}

	public void updateCharacterStream(
		final String columnLabel,
		final Reader reader,
		final long length
	)
		throws SQLException
	{
		_rs.updateCharacterStream(columnLabel, reader, length);
	}

	public void updateBlob(
		final int columnIndex,
		final InputStream inputStream,
		final long length
	)
		throws SQLException
	{
		_rs.updateBlob(columnIndex, inputStream, length);
	}

	public void updateBlob(
		final String columnLabel,
		final InputStream inputStream,
		final long length
	)
		throws SQLException
	{
		_rs.updateBlob(columnLabel, inputStream, length);
	}

	public void updateClob(
		final int columnIndex,
		final Reader reader,
		final long length
	)
		throws SQLException
	{
		_rs.updateClob(columnIndex, reader, length);
	}

	public void updateClob(
		final String columnLabel,
		final Reader reader,
		final long length
	)
		throws SQLException
	{
		_rs.updateClob(columnLabel, reader, length);
	}

	public void updateNClob(
		final int columnIndex,
		final Reader reader,
		final long length
	)
		throws SQLException
	{
		_rs.updateNClob(columnIndex, reader, length);
	}

	public void updateNClob(
		final String columnLabel,
		final Reader reader,
		final long length
	)
		throws SQLException
	{
		_rs.updateNClob(columnLabel, reader, length);
	}

	public void updateNCharacterStream(final int columnIndex, final Reader x)
		throws SQLException
	{
		_rs.updateNCharacterStream(columnIndex, x);
	}

	public void updateNCharacterStream(
		final String columnLabel,
		final Reader reader
	)
		throws SQLException
	{
		_rs.updateNCharacterStream(columnLabel, reader);
	}

	public void updateAsciiStream(final int columnIndex, final InputStream x)
		throws SQLException
	{
		_rs.updateAsciiStream(columnIndex, x);
	}

	public void updateBinaryStream(final int columnIndex, final InputStream x)
		throws SQLException
	{
		_rs.updateBinaryStream(columnIndex, x);
	}

	public void updateCharacterStream(final int columnIndex, final Reader x)
		throws SQLException
	{
		_rs.updateCharacterStream(columnIndex, x);
	}

	public void updateAsciiStream(final String columnLabel, final InputStream x)
		throws SQLException
	{
		_rs.updateAsciiStream(columnLabel, x);
	}

	public void updateBinaryStream(final String columnLabel, final InputStream x)
		throws SQLException
	{
		_rs.updateBinaryStream(columnLabel, x);
	}

	public void updateCharacterStream(
		final String columnLabel,
		final Reader reader
	)
		throws SQLException
	{
		_rs.updateCharacterStream(columnLabel, reader);
	}

	public void updateBlob(final int columnIndex, final InputStream inputStream)
		throws SQLException
	{
		_rs.updateBlob(columnIndex, inputStream);
	}

	public void updateBlob(
		final String columnLabel,
		final InputStream inputStream
	)
		throws SQLException
	{
		_rs.updateBlob(columnLabel, inputStream);
	}

	public void updateClob(final int columnIndex, final Reader reader)
		throws SQLException
	{
		_rs.updateClob(columnIndex, reader);
	}

	public void updateClob(final String columnLabel, final Reader reader)
		throws SQLException
	{
		_rs.updateClob(columnLabel, reader);
	}

	public void updateNClob(final int columnIndex, final Reader reader)
		throws SQLException
	{
		_rs.updateNClob(columnIndex, reader);
	}

	public void updateNClob(final String columnLabel, final Reader reader)
		throws SQLException
	{
		_rs.updateNClob(columnLabel, reader);
	}

	public <T> T getObject(final int columnIndex, final Class<T> type)
		throws SQLException
	{
		return _rs.getObject(columnIndex, type);
	}

	public <T> T getObject(final String columnLabel, final Class<T> type)
		throws SQLException
	{
		return _rs.getObject(columnLabel, type);
	}

	public void updateObject(
		final int columnIndex,
		final Object x,
		final SQLType targetSqlType,
		final int scaleOrLength
	)
		throws SQLException
	{
		_rs.updateObject(columnIndex, x, targetSqlType, scaleOrLength);
	}

	public void updateObject(
		final String columnLabel,
		final Object x,
		final SQLType targetSqlType,
		final int scaleOrLength
	)
		throws SQLException
	{
		_rs.updateObject(columnLabel, x, targetSqlType, scaleOrLength);
	}

	public void updateObject(
		final int columnIndex,
		final Object x,
		final SQLType targetSqlType
	)
		throws SQLException
	{
		_rs.updateObject(columnIndex, x, targetSqlType);
	}

	public void updateObject(
		final String columnLabel,
		final Object x,
		final SQLType targetSqlType
	)
		throws SQLException
	{
		_rs.updateObject(columnLabel, x, targetSqlType);
	}

	public <T> T unwrap(final Class<T> iface) throws SQLException {
		return _rs.unwrap(iface);
	}

	public boolean isWrapperFor(final Class<?> iface) throws SQLException {
		return _rs.isWrapperFor(iface);
	}
	static Row of(final ResultSet result) {
		return new Row(result);
	}

}
