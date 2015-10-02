package ish.oncourse.services.paymentexpress;

import com.paymentexpress.stubs.TransactionResult2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentExpressUtil {

	private static final Logger LOG = LogManager.getLogger();

	/**
	 * PaymentExpress format for transaction dates.
	 */
	private static final String SETTLEMENT_DATE_FORMAT = "yyyyMMdd";

	public static final String PAYMENT_EXPRESS_TXN_TYPE = "Purchase";
	public static final String PAYMENT_EXPRESS_TXN_TYPE_REFUND = "Refund";

	public static final DecimalFormat DECIMAL_AMOUNT_FORMATTER;
	public static final DecimalFormat IN_CENTS_AMOUNT_FORMATTER;
	
	public static final String[] INVALID_RESPONCES = {"ConnectErr", "ConnectTimeout", "csdSend Select Err", "TRANSMISSION ERROR"}; 

	static {
		DECIMAL_AMOUNT_FORMATTER = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
		DECIMAL_AMOUNT_FORMATTER.setMaximumFractionDigits(2);
		DECIMAL_AMOUNT_FORMATTER.setMinimumFractionDigits(2);
		DECIMAL_AMOUNT_FORMATTER.setMinimumIntegerDigits(1);
		DECIMAL_AMOUNT_FORMATTER.setGroupingUsed(false);
		// FIXME ArithmeticException is thrown with this mode.
		// DECIMAL_AMOUNT_FORMATTER.setRoundingMode( RoundingMode.UNNECESSARY );
		DECIMAL_AMOUNT_FORMATTER.setParseBigDecimal(true);

		IN_CENTS_AMOUNT_FORMATTER = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
		IN_CENTS_AMOUNT_FORMATTER.setMaximumFractionDigits(0);
		IN_CENTS_AMOUNT_FORMATTER.setMinimumFractionDigits(0);
		IN_CENTS_AMOUNT_FORMATTER.setMinimumIntegerDigits(1);
		IN_CENTS_AMOUNT_FORMATTER.setGroupingUsed(false);
		// IN_CENTS_AMOUNT_FORMATTER.setRoundingMode( RoundingMode.UNNECESSARY
		// );
		IN_CENTS_AMOUNT_FORMATTER.setMultiplier(100);
		IN_CENTS_AMOUNT_FORMATTER.setParseBigDecimal(true);
	}

	/**
	 * Indicates card expiry date. Format is MMYY where MM is month 01-12 and
	 * Year 00-99. do not insert "/" or other delimiter.
	 * 
	 * @param expiryDate
	 *            "M/YY", or "M/YYYY", or "MM/YY", or "MM/YYYY""
	 * @return
	 */
	public static String translateInputExpiryDate(String expiryDate) {
		StringBuilder buff = new StringBuilder();
		if (expiryDate != null) {
			if ((expiryDate.matches("^(\\d{1}|\\d{2})/(\\d{2}|\\d{4})"))) {
				String[] dateParts = expiryDate.split("/");
				if (dateParts[0].length() == 1) {
					buff.append("0");
				}
				buff.append(dateParts[0]);
				if (dateParts[1].length() == 4) {
					dateParts[1] = dateParts[1].substring(2);
				}
				buff.append(dateParts[1]);
			}
		}
		return buff.toString();
	}

	/**
	 * @param amount
	 * @return the amount as a decimal number with 2 decimal places
	 */
	public static String translateInputAmountAsDecimalString(BigDecimal amount) {
		return DECIMAL_AMOUNT_FORMATTER.format(amount);
	}

	public static boolean translateFlag(String flag) {
		return "true".equalsIgnoreCase(flag) || "1".equals(flag);
	}

	/**
	 * Translates payment express settlement date string into java date object.
	 * 
	 * @param dateStr
	 *            date string
	 * @return java date object
	 */
	public static Date translateSettlementDate(String dateStr) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(SETTLEMENT_DATE_FORMAT);
		Date date = null;

		try {
			date = dateFormat.parse(dateStr);
		} catch (Exception e) {
			LOG.error("Can not parse settlement date from PaymentExpress response.", e);
		}

		return date;
	}

	public static boolean isValidResult(TransactionResult result) {
		if (result.getResult2() == null || TransactionResult.ResultStatus.UNKNOWN.equals(result.getStatus())) {
			return false;
		} else if (StringUtils.trimToNull(result.getResult2().getResponseText()) != null) {
			
			for (String pref : INVALID_RESPONCES) {
				if (result.getResult2().getResponseText().startsWith(pref)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
