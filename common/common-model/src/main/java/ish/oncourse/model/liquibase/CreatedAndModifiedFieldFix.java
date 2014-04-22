package ish.oncourse.model.liquibase;

import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.statement.SqlStatement;
import org.apache.log4j.Logger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class CreatedAndModifiedFieldFix extends ACustomSqlChange {

    private static final Logger LOGGER = Logger.getLogger(CreatedAndModifiedFieldFix.class);
    //entities to update
    private static final String[] ENTITIES =
            {
                    "Enrolment",
                    "Invoice",
                    "InvoiceLine",
                    "PaymentIn",
                    "PaymentInLine",
                    "VoucherPaymentIn"
            };

    private static final String[] SELECT_To_UPDATE_NullCreated_Columns =
            {
                    "SELECT id, modified FROM %s WHERE created IS NULL AND modified IS NOT NULL",
                    "UPDATE %s SET created = ? WHERE id= ?"
            };

    private static final String[] SELECT_To_UPDATE_NullModified_Columns =
            {
                    "SELECT id, created FROM %s WHERE modified IS NULL AND created IS NOT NULL",
                    "UPDATE %s SET modified = ? WHERE id= ?"
            };

    private static final String[] SELECT_NullCreated_NullModified_Columns =
            {
                    "SELECT id FROM %s WHERE created IS NULL AND modified IS NULL",
                    "UPDATE %s SET created = ?, modified = ? WHERE id= ?"
            };

    //set time 2011-01-01 00:00:00
    private static final String fixDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        GregorianCalendar gregorianCalendar = new GregorianCalendar(2011, 0, 1);
        return simpleDateFormat.format(gregorianCalendar.getTime());
    }


    @Override
    public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
        Connection connection = null;
        try {
            connection = ds.getConnection();
            for (String entity : ENTITIES) {
                UpdateNullCreatedOrModifiedColumns(connection, entity, SELECT_To_UPDATE_NullCreated_Columns);
                UpdateNullCreatedOrModifiedColumns(connection, entity, SELECT_To_UPDATE_NullModified_Columns);
                UpdateNullCreatedAndModifiedColumns(connection, entity, SELECT_NullCreated_NullModified_Columns);
            }
        } catch (Throwable e) {
            LOGGER.error(e);
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException e) {
                }
        }
        return null;
    }

    //Update NullCreated or NullModified columns;
    private void UpdateNullCreatedOrModifiedColumns(Connection connection, String entity, String[] query) {
        Statement selectStatement = null;
        ResultSet resultSet = null;
        PreparedStatement updateStatement = null;
        String selectQuery = String.format(query[0], entity);
        String updateQuery = String.format(query[1], entity);
        try {
            selectStatement = connection.createStatement();
            resultSet = selectStatement.executeQuery(selectQuery);
            while (resultSet.next()) {
                updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, resultSet.getString(2));
                updateStatement.setLong(2, resultSet.getLong(1));
                updateStatement.executeUpdate();
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle);
        } finally {
            try {
                if (updateStatement != null) {
                    updateStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (selectStatement != null) {
                    selectStatement.close();
                }
            } catch (SQLException sqle) {
            }
        }

    }

    //Update NullCreated and NullModified columns together;
    private void UpdateNullCreatedAndModifiedColumns(Connection connection, String entity, String[] query) {
        Statement selectStatement = null;
        ResultSet resultSet = null;
        PreparedStatement updateStatement = null;
        String selectQuery = String.format(query[0], entity);
        String updateQuery = String.format(query[1], entity);
        try {
            selectStatement = connection.createStatement();
            resultSet = selectStatement.executeQuery(selectQuery);
            while (resultSet.next()) {
                updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, fixDate());
                updateStatement.setString(2, fixDate());
                updateStatement.setLong(3, resultSet.getLong(1));
                updateStatement.executeUpdate();
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle);
        } finally {
            try {
                if (updateStatement != null) {
                    updateStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (selectStatement != null) {
                    selectStatement.close();
                }
            } catch (SQLException sqle) {
            }
        }

    }
}
