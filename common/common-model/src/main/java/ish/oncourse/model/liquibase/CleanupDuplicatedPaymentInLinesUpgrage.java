package ish.oncourse.model.liquibase;

import ish.math.Money;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class CleanupDuplicatedPaymentInLinesUpgrage implements CustomSqlChange {
	private static final String CLEANUP_PAYMENTINLINES_QUERY = "delete from PaymentInLine where id=%s;";
	private static final String MERGE_PAYMENTINLINES_QUERY = "update PaymentInLine set amount = %s where id=%s;";
	private static final String DUPLICATE_PAYMENTINLINES_SELECT_QUERY = "select pil1.id, pil1.amount, pil1.paymentInId, pil1.invoiceId " +
	"from PaymentInLine pil1 left join PaymentInLine pil2 on pil1.paymentInId = pil2.paymentInId and pil1.invoiceId = pil2.invoiceId and pil1.id != pil2.id " +
	"where pil2.ID is not NULL order by pil1.created desc;";
	private String oncourseSchema;
	private Statement statement;
	
	@Override
	public String getConfirmationMessage() {
		return "prepare for add unique constraint to PaymetnInLines completed";
	}

	@Override
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
			final Map<Long, Map<Long, List<PaymentInLineContainer>>> duplicatesMap = new HashMap<Long, Map<Long, List<PaymentInLineContainer>>>();
			try {
				try {
					ResultSet rs = statement.executeQuery(DUPLICATE_PAYMENTINLINES_SELECT_QUERY);
					while (rs.next()) {
						//fill the data
						Long paymentInLineId = rs.getLong(1);
						Double amount = rs.getDouble(2);
						Long paymentId = rs.getLong(3);
						Long invoiceId = rs.getLong(4);
						Map<Long, List<PaymentInLineContainer>> paymentInvoices = duplicatesMap.get(paymentId);
						if (paymentInvoices == null) {
							paymentInvoices = new HashMap<Long, List<PaymentInLineContainer>>();
						}
						List<PaymentInLineContainer> duplicatedPaymentInLines = paymentInvoices.get(invoiceId);
						if (duplicatedPaymentInLines == null) {
							duplicatedPaymentInLines = new ArrayList<PaymentInLineContainer>();
						}
						duplicatedPaymentInLines.add(new PaymentInLineContainer(paymentInLineId, amount));
						paymentInvoices.put(invoiceId, duplicatedPaymentInLines);
						duplicatesMap.put(paymentId, paymentInvoices);
					}
					//update and cleanup the data
					for (Long paymentId : duplicatesMap.keySet()) {
						Map<Long, List<PaymentInLineContainer>> paymentInvoices = duplicatesMap.get(paymentId);
						for (Long invoiceId : paymentInvoices.keySet()) {
							List<PaymentInLineContainer> duplicatedPaymentInLines = paymentInvoices.get(invoiceId);
							Money resultAmount = Money.ZERO;
							for (PaymentInLineContainer container : duplicatedPaymentInLines) {
								resultAmount = resultAmount.add(new Money(container.amount.toString()));
							}
							//execute update after calculating the result amount
							String mergeQuery = String.format(MERGE_PAYMENTINLINES_QUERY, resultAmount.toBigDecimal().toString(), duplicatedPaymentInLines.get(0).id);
							statement.execute(mergeQuery);
							System.out.println(mergeQuery);
							duplicatedPaymentInLines.remove(0);
							//cleanup the duplicates
							for (PaymentInLineContainer container : duplicatedPaymentInLines) {
								String cleanupQuery = String.format(CLEANUP_PAYMENTINLINES_QUERY, container.id);
								statement.execute(cleanupQuery);
								System.out.println(cleanupQuery);
							}
						}
					}
					if (!statement.getConnection().getAutoCommit()) {
	                    statement.getConnection().commit();
	                }
					return null;
				} finally {
					if (statement != null && !statement.isClosed()) {
						statement.close();
					}
				}
			} catch (SQLException e) {
				throw new CustomChangeException("Falied to execute upgrade", e);
			}
		} else {
            throw new CustomChangeException("Failed to execute PaymentInLine cleanup migration");
        }
	}
	
	private class PaymentInLineContainer {
		private Long id;
		private Double amount;
		
		private PaymentInLineContainer(Long id, Double amount) {
			this.id = id;
			this.amount = amount;
		}
	}

}
