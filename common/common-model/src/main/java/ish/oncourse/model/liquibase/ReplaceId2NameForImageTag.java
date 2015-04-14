package ish.oncourse.model.liquibase;

import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The update  replaces all rich tag like '{image id:....}' to corresponding '{image name:....}'.
 */
public class ReplaceId2NameForImageTag implements CustomSqlChange {

	private static final Logger logger = LogManager.getLogger();
	private static final String SELECT_webContents = "select wc.id, ws.collegeid, wc.content, wc.content_textile from WebContent wc inner join WebSite ws on wc.webSiteId = ws.id where content_textile like '%{image id:%'";
	private static final String SELECT_binaryInfo = "select id, referenceNumber, name from BinaryInfo bi where bi.referenceNumber = ? and bi.collegeId = ?";
	private static final String UPDATE_webContent = "update WebContent set content=?, content_textile=? where id = ?";
	private DataSource ds;

	@Override
	public String getConfirmationMessage() {
		return "prepare for replace id param to name param for  rich tag \"image\" in WebContent";
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

	@Override
	public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
		Connection connection = null;

		try {
			connection = ds.getConnection();
			List<WebContent> result = getWebContents(connection);
			for (WebContent wc : result) {
				adjust(wc, connection);
			}
		} catch (Throwable e) {
			logger.error(e);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
				}
		}
		return null;
	}

	private void adjust(WebContent wc, Connection connection) throws CustomChangeException {
		String ctsp = "[{]image[ ]id:\"[0-9]+\"";
		String csp = "[{]image id:&#8220;[0-9]+";

		Pattern p = Pattern.compile(ctsp);
		Matcher matcher = p.matcher(wc.content_textile);
		while (matcher.find()) {
			try {
				String value = matcher.group();
				Pattern p1 = Pattern.compile("[0-9]+");
				Matcher m1 = p1.matcher(value);
				m1.find();

				long referenceNumber = Long.valueOf(m1.group());
				String rcts = String.format("{image id:\"%d", referenceNumber);
				String rcs = String.format("{image id:&#8220;%d", referenceNumber);

				BinaryInfo bi = getBinaryInfo(referenceNumber, wc.collegeid, connection);
				String ncts;
				String ncs;
				if (bi != null) {
					ncts = String.format("{image name:\"%s", bi.name);
					ncs = String.format("{image name:&#8220;%s", bi.name);
				} else {
					logger.error("Cannot find BinaryInfo by referenceNumber: {} and collegeId: {}", referenceNumber, wc.collegeid);

					ncts = String.format("{image name:\"%s", referenceNumber);
					ncs = String.format("{image name:&#8220;%s", referenceNumber);
				}
				wc.content_textile = wc.content_textile.replace(rcts, ncts);

				wc.content = wc.content.replace(rcs, ncs);
				wc.content = wc.content.replace(rcts, ncts);

				updateWebContent(wc, connection);
			} catch (Throwable e) {
				logger.error("Cannot update WebContent with id: {}", wc.id, e);
			}
		}
	}

	private void updateWebContent(WebContent wc, Connection connection)throws SQLException{
		PreparedStatement statement = null;
		try {
			BinaryInfo result = new BinaryInfo();
			statement = connection.prepareStatement(UPDATE_webContent);
			statement.setString(1, wc.content);
			statement.setString(2, wc.content_textile);
			statement.setLong(3, wc.id);

			statement.executeUpdate();
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
				}
		}

	}

	private BinaryInfo getBinaryInfo(long referenceNumber, long collegeId, Connection connection) throws CustomChangeException {
		PreparedStatement statement = null;

		try {
			BinaryInfo result = new BinaryInfo();
			statement = connection.prepareStatement(SELECT_binaryInfo);
			statement.setLong(1, referenceNumber);
			statement.setLong(2, collegeId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				result.id = resultSet.getLong(1);
				result.referenceNumber = resultSet.getInt(2);
				result.name = resultSet.getString(3);
				return result;
			}
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
				}
		}
		return null;
	}

	private List<WebContent> getWebContents(Connection connection) throws SQLException {
		Statement statement = null;

		ArrayList<WebContent> result = null;
		try {
			result = new ArrayList<>();

			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(SELECT_webContents);
			while (resultSet.next()) {
				WebContent webContent = new WebContent();
				webContent.id = resultSet.getLong(1);
				webContent.collegeid = resultSet.getLong(2);
				webContent.content = resultSet.getString(3);
				webContent.content_textile = resultSet.getString(4);
				result.add(webContent);
			}
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			if (statement != null)
				statement.getConnection();
		}

		return result;
	}

	private static class WebContent {
		long id;
		long collegeid;
		String content;
		String content_textile;
	}

	private static class BinaryInfo {
		public int referenceNumber;
		long id;
		String name;
	}
}
