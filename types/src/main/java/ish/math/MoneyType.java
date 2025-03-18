/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.math;

import org.apache.cayenne.access.types.ExtendedType;
import org.apache.cayenne.dba.TypesMapping;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A custom Cayenne {@link ExtendedType} implementation that allows the use of {@link Money}
 * as an entity attribute type in Cayenne ORM.
 * <p>
 * This class handles conversion between database values and {@link Money} instances, ensuring
 * proper serialization and deserialization of monetary values within the ORM framework.
 * </p>
 */
public class MoneyType implements ExtendedType {

	/**
	 * Returns fully qualified class name of the {@link Money} type.
	 *
	 * @return the class name of {@link Money}.
	 *
	 * @see org.apache.cayenne.access.types.ExtendedType#getClassName()
	 */
	public String getClassName() {
		return Money.class.getName();
	}

	/**
	 * Converts a value from a {@link ResultSet} into a {@link Money} object.
	 *
	 * @param rs    the result set that contain the value.
	 * @param index the column index in the result set.
	 * @param type  the SQL type of the column.
	 * @return a {@link Money} instance representing the value, or {@code null} if the column is NULL.
	 * @throws SQLException if a database access error occurs.
	 *
	 * @see org.apache.cayenne.access.types.ExtendedType#materializeObject(java.sql.ResultSet, int, int)
	 */
	public Object materializeObject(ResultSet rs, int index, int type) throws SQLException {
		Object object = rs.getObject(index);
		if (rs.wasNull()) {
			return null;
		}
		return Money.of(object.toString());
	}

	/**
	 * Converts a value from a {@link CallableStatement} into a {@link Money} object.
	 *
	 * @param rs    the callable statement containing the value.
	 * @param index the parameter index.
	 * @param type  the SQL type of the parameter.
	 * @return a {@link Money} instance representing the value, or {@code null} if the parameter is NULL.
	 * @throws SQLException if a database access error occurs.
	 *
	 * @see org.apache.cayenne.access.types.ExtendedType#materializeObject(java.sql.CallableStatement, int, int)
	 */
	public Object materializeObject(CallableStatement rs, int index, int type) throws SQLException {
		Object object = rs.getObject(index);
		if (rs.wasNull()) {
			return null;
		}
		return Money.of(object.toString());
	}

	@Override
	public String toString(Object value) {
		return ((Money) value).toBigDecimal().toString();
	}

	/**
	 * Binds a {@link Money} object to a JDBC {@link PreparedStatement}.
	 * <p>
	 * This method ensures that a {@@link Money} value is correctly converted and stored in a SQL database.
	 * If the {@code value} is {@code null}, it binds a SQL NULL to the statement.
	 * Otherwise, it checks if the given SQL type is numeric and binds the {@link Money} amount
	 * as a {@link java.math.BigDecimal}.
	 * </p>
	 *
	 * <h3>Binding Process:</h3>
	 * <ul>
	 *     <li>Checks if {@code value} is {@code null}. If so, binds a NULL to the statement.</li>
	 *     <li>Validates whether the SQL type is numeric using {@link TypesMapping#isNumeric(int)}.</li>
	 *     <li>If the type is numeric, extracts the {@code BigDecimal} value from {@link Money}
	 *         and binds it using {@link PreparedStatement#setBigDecimal(int, BigDecimal)}.</li>
	 *     <li>Throws {@link IllegalArgumentException} if attempting to bind a non-numeric SQL type.</li>
	 * </ul>
	 *
	 * @param statement the prepared statement to bind the value to.
	 * @param value     the {@link Money} object containing the monetary value.
	 * @param pos       the position of the parameter in the SQL statement (1-based index).
	 * @param type      the SQL type of the parameter, as defined in {@link java.sql.Types}.
	 * @param precision the precision of the numeric column (not used in this implementation).
	 * @throws SQLException if a database access error occurs.
	 * @throws IllegalArgumentException if the provided SQL type is not numeric.
	 *
	 * @see org.apache.cayenne.access.types.ExtendedType#setJdbcObject(java.sql.PreparedStatement, java.lang.Object, int, int, int)
	 */
	public void setJdbcObject(PreparedStatement statement, Object value, int pos, int type, int precision) throws SQLException {
		if (value == null) {
			statement.setNull(pos, type);
		} else if (TypesMapping.isNumeric(type)) {
			statement.setBigDecimal(pos, ((Money) value).toBigDecimal());
		} else {
			throw new IllegalArgumentException("Can't map Money to a non-numeric type: " + TypesMapping.getSqlNameByType(type));
		}
	}

}
