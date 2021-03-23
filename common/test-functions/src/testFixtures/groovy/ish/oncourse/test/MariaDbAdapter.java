/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.test;

import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.translator.ParameterBinding;
import org.apache.cayenne.access.translator.ejbql.EJBQLTranslatorFactory;
import org.apache.cayenne.access.translator.select.QualifierTranslator;
import org.apache.cayenne.access.translator.select.QueryAssembler;
import org.apache.cayenne.access.translator.select.SelectTranslator;
import org.apache.cayenne.access.types.ExtendedTypeMap;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dba.PkGenerator;
import org.apache.cayenne.dba.QuotingStrategy;
import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbEntity;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.query.Query;
import org.apache.cayenne.query.SQLAction;
import org.apache.cayenne.query.SelectQuery;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * User: akoiro
 * Date: 31/10/17
 */
public class MariaDbAdapter implements DbAdapter {

	private DbAdapter delegate;

	public MariaDbAdapter(DbAdapter delegate) {
		this.delegate = delegate;
	}


	@Override
	public String getBatchTerminator() {
		return delegate.getBatchTerminator();
	}

	@Override
	public SelectTranslator getSelectTranslator(SelectQuery<?> query, EntityResolver entityResolver) {
		return delegate.getSelectTranslator(query, entityResolver);
	}

	@Override
	public QualifierTranslator getQualifierTranslator(QueryAssembler queryAssembler) {
		return delegate.getQualifierTranslator(queryAssembler);
	}

	@Override
	public SQLAction getAction(Query query, DataNode node) {
		return delegate.getAction(query, node);
	}

	@Override
	public boolean supportsUniqueConstraints() {
		return delegate.supportsUniqueConstraints();
	}

	@Override
	public boolean supportsCatalogsOnReverseEngineering() {
		return delegate.supportsCatalogsOnReverseEngineering();
	}

	@Override
	public boolean supportsGeneratedKeys() {
		return delegate.supportsGeneratedKeys();
	}

	@Override
	public boolean supportsBatchUpdates() {
		return delegate.supportsBatchUpdates();
	}

	@Override
	public boolean typeSupportsLength(int type) {
		return delegate.typeSupportsLength(type);
	}

	@Override
	public Collection<String> dropTableStatements(DbEntity table) {
		return delegate.dropTableStatements(table);
	}

	@Override
	public String createTable(DbEntity entity) {
		String sql = delegate.createTable(entity);

		if (entity.getName().equals("CustomField")) {
			sql = sql.replace("value VARCHAR(32000) NULL", "value TEXT NULL");
		}

		if (entity.getName().equals("CustomFieldType")) {
			sql = sql.replace("defaultValue VARCHAR(32000) NULL", "defaultValue TEXT NULL");
		}
		sql = sql.replace("VARCHAR ", "VARCHAR(4096) ");
		sql = sql.replace("VARCHAR;", "VARCHAR(4096);");
		return sql;
	}

	@Override
	public String createUniqueConstraint(DbEntity source, Collection<DbAttribute> columns) {
		Collection<DbAttribute> filtered = filterConstraintColumns(source.getName(), columns);
		if (filtered.size() > 0)
			return delegate.createUniqueConstraint(source, filtered);
		else
			return null;
	}

	@Override
	public String createFkConstraint(DbRelationship rel) {
		/*liquibase.db.changelog.xml:changeSet:278*/
		if (rel.getSourceEntityName().equals("WebMenu") && rel.getTargetEntityName().equals("WebMenu")) {
			return String.format("%s ON DELETE CASCADE", delegate.createFkConstraint(rel));
		} else {
			return delegate.createFkConstraint(rel);
		}
	}

	@Override
	public String[] externalTypesForJdbcType(int type) {
		return delegate.externalTypesForJdbcType(type);
	}

	@Override
	public ExtendedTypeMap getExtendedTypes() {
		return delegate.getExtendedTypes();
	}

	@Override
	public PkGenerator getPkGenerator() {
		return delegate.getPkGenerator();
	}

	@Override
	public void setPkGenerator(PkGenerator pkGenerator) {
		delegate.setPkGenerator(pkGenerator);
	}

	@Override
	public DbAttribute buildAttribute(String name, String typeName, int type, int size, int scale, boolean allowNulls) {
		return delegate.buildAttribute(name, typeName, type, size, scale, allowNulls);
	}

	@Override
	public void bindParameter(PreparedStatement statement, ParameterBinding parameterBinding) throws SQLException, Exception {
		delegate.bindParameter(statement, parameterBinding);
	}

	@Override
	public String tableTypeForTable() {
		return delegate.tableTypeForTable();
	}

	@Override
	public String tableTypeForView() {
		return delegate.tableTypeForView();
	}

	@Override
	public void createTableAppendColumn(StringBuffer sqlBuffer, DbAttribute column) {

		delegate.createTableAppendColumn(sqlBuffer, column);

		if (column.getType() == 12 && column.getMaxLength() > 2000) {
			System.out.println(sqlBuffer);
		}
	}

	@Override
	public QuotingStrategy getQuotingStrategy() {
		return delegate.getQuotingStrategy();
	}

	@Override
	public DbAdapter unwrap() {
		return delegate.unwrap();
	}

	@Override
	public EJBQLTranslatorFactory getEjbqlTranslatorFactory() {
		return delegate.getEjbqlTranslatorFactory();
	}

	/**
	 * Filter not used constraints here
	 *
	 * @param tableName table
	 * @param columns   source DbAttribute collection
	 * @return filtered DbAttribute collection
	 */
	private Collection<DbAttribute> filterConstraintColumns(String tableName, Collection<DbAttribute> columns) {
		if ("Contact".equals(tableName)) {
			return columns.stream()
					.filter(attr -> !"studentId".equals(attr.getName()))
					.collect(Collectors.toList());
		}

		return columns;
	}
}
