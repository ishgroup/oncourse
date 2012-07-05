package ish.oncourse.utils;

import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;

public class MigrateReferencesUpgrage implements CustomSqlChange {
	private String oncourseSchema;
	private String oncourseReferenceSchema;
	private Statement statement;
	@Override
	public String getConfirmationMessage() {
		return "References tables migrated successfully.";
	}

	@Override
	public void setUp() throws SetupException {
		try {
			Context context = (Context) new InitialContext().lookup("java:comp/env/jdbc");
			DataSource ds1 = (DataSource) context.lookup("/oncourse");
			DataSource ds2 = (DataSource) context.lookup("/oncourse_reference");
			if (ds1 != null && !ds1.getConnection().isClosed() && !ds1.getConnection().isReadOnly()) {
				oncourseSchema = ds1.getConnection().getCatalog();
			} else {
				throw new SetupException("oncourse connection closed or read only");
			}
			if (ds2 != null && !ds2.getConnection().isClosed() && !ds2.getConnection().isReadOnly()) {
				oncourseReferenceSchema = ds2.getConnection().getCatalog();
			}  else {
				throw new SetupException("oncourse_reference connection closed or read only");
			}
			if (oncourseSchema != null && oncourseReferenceSchema != null) {
				statement = ds1.getConnection().createStatement();
			}
		} catch (NamingException e) {
			throw new SetupException(e);
		} catch (SQLException e) {
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

	@Override
	public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
		if (statement != null) {
			try {
				//delete tables with the same name if exists
				statement.execute(String.format("drop table if exists %s.Country;", oncourseSchema));
				statement.execute(String.format("drop table if exists %s.Language;", oncourseSchema));
				statement.execute(String.format("drop table if exists %s.Module;", oncourseSchema));
				statement.execute(String.format("drop table if exists %s.Qualification;", oncourseSchema));
				statement.execute(String.format("drop table if exists %s.TrainingPackage;", oncourseSchema));
				statement.execute(String.format("drop table if exists %s.postcode;", oncourseSchema));
				statement.execute(String.format("drop table if exists %s.postcode_db;", oncourseSchema));
				//now move data
				statement.execute(String.format("alter table %s.Country rename %s.Country; ", oncourseReferenceSchema, oncourseSchema));
				statement.execute(String.format("alter table %s.Language rename %s.Language; ", oncourseReferenceSchema, oncourseSchema));
				statement.execute(String.format("alter table %s.Module rename %s.Module; ", oncourseReferenceSchema, oncourseSchema));
				statement.execute(String.format("alter table %s.Qualification rename %s.Qualification; ", oncourseReferenceSchema, oncourseSchema));
				statement.execute(String.format("alter table %s.TrainingPackage rename %s.TrainingPackage; ", oncourseReferenceSchema, oncourseSchema));
				statement.execute(String.format("alter table %s.postcode rename %s.postcode; ", oncourseReferenceSchema, oncourseSchema));
				statement.execute(String.format("alter table %s.postcode_db rename %s.postcode_db; ", oncourseReferenceSchema, oncourseSchema));
				if (!statement.getConnection().getAutoCommit()) {
					statement.getConnection().commit();
				}
				return null;
			} catch (SQLException e) {
				/*try {
					statement.getConnection().rollback();
				} catch (SQLException e1) {
					throw new CustomChangeException(e1);
				}*/
				throw new CustomChangeException(e);
			}
		} else {
			throw new CustomChangeException("Failed to execute reference tables migration");
		}
	}

}
