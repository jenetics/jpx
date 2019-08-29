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
package io.jenetics.jpx.jdbc.internal.db;

import static java.time.ZoneOffset.UTC;
import static java.util.Objects.requireNonNull;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
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
import java.time.Duration;
import java.time.Instant;
import java.time.Year;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Map;
import java.util.function.Function;

import io.jenetics.jpx.DGPSStation;
import io.jenetics.jpx.Degrees;
import io.jenetics.jpx.Fix;
import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;
import io.jenetics.jpx.Speed;
import io.jenetics.jpx.UInt;

/**
 * Extends the JDBC {@link ResultSet} with additional useful access methods.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Results implements ResultSet {

	private final ResultSet _rs;

	private Results(final ResultSet rs) {
		_rs = requireNonNull(rs);
	}


	public <T> T get(final Class<T> type, final String columnName)
		throws SQLException
	{
		return type.cast(getObject(columnName));
	}

	public <A, B> B get(
		final Class<A> type,
		final Function<A, B> mapper,
		final String columnName
	)
		throws SQLException
	{
		final A value = get(type, columnName);
		return value != null ? mapper.apply(value) : null;
	}

	public Integer getInteger(final String columnName) throws SQLException {
		return get(Integer.class, columnName);
	}

	public ZonedDateTime getZonedDateTime(final String columnName)
		throws SQLException
	{
		final Timestamp ts = getTimestamp(columnName);
		return ts != null
			? ZonedDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), UTC)
			: null;
	}

	public Year getYear(final String columnName) throws SQLException {
		final Integer value = get(Integer.class, columnName);
		return value != null ? Year.of(value) : null;
	}

	public URI getURI(final String columnName) throws SQLException {
		final String value = getString(columnName);

		URI uri = null;
		if (value != null) {
			try { uri = new URI(value); } catch (URISyntaxException ignore) {}
		}

		return uri;
	}

	public Latitude getLatitude(final String columnName) throws SQLException {
		final Double value = get(Double.class, columnName);
		return value != null ? Latitude.ofDegrees(value) : null;
	}

	public Longitude getLongitude(final String columnName) throws SQLException {
		final Double value = get(Double.class, columnName);
		return value != null ? Longitude.ofDegrees(value) : null;
	}

	public Length getLength(final String columnName) throws SQLException {
		final Double value = get(Double.class, columnName);
		return value != null ? Length.of(value, Length.Unit.METER) : null;
	}

	public Speed getSpeed(final String columnName) throws SQLException {
		final Double value = get(Double.class, columnName);
		return value != null ? Speed.of(value, Speed.Unit.METERS_PER_SECOND) : null;
	}

	public Degrees getDegrees(final String columnName) throws SQLException {
		final Double value = get(Double.class, columnName);
		return value != null ? Degrees.ofDegrees(value) : null;
	}

	public Fix getFix(final String columnName) throws SQLException {
		final String value = getString(columnName);
		return value != null ? Fix.ofName(value).orElse(null) : null;
	}

	public UInt getUInt(final String columnName) throws SQLException {
		final Integer value = get(Integer.class, columnName);
		return value != null ? UInt.of(value) : null;
	}

	public Duration getDuration(final String columnName) throws SQLException {
		final Integer value = get(Integer.class, columnName);
		return value != null ? Duration.ofSeconds(value) : null;
	}

	public DGPSStation getDGPSStation(final String columnName) throws SQLException {
		final Integer value = get(Integer.class, columnName);
		return value != null ? DGPSStation.of(value) : null;
	}


	/* *************************************************************************
	 * ResultSet delegate methods.
	 **************************************************************************/

	@Override
	public boolean next() throws SQLException {
		return _rs.next();
	}

	@Override
	public void close() throws SQLException {
		_rs.close();
	}

	@Override
	public boolean wasNull() throws SQLException {
		return _rs.wasNull();
	}

	@Override
	public String getString(final int columnIndex) throws SQLException {
		return _rs.getString(columnIndex);
	}

	@Override
	public boolean getBoolean(final int columnIndex) throws SQLException {
		return _rs.getBoolean(columnIndex);
	}

	@Override
	public byte getByte(final int columnIndex) throws SQLException {
		return _rs.getByte(columnIndex);
	}

	@Override
	public short getShort(final int columnIndex) throws SQLException {
		return _rs.getShort(columnIndex);
	}

	@Override
	public int getInt(final int columnIndex) throws SQLException {
		return _rs.getInt(columnIndex);
	}

	@Override
	public long getLong(final int columnIndex) throws SQLException {
		return _rs.getLong(columnIndex);
	}

	@Override
	public float getFloat(final int columnIndex) throws SQLException {
		return _rs.getFloat(columnIndex);
	}

	@Override
	public double getDouble(final int columnIndex) throws SQLException {
		return _rs.getDouble(columnIndex);
	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(final int columnIndex, final int scale)
		throws SQLException
	{
		return _rs.getBigDecimal(columnIndex, scale);
	}

	@Override
	public byte[] getBytes(final int columnIndex) throws SQLException {
		return _rs.getBytes(columnIndex);
	}

	@Override
	public Date getDate(final int columnIndex) throws SQLException {
		return _rs.getDate(columnIndex);
	}

	@Override
	public Time getTime(final int columnIndex) throws SQLException {
		return _rs.getTime(columnIndex);
	}

	@Override
	public Timestamp getTimestamp(final int columnIndex) throws SQLException {
		return _rs.getTimestamp(columnIndex);
	}

	@Override
	public InputStream getAsciiStream(final int columnIndex)
		throws SQLException
	{
		return _rs.getAsciiStream(columnIndex);
	}

	@Override
	@Deprecated
	public InputStream getUnicodeStream(final int columnIndex)
		throws SQLException
	{
		return _rs.getUnicodeStream(columnIndex);
	}

	@Override
	public InputStream getBinaryStream(final int columnIndex)
		throws SQLException
	{
		return _rs.getBinaryStream(columnIndex);
	}

	@Override
	public String getString(final String columnLabel) throws SQLException {
		return _rs.getString(columnLabel);
	}

	@Override
	public boolean getBoolean(final String columnLabel) throws SQLException {
		return _rs.getBoolean(columnLabel);
	}

	@Override
	public byte getByte(final String columnLabel) throws SQLException {
		return _rs.getByte(columnLabel);
	}

	@Override
	public short getShort(final String columnLabel) throws SQLException {
		return _rs.getShort(columnLabel);
	}

	@Override
	public int getInt(final String columnLabel) throws SQLException {
		return _rs.getInt(columnLabel);
	}

	@Override
	public long getLong(final String columnLabel) throws SQLException {
		return _rs.getLong(columnLabel);
	}

	@Override
	public float getFloat(final String columnLabel) throws SQLException {
		return _rs.getFloat(columnLabel);
	}

	@Override
	public double getDouble(final String columnLabel) throws SQLException {
		return _rs.getDouble(columnLabel);
	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(final String columnLabel, final int scale)
		throws SQLException
	{
		return _rs.getBigDecimal(columnLabel, scale);
	}

	@Override
	public byte[] getBytes(final String columnLabel) throws SQLException {
		return _rs.getBytes(columnLabel);
	}

	@Override
	public Date getDate(final String columnLabel) throws SQLException {
		return _rs.getDate(columnLabel);
	}

	@Override
	public Time getTime(final String columnLabel) throws SQLException {
		return _rs.getTime(columnLabel);
	}

	@Override
	public Timestamp getTimestamp(final String columnLabel) throws SQLException {
		return _rs.getTimestamp(columnLabel);
	}

	@Override
	public InputStream getAsciiStream(final String columnLabel)
		throws SQLException
	{
		return _rs.getAsciiStream(columnLabel);
	}

	@Override
	@Deprecated
	public InputStream getUnicodeStream(final String columnLabel)
		throws SQLException
	{
		return _rs.getUnicodeStream(columnLabel);
	}

	@Override
	public InputStream getBinaryStream(final String columnLabel)
		throws SQLException
	{
		return _rs.getBinaryStream(columnLabel);
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return _rs.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		_rs.clearWarnings();
	}

	@Override
	public String getCursorName() throws SQLException {
		return _rs.getCursorName();
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return _rs.getMetaData();
	}

	@Override
	public Object getObject(final int columnIndex) throws SQLException {
		return _rs.getObject(columnIndex);
	}

	@Override
	public Object getObject(final String columnLabel) throws SQLException {
		return _rs.getObject(columnLabel);
	}

	@Override
	public int findColumn(final String columnLabel) throws SQLException {
		return _rs.findColumn(columnLabel);
	}

	@Override
	public Reader getCharacterStream(final int columnIndex)
		throws SQLException
	{
		return _rs.getCharacterStream(columnIndex);
	}

	@Override
	public Reader getCharacterStream(final String columnLabel)
		throws SQLException
	{
		return _rs.getCharacterStream(columnLabel);
	}

	@Override
	public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
		return _rs.getBigDecimal(columnIndex);
	}

	@Override
	public BigDecimal getBigDecimal(final String columnLabel)
		throws SQLException
	{
		return _rs.getBigDecimal(columnLabel);
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		return _rs.isBeforeFirst();
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		return _rs.isAfterLast();
	}

	@Override
	public boolean isFirst() throws SQLException {
		return _rs.isFirst();
	}

	@Override
	public boolean isLast() throws SQLException {
		return _rs.isLast();
	}

	@Override
	public void beforeFirst() throws SQLException {
		_rs.beforeFirst();
	}

	@Override
	public void afterLast() throws SQLException {
		_rs.afterLast();
	}

	@Override
	public boolean first() throws SQLException {
		return _rs.first();
	}

	@Override
	public boolean last() throws SQLException {
		return _rs.last();
	}

	@Override
	public int getRow() throws SQLException {
		return _rs.getRow();
	}

	@Override
	public boolean absolute(final int row) throws SQLException {
		return _rs.absolute(row);
	}

	@Override
	public boolean relative(final int rows) throws SQLException {
		return _rs.relative(rows);
	}

	@Override
	public boolean previous() throws SQLException {
		return _rs.previous();
	}

	@Override
	public void setFetchDirection(final int direction) throws SQLException {
		_rs.setFetchDirection(direction);
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return _rs.getFetchDirection();
	}

	@Override
	public void setFetchSize(final int rows) throws SQLException {
		_rs.setFetchSize(rows);
	}

	@Override
	public int getFetchSize() throws SQLException {
		return _rs.getFetchSize();
	}

	@Override
	public int getType() throws SQLException {
		return _rs.getType();
	}

	@Override
	public int getConcurrency() throws SQLException {
		return _rs.getConcurrency();
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		return _rs.rowUpdated();
	}

	@Override
	public boolean rowInserted() throws SQLException {
		return _rs.rowInserted();
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		return _rs.rowDeleted();
	}

	@Override
	public void updateNull(final int columnIndex) throws SQLException {
		_rs.updateNull(columnIndex);
	}

	@Override
	public void updateBoolean(final int columnIndex, final boolean x)
		throws SQLException
	{
		_rs.updateBoolean(columnIndex, x);
	}

	@Override
	public void updateByte(final int columnIndex, final byte x)
		throws SQLException
	{
		_rs.updateByte(columnIndex, x);
	}

	@Override
	public void updateShort(final int columnIndex, final short x)
		throws SQLException
	{
		_rs.updateShort(columnIndex, x);
	}

	@Override
	public void updateInt(final int columnIndex, final int x)
		throws SQLException
	{
		_rs.updateInt(columnIndex, x);
	}

	@Override
	public void updateLong(final int columnIndex, final long x)
		throws SQLException
	{
		_rs.updateLong(columnIndex, x);
	}

	@Override
	public void updateFloat(final int columnIndex, final float x)
		throws SQLException
	{
		_rs.updateFloat(columnIndex, x);
	}

	@Override
	public void updateDouble(final int columnIndex, final double x)
		throws SQLException
	{
		_rs.updateDouble(columnIndex, x);
	}

	@Override
	public void updateBigDecimal(final int columnIndex, final BigDecimal x)
		throws SQLException
	{
		_rs.updateBigDecimal(columnIndex, x);
	}

	@Override
	public void updateString(final int columnIndex, final String x)
		throws SQLException
	{
		_rs.updateString(columnIndex, x);
	}

	@Override
	public void updateBytes(final int columnIndex, final byte[] x)
		throws SQLException
	{
		_rs.updateBytes(columnIndex, x);
	}

	@Override
	public void updateDate(final int columnIndex, final Date x)
		throws SQLException
	{
		_rs.updateDate(columnIndex, x);
	}

	@Override
	public void updateTime(final int columnIndex, final Time x)
		throws SQLException
	{
		_rs.updateTime(columnIndex, x);
	}

	@Override
	public void updateTimestamp(final int columnIndex, final Timestamp x)
		throws SQLException
	{
		_rs.updateTimestamp(columnIndex, x);
	}

	@Override
	public void updateAsciiStream(
		final int columnIndex,
		final InputStream x,
		final int length
	)
		throws SQLException
	{
		_rs.updateAsciiStream(columnIndex, x, length);
	}

	@Override
	public void updateBinaryStream(
		final int columnIndex,
		final InputStream x,
		final int length
	)
		throws SQLException
	{
		_rs.updateBinaryStream(columnIndex, x, length);
	}

	@Override
	public void updateCharacterStream(
		final int columnIndex,
		final Reader x,
		final int length
	)
		throws SQLException
	{
		_rs.updateCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateObject(
		final int columnIndex,
		final Object x,
		final int scaleOrLength
	)
		throws SQLException
	{
		_rs.updateObject(columnIndex, x, scaleOrLength);
	}

	@Override
	public void updateObject(final int columnIndex, final Object x)
		throws SQLException
	{
		_rs.updateObject(columnIndex, x);
	}

	@Override
	public void updateNull(final String columnLabel) throws SQLException {
		_rs.updateNull(columnLabel);
	}

	@Override
	public void updateBoolean(final String columnLabel, final boolean x)
		throws SQLException
	{
		_rs.updateBoolean(columnLabel, x);
	}

	@Override
	public void updateByte(final String columnLabel, final byte x)
		throws SQLException
	{
		_rs.updateByte(columnLabel, x);
	}

	@Override
	public void updateShort(final String columnLabel, final short x)
		throws SQLException
	{
		_rs.updateShort(columnLabel, x);
	}

	@Override
	public void updateInt(final String columnLabel, final int x)
		throws SQLException
	{
		_rs.updateInt(columnLabel, x);
	}

	@Override
	public void updateLong(final String columnLabel, final long x)
		throws SQLException
	{
		_rs.updateLong(columnLabel, x);
	}

	@Override
	public void updateFloat(final String columnLabel, final float x)
		throws SQLException
	{
		_rs.updateFloat(columnLabel, x);
	}

	@Override
	public void updateDouble(final String columnLabel, final double x)
		throws SQLException
	{
		_rs.updateDouble(columnLabel, x);
	}

	@Override
	public void updateBigDecimal(final String columnLabel, final BigDecimal x)
		throws SQLException
	{
		_rs.updateBigDecimal(columnLabel, x);
	}

	@Override
	public void updateString(final String columnLabel, final String x)
		throws SQLException
	{
		_rs.updateString(columnLabel, x);
	}

	@Override
	public void updateBytes(final String columnLabel, final byte[] x)
		throws SQLException
	{
		_rs.updateBytes(columnLabel, x);
	}

	@Override
	public void updateDate(final String columnLabel, final Date x)
		throws SQLException
	{
		_rs.updateDate(columnLabel, x);
	}

	@Override
	public void updateTime(final String columnLabel, final Time x)
		throws SQLException
	{
		_rs.updateTime(columnLabel, x);
	}

	@Override
	public void updateTimestamp(final String columnLabel, final Timestamp x)
		throws SQLException
	{
		_rs.updateTimestamp(columnLabel, x);
	}

	@Override
	public void updateAsciiStream(
		final String columnLabel,
		final InputStream x,
		final int length
	)
		throws SQLException
	{
		_rs.updateAsciiStream(columnLabel, x, length);
	}

	@Override
	public void updateBinaryStream(
		final String columnLabel,
		final InputStream x,
		final int length
	)
		throws SQLException
	{
		_rs.updateBinaryStream(columnLabel, x, length);
	}

	@Override
	public void updateCharacterStream(
		final String columnLabel,
		final Reader reader,
		final int length
	)
		throws SQLException
	{
		_rs.updateCharacterStream(columnLabel, reader, length);
	}

	@Override
	public void updateObject(
		final String columnLabel,
		final Object x,
		final int scaleOrLength
	)
		throws SQLException
	{
		_rs.updateObject(columnLabel, x, scaleOrLength);
	}

	@Override
	public void updateObject(final String columnLabel, final Object x)
		throws SQLException
	{
		_rs.updateObject(columnLabel, x);
	}

	@Override
	public void insertRow() throws SQLException {
		_rs.insertRow();
	}

	@Override
	public void updateRow() throws SQLException {
		_rs.updateRow();
	}

	@Override
	public void deleteRow() throws SQLException {
		_rs.deleteRow();
	}

	@Override
	public void refreshRow() throws SQLException {
		_rs.refreshRow();
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		_rs.cancelRowUpdates();
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		_rs.moveToInsertRow();
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		_rs.moveToCurrentRow();
	}

	@Override
	public Statement getStatement() throws SQLException {
		return _rs.getStatement();
	}

	@Override
	public Object getObject(
		final int columnIndex,
		final Map<String, Class<?>> map
	)
		throws SQLException
	{
		return _rs.getObject(columnIndex, map);
	}

	@Override
	public Ref getRef(final int columnIndex) throws SQLException {
		return _rs.getRef(columnIndex);
	}

	@Override
	public Blob getBlob(final int columnIndex) throws SQLException {
		return _rs.getBlob(columnIndex);
	}

	@Override
	public Clob getClob(final int columnIndex) throws SQLException {
		return _rs.getClob(columnIndex);
	}

	@Override
	public Array getArray(final int columnIndex) throws SQLException {
		return _rs.getArray(columnIndex);
	}

	@Override
	public Object getObject(
		final String columnLabel,
		final Map<String, Class<?>> map
	)
		throws SQLException
	{
		return _rs.getObject(columnLabel, map);
	}

	@Override
	public Ref getRef(final String columnLabel) throws SQLException {
		return _rs.getRef(columnLabel);
	}

	@Override
	public Blob getBlob(final String columnLabel) throws SQLException {
		return _rs.getBlob(columnLabel);
	}

	@Override
	public Clob getClob(final String columnLabel) throws SQLException {
		return _rs.getClob(columnLabel);
	}

	@Override
	public Array getArray(final String columnLabel) throws SQLException {
		return _rs.getArray(columnLabel);
	}

	@Override
	public Date getDate(final int columnIndex, final Calendar cal)
		throws SQLException
	{
		return _rs.getDate(columnIndex, cal);
	}

	@Override
	public Date getDate(final String columnLabel, final Calendar cal)
		throws SQLException
	{
		return _rs.getDate(columnLabel, cal);
	}

	@Override
	public Time getTime(final int columnIndex, final Calendar cal)
		throws SQLException
	{
		return _rs.getTime(columnIndex, cal);
	}

	@Override
	public Time getTime(final String columnLabel, final Calendar cal)
		throws SQLException
	{
		return _rs.getTime(columnLabel, cal);
	}

	@Override
	public Timestamp getTimestamp(final int columnIndex, final Calendar cal)
		throws SQLException
	{
		return _rs.getTimestamp(columnIndex, cal);
	}

	@Override
	public Timestamp getTimestamp(final String columnLabel, final Calendar cal)
		throws SQLException
	{
		return _rs.getTimestamp(columnLabel, cal);
	}

	@Override
	public URL getURL(final int columnIndex) throws SQLException {
		return _rs.getURL(columnIndex);
	}

	@Override
	public URL getURL(final String columnLabel) throws SQLException {
		return _rs.getURL(columnLabel);
	}

	@Override
	public void updateRef(final int columnIndex, final Ref x)
		throws SQLException
	{
		_rs.updateRef(columnIndex, x);
	}

	@Override
	public void updateRef(final String columnLabel, final Ref x)
		throws SQLException
	{
		_rs.updateRef(columnLabel, x);
	}

	@Override
	public void updateBlob(final int columnIndex, final Blob x)
		throws SQLException
	{
		_rs.updateBlob(columnIndex, x);
	}

	@Override
	public void updateBlob(final String columnLabel, final Blob x)
		throws SQLException
	{
		_rs.updateBlob(columnLabel, x);
	}

	@Override
	public void updateClob(final int columnIndex, final Clob x)
		throws SQLException
	{
		_rs.updateClob(columnIndex, x);
	}

	@Override
	public void updateClob(final String columnLabel, final Clob x)
		throws SQLException
	{
		_rs.updateClob(columnLabel, x);
	}

	@Override
	public void updateArray(final int columnIndex, final Array x)
		throws SQLException
	{
		_rs.updateArray(columnIndex, x);
	}

	@Override
	public void updateArray(final String columnLabel, final Array x)
		throws SQLException
	{
		_rs.updateArray(columnLabel, x);
	}

	@Override
	public RowId getRowId(final int columnIndex) throws SQLException {
		return _rs.getRowId(columnIndex);
	}

	@Override
	public RowId getRowId(final String columnLabel) throws SQLException {
		return _rs.getRowId(columnLabel);
	}

	@Override
	public void updateRowId(final int columnIndex, final RowId x)
		throws SQLException
	{
		_rs.updateRowId(columnIndex, x);
	}

	@Override
	public void updateRowId(final String columnLabel, final RowId x)
		throws SQLException
	{
		_rs.updateRowId(columnLabel, x);
	}

	@Override
	public int getHoldability() throws SQLException {
		return _rs.getHoldability();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return _rs.isClosed();
	}

	@Override
	public void updateNString(final int columnIndex, final String nString)
		throws SQLException
	{
		_rs.updateNString(columnIndex, nString);
	}

	@Override
	public void updateNString(final String columnLabel, final String nString)
		throws SQLException
	{
		_rs.updateNString(columnLabel, nString);
	}

	@Override
	public void updateNClob(final int columnIndex, final NClob nClob)
		throws SQLException
	{
		_rs.updateNClob(columnIndex, nClob);
	}

	@Override
	public void updateNClob(final String columnLabel, final NClob nClob)
		throws SQLException
	{
		_rs.updateNClob(columnLabel, nClob);
	}

	@Override
	public NClob getNClob(final int columnIndex) throws SQLException {
		return _rs.getNClob(columnIndex);
	}

	@Override
	public NClob getNClob(final String columnLabel) throws SQLException {
		return _rs.getNClob(columnLabel);
	}

	@Override
	public SQLXML getSQLXML(final int columnIndex) throws SQLException {
		return _rs.getSQLXML(columnIndex);
	}

	@Override
	public SQLXML getSQLXML(final String columnLabel) throws SQLException {
		return _rs.getSQLXML(columnLabel);
	}

	@Override
	public void updateSQLXML(final int columnIndex, final SQLXML xmlObject)
		throws SQLException
	{
		_rs.updateSQLXML(columnIndex, xmlObject);
	}

	@Override
	public void updateSQLXML(final String columnLabel, final SQLXML xmlObject)
		throws SQLException
	{
		_rs.updateSQLXML(columnLabel, xmlObject);
	}

	@Override
	public String getNString(final int columnIndex) throws SQLException {
		return _rs.getNString(columnIndex);
	}

	@Override
	public String getNString(final String columnLabel) throws SQLException {
		return _rs.getNString(columnLabel);
	}

	@Override
	public Reader getNCharacterStream(final int columnIndex)
		throws SQLException
	{
		return _rs.getNCharacterStream(columnIndex);
	}

	@Override
	public Reader getNCharacterStream(final String columnLabel)
		throws SQLException
	{
		return _rs.getNCharacterStream(columnLabel);
	}

	@Override
	public void updateNCharacterStream(
		final int columnIndex,
		final Reader x,
		final long length
	)
		throws SQLException
	{
		_rs.updateNCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateNCharacterStream(
		final String columnLabel,
		final Reader reader,
		final long length
	)
		throws SQLException
	{
		_rs.updateNCharacterStream(columnLabel, reader, length);
	}

	@Override
	public void updateAsciiStream(
		final int columnIndex,
		final InputStream x,
		final long length
	)
		throws SQLException
	{
		_rs.updateAsciiStream(columnIndex, x, length);
	}

	@Override
	public void updateBinaryStream(
		final int columnIndex,
		final InputStream x,
		final long length
	)
		throws SQLException
	{
		_rs.updateBinaryStream(columnIndex, x, length);
	}

	@Override
	public void updateCharacterStream(
		final int columnIndex,
		final Reader x,
		final long length
	)
		throws SQLException
	{
		_rs.updateCharacterStream(columnIndex, x, length);
	}

	@Override
	public void updateAsciiStream(
		final String columnLabel,
		final InputStream x,
		final long length
	)
		throws SQLException
	{
		_rs.updateAsciiStream(columnLabel, x, length);
	}

	@Override
	public void updateBinaryStream(
		final String columnLabel,
		final InputStream x,
		final long length
	)
		throws SQLException
	{
		_rs.updateBinaryStream(columnLabel, x, length);
	}

	@Override
	public void updateCharacterStream(
		final String columnLabel,
		final Reader reader,
		final long length
	)
		throws SQLException
	{
		_rs.updateCharacterStream(columnLabel, reader, length);
	}

	@Override
	public void updateBlob(
		final int columnIndex,
		final InputStream inputStream,
		final long length
	)
		throws SQLException
	{
		_rs.updateBlob(columnIndex, inputStream, length);
	}

	@Override
	public void updateBlob(
		final String columnLabel,
		final InputStream inputStream,
		final long length
	)
		throws SQLException
	{
		_rs.updateBlob(columnLabel, inputStream, length);
	}

	@Override
	public void updateClob(
		final int columnIndex,
		final Reader reader,
		final long length
	)
		throws SQLException
	{
		_rs.updateClob(columnIndex, reader, length);
	}

	@Override
	public void updateClob(
		final String columnLabel,
		final Reader reader,
		final long length
	)
		throws SQLException
	{
		_rs.updateClob(columnLabel, reader, length);
	}

	@Override
	public void updateNClob(
		final int columnIndex,
		final Reader reader,
		final long length
	)
		throws SQLException
	{
		_rs.updateNClob(columnIndex, reader, length);
	}

	@Override
	public void updateNClob(
		final String columnLabel,
		final Reader reader,
		final long length
	)
		throws SQLException
	{
		_rs.updateNClob(columnLabel, reader, length);
	}

	@Override
	public void updateNCharacterStream(final int columnIndex, final Reader x)
		throws SQLException
	{
		_rs.updateNCharacterStream(columnIndex, x);
	}

	@Override
	public void updateNCharacterStream(
		final String columnLabel,
		final Reader reader
	)
		throws SQLException
	{
		_rs.updateNCharacterStream(columnLabel, reader);
	}

	@Override
	public void updateAsciiStream(final int columnIndex, final InputStream x)
		throws SQLException
	{
		_rs.updateAsciiStream(columnIndex, x);
	}

	@Override
	public void updateBinaryStream(final int columnIndex, final InputStream x)
		throws SQLException
	{
		_rs.updateBinaryStream(columnIndex, x);
	}

	@Override
	public void updateCharacterStream(final int columnIndex, final Reader x)
		throws SQLException
	{
		_rs.updateCharacterStream(columnIndex, x);
	}

	@Override
	public void updateAsciiStream(final String columnLabel, final InputStream x)
		throws SQLException
	{
		_rs.updateAsciiStream(columnLabel, x);
	}

	@Override
	public void updateBinaryStream(final String columnLabel, final InputStream x)
		throws SQLException
	{
		_rs.updateBinaryStream(columnLabel, x);
	}

	@Override
	public void updateCharacterStream(
		final String columnLabel,
		final Reader reader
	)
		throws SQLException
	{
		_rs.updateCharacterStream(columnLabel, reader);
	}

	@Override
	public void updateBlob(final int columnIndex, final InputStream inputStream)
		throws SQLException
	{
		_rs.updateBlob(columnIndex, inputStream);
	}

	@Override
	public void updateBlob(
		final String columnLabel,
		final InputStream inputStream
	)
		throws SQLException
	{
		_rs.updateBlob(columnLabel, inputStream);
	}

	@Override
	public void updateClob(final int columnIndex, final Reader reader)
		throws SQLException
	{
		_rs.updateClob(columnIndex, reader);
	}

	@Override
	public void updateClob(final String columnLabel, final Reader reader)
		throws SQLException
	{
		_rs.updateClob(columnLabel, reader);
	}

	@Override
	public void updateNClob(final int columnIndex, final Reader reader)
		throws SQLException
	{
		_rs.updateNClob(columnIndex, reader);
	}

	@Override
	public void updateNClob(final String columnLabel, final Reader reader)
		throws SQLException
	{
		_rs.updateNClob(columnLabel, reader);
	}

	@Override
	public <T> T getObject(final int columnIndex, final Class<T> type)
		throws SQLException
	{
		return _rs.getObject(columnIndex, type);
	}

	@Override
	public <T> T getObject(final String columnLabel, final Class<T> type)
		throws SQLException
	{
		return _rs.getObject(columnLabel, type);
	}

	@Override
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

	@Override
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

	@Override
	public void updateObject(
		final int columnIndex,
		final Object x,
		final SQLType targetSqlType
	)
		throws SQLException
	{
		_rs.updateObject(columnIndex, x, targetSqlType);
	}

	@Override
	public void updateObject(
		final String columnLabel,
		final Object x,
		final SQLType targetSqlType
	)
		throws SQLException
	{
		_rs.updateObject(columnLabel, x, targetSqlType);
	}

	@Override
	public <T> T unwrap(final Class<T> iface) throws SQLException {
		return _rs.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(final Class<?> iface) throws SQLException {
		return _rs.isWrapperFor(iface);
	}

	static Results of(final ResultSet rs) {
		return new Results(rs);
	}

}
