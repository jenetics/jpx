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
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * This class is a wrapper around the JDBC {@link ResultSet} without all
 * <em>update</em> methods. It just allows you to access the data of the current
 * row.
 *
 * @implNote
 * This class is intended only to be used in the {@link RowParser} interfaces.
 * Client code should not hold references of this class outside of this narrow
 * scope, since the wrapped {@link ResultSet} is move forward and calling the
 * getter methods will return different results, or even throw an
 * {@link SQLException}, if the underlying result-set has been closed.
 *
 * @see ResultSet
 * @see RowParser
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Row {

	private final ResultSet _rs;

	private Row(final ResultSet result) {
		_rs = requireNonNull(result);
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

	public InputStream getBinaryStream(final String columnLabel)
		throws SQLException
	{
		return _rs.getBinaryStream(columnLabel);
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

	public int getRow() throws SQLException {
		return _rs.getRow();
	}

	public int getType() throws SQLException {
		return _rs.getType();
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

	public RowId getRowId(final int columnIndex) throws SQLException {
		return _rs.getRowId(columnIndex);
	}

	public RowId getRowId(final String columnLabel) throws SQLException {
		return _rs.getRowId(columnLabel);
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

	public <T> T unwrap(final Class<T> iface) throws SQLException {
		return _rs.unwrap(iface);
	}

	public boolean isWrapperFor(final Class<?> iface) throws SQLException {
		return _rs.isWrapperFor(iface);
	}

	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	static Row of(final ResultSet result) {
		return new Row(result);
	}

}
