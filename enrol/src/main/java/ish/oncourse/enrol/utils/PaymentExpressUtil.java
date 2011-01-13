package ish.oncourse.enrol.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentExpressUtil {

	public static final String PAYMENT_EXPRESS_TXN_TYPE = "Purchase";
	
	public static final DecimalFormat DECIMAL_AMOUNT_FORMATTER;
	public static final DecimalFormat IN_CENTS_AMOUNT_FORMATTER;
	static
	{
		DECIMAL_AMOUNT_FORMATTER = ( DecimalFormat )NumberFormat.getNumberInstance( Locale.US );
		DECIMAL_AMOUNT_FORMATTER.setMaximumFractionDigits( 2 );
		DECIMAL_AMOUNT_FORMATTER.setMinimumFractionDigits( 2 );
		DECIMAL_AMOUNT_FORMATTER.setMinimumIntegerDigits( 1 );
		DECIMAL_AMOUNT_FORMATTER.setGroupingUsed( false );
		//FIXME ArithmeticException is thrown with this mode.
		//DECIMAL_AMOUNT_FORMATTER.setRoundingMode( RoundingMode.UNNECESSARY );
		DECIMAL_AMOUNT_FORMATTER.setParseBigDecimal( true );
		
		IN_CENTS_AMOUNT_FORMATTER = ( DecimalFormat )NumberFormat.getNumberInstance( Locale.US );
		IN_CENTS_AMOUNT_FORMATTER.setMaximumFractionDigits( 0 );
		IN_CENTS_AMOUNT_FORMATTER.setMinimumFractionDigits( 0 );
		IN_CENTS_AMOUNT_FORMATTER.setMinimumIntegerDigits( 1 );
		IN_CENTS_AMOUNT_FORMATTER.setGroupingUsed( false );
		//IN_CENTS_AMOUNT_FORMATTER.setRoundingMode( RoundingMode.UNNECESSARY );
		IN_CENTS_AMOUNT_FORMATTER.setMultiplier( 100 );
		IN_CENTS_AMOUNT_FORMATTER.setParseBigDecimal( true );
	}
	
	public static String translateInputExpiryDate( String expiryDate )
	{
		StringBuffer buff = new StringBuffer();
		if ( expiryDate != null && ( expiryDate.matches( "^\\d{2}/?\\d{2}$" ) || expiryDate.matches( "^\\d{2}/?\\d{4}$" ) ) )
		{
			buff.append( expiryDate );
			switch ( buff.length() )
			{
				case 4:
					break;
				case 6:
					buff.replace( 2, 4, "" );
					break;
				case 7:
					buff.replace( 2, 5, "" );
					break;
			}
		}
		return buff.toString();
	}
	
	/**
	 * @param amount
	 * @return the amount as a decimal number with 2 decimal places
	 */
	public static String translateInputAmountAsDecimalString( BigDecimal amount )
	{
		return DECIMAL_AMOUNT_FORMATTER.format( amount );
	}
	
	public static boolean translateFlag(String flag) {
		return "true".equalsIgnoreCase(flag) || "1".equals(flag);
	}
}
