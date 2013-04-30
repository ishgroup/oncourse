package ish.oncourse.model.liquibase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

public class LicenseFeeBillingMonthUpgrade implements CustomSqlChange {
	
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private String oncourseSchema;
	private Statement statement;

	public String getConfirmationMessage() {
		return "Billing month data upgraded successfully.";
	}

	public void setUp() throws SetupException {
		try {
            Context context = (Context) new InitialContext().lookup("java:comp/env/jdbc");
            DataSource ds = (DataSource) context.lookup("/oncourse");
            if (ds != null && !ds.getConnection().isClosed() && !ds.getConnection().isReadOnly()) {
                oncourseSchema = ds.getConnection().getCatalog();
            } else {
                throw new SetupException("oncourse connection closed or read only");
            }
            if (oncourseSchema != null) {
                statement = ds.getConnection().createStatement();
            }
        } catch (NamingException | SQLException e) {
            throw new SetupException(e);
        }
	}

	public void setFileOpener(ResourceAccessor resourceAccessor) {}

	public ValidationErrors validate(Database database) { 
		return null;
	}

	public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
		if (this.statement != null) {
			try {
				Map<Long, Integer> monthMap = new HashMap<>();
				
				try {
					ResultSet rs = statement.executeQuery("SELECT id, billingMonth FROM LicenseFee where billingMonth is not null");
					while (rs.next()) {
						monthMap.put(rs.getLong(1), rs.getInt(2));
					}
					
					for (Long id : monthMap.keySet()) {
						String query = String.format(
								"UPDATE LicenseFee SET renewalDate = '%s' where id = %d", getBillingDate(monthMap.get(id)), id);
						statement.execute(query);
					}
				} finally {
					statement.close();
				}
			} catch (SQLException e) {
				throw new CustomChangeException("Falied to execute upgrade", e);
			}
		}
		return null;
	}
	
	private String getBillingDate(Integer month) {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		
		cal.set(Calendar.MONTH, month);
		if (cal.getTime().before(now)) {
			cal.add(Calendar.YEAR, 1);
		}
		
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		return dateFormat.format(cal.getTime());
	}

}
