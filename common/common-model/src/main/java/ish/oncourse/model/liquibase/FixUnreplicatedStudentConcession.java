/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.model.liquibase;

import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.statement.SqlStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;


public class FixUnreplicatedStudentConcession extends ACustomSqlChange {

	private static final Logger logger = LogManager.getLogger();

	private static final String SELECT_Duplicate_Concession = "select c1.id from  StudentConcession c1 \n" +
			"inner join StudentConcession c2  on c1.studentId = c2.studentId  and c1.concessionTypeId = c2.concessionTypeId and c1.id != c2.id\n" +
			"where \n" +
			"c1.collegeId is null and c2.collegeId is not null";
	
	private static final String DELETE_Duplicate_Concession = "DELETE FROM StudentConcession WHERE id=?";


	private static final String SELECT_Unreplicated_Concession = "select s.collegeId, sc.id  from StudentConcession sc join Student s on sc.studentId = s.id where sc.collegeId is null";

	private static final String SET_CollegeID = "update StudentConcession set collegeId =? where id =?";

	private static final String INSERT_QueuedTransaction = "INSERT INTO QueuedTransaction (collegeId, transactionKey, created) VALUES (?,?,now())";

	private static final String INSERT_QueuedRecord = "INSERT INTO QueuedRecord (collegeId, entityIdentifier, entityWillowId, `action`, numberOfAttempts, lastAttemptTimestamp, transactionId)\n" +
			"\tSELECT collegeId, 'StudentConcession',id,'UPDATE',0,now(),(select t.id from QueuedTransaction t where t.transactionKey=?) FROM StudentConcession where id = ?";

	private static final String ALTER_AadConstrain = "ALTER TABLE StudentConcession MODIFY collegeId BIGINT NOT NULL";


	@Override
	public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
		Connection connection = null;

		try {
			connection = ds.getConnection();
			removeDuplicateConcessions(connection);
			updateStudentConcession(connection); 
			addConstrain(connection);

			
		} catch (Throwable e) {
			logger.catching(e);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
				}
		}
		return null;
	}

	private void addConstrain(Connection connection) {
		PreparedStatement alterStatement = null;
		try {
			alterStatement = connection.prepareStatement(ALTER_AadConstrain);
			alterStatement.executeUpdate();
			alterStatement.close();
		} catch (SQLException e) {
			logger.catching(e);
		} finally {
			try {
				if (alterStatement != null)
					alterStatement.close();

			} catch(SQLException e){
			}

		}
	}


	private void removeDuplicateConcessions(Connection connection) throws SQLException {
		Statement selectStatement = null;
		ResultSet resultSet = null;
		PreparedStatement deleteStatement = null;
		
		try {
			selectStatement = connection.createStatement();
			resultSet = selectStatement.executeQuery(SELECT_Duplicate_Concession);
			while (resultSet.next()) {
				deleteStatement = connection.prepareStatement(DELETE_Duplicate_Concession);
				deleteStatement.setLong(1,resultSet.getLong(1));
				deleteStatement.executeUpdate();
				deleteStatement.close();
			}

			
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			try {
				if (deleteStatement != null)
					deleteStatement.close();				
				if (selectStatement != null)
					selectStatement.close();
				if (resultSet != null)
					resultSet.close();
			} catch(SQLException e){
			}
			
		}

	}


	private void updateStudentConcession(Connection connection) {
		Statement statement = null;
		PreparedStatement setStatement = null;
		PreparedStatement insertQueuedTransaction = null;
		PreparedStatement insertQueuedRecord = null;
		ResultSet resultSet = null;
		
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(SELECT_Unreplicated_Concession);

			
			while (resultSet.next()) {
				
				setStatement = connection.prepareStatement(SET_CollegeID);
				setStatement.setLong(1, resultSet.getLong(1));
				setStatement.setLong(2, resultSet.getLong(2));
				setStatement.executeUpdate();
				setStatement.close();
				
				insertQueuedTransaction = connection.prepareStatement(INSERT_QueuedTransaction);
				insertQueuedTransaction.setLong(1, resultSet.getLong(1));
				insertQueuedTransaction.setString(2, "StudentConcession_" + resultSet.getLong(2));
				insertQueuedTransaction.executeUpdate();
				insertQueuedTransaction.close();
				
				insertQueuedRecord = connection.prepareStatement(INSERT_QueuedRecord);
				insertQueuedRecord.setString(1, "StudentConcession_" + resultSet.getLong(2));
				insertQueuedRecord.setLong(2, resultSet.getLong(2));
				insertQueuedRecord.executeUpdate();
				insertQueuedRecord.close();
				
			}

		} catch (SQLException e) {
			logger.error(e);
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (setStatement != null)
					setStatement.close();
				if (insertQueuedTransaction != null)
					insertQueuedTransaction.close();
				if (insertQueuedRecord != null)
					insertQueuedRecord.close();
				if (statement != null)
					statement.close();

				
			} catch (SQLException e) {
			}
		}
	}
	
}
