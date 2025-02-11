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
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.validation.ValidationResult;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Allows cayenne to using Money as a type for entity attributes
 */
public class MoneyType implements ExtendedType {

	/**
	 * @see org.apache.cayenne.access.types.ExtendedType#getClassName()
	 */
	public String getClassName() {
		return Money.class.getName();
	}

	/**
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

	/**
	 * @see org.apache.cayenne.access.types.ExtendedType#validateProperty(java.lang.Object, java.lang.String, java.lang.Object,
	 *      org.apache.cayenne.map.DbAttribute, org.apache.cayenne.validation.ValidationResult)
	 */
	@Deprecated
	public boolean validateProperty(Object source, String property, Object value, DbAttribute dbAttribute, ValidationResult validationResult) {
		return true;
	}

}
