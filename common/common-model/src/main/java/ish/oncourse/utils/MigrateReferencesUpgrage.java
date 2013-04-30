package ish.oncourse.utils;

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
import java.sql.SQLException;
import java.sql.Statement;

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
            } else {
                throw new SetupException("oncourse_reference connection closed or read only");
            }
            if (oncourseSchema != null && oncourseReferenceSchema != null) {
                statement = ds1.getConnection().createStatement();
            }
        } catch (NamingException | SQLException e) {
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
                statement.execute("SET foreign_key_checks = 0;");
                migrateTable("Country");
                migrateTable("Language");
                migrateTable("TrainingPackage");
                migrateTable("Qualification");
                migrateTable("Module");
                migrateTable("postcode");
                migrateTable("postcode_db");
                statement.execute("SET foreign_key_checks = 1;");
                if (!statement.getConnection().getAutoCommit()) {
                    statement.getConnection().commit();
                }
                return null;
            } catch (SQLException e) {
                throw new CustomChangeException(e);
            }
        } else {
            throw new CustomChangeException("Failed to execute reference tables migration");
        }
    }

    private void migrateTable(String tableName) throws SQLException {
        statement.execute(String.format("drop table if exists %s.%s;", oncourseSchema, tableName));
        statement.execute(String.format("create table %s.%s like %s.%s;", oncourseSchema, tableName, oncourseReferenceSchema, tableName));
        statement.execute(String.format("insert into %s.%s SELECT * FROM %s.%s;", oncourseSchema , tableName, oncourseReferenceSchema, tableName));
    }

}
