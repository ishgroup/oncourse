/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.model.liquibase;

import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public abstract class ACustomSqlChange implements CustomSqlChange {
	
	protected DataSource ds;
	
	@Override
	abstract public SqlStatement[] generateStatements(Database database) throws CustomChangeException; 

	@Override
	public String getConfirmationMessage() {
		return null;
	}

	@Override
	public void setUp() throws SetupException {
		try {
			Context context = (Context) new InitialContext().lookup("java:comp/env/jdbc");
			ds = (DataSource) context.lookup("/oncourse");
		} catch (NamingException e) {
			throw new SetupException(e);
		}
	}

	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {

	}

	@Override
	public ValidationErrors validate(Database database) {
		return null;
	}
}
