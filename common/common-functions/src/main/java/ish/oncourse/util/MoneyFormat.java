package ish.oncourse.util;

import ish.math.Country;
import ish.math.Money;

import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class MoneyFormat extends Format {

    private NumberFormat format;

    private MoneyFormat() {

    }

    @Override       
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        Object value = obj;
        if (value instanceof Money) {
            value = ((Money) value).toBigDecimal();
        }
        return format.format(value, toAppendTo, pos);
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return format.parseObject(source, pos);
    }
    
    public static Format getInstance(int minDigits) {
        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(Country.AUSTRALIA.locale());
        currencyInstance.setMinimumFractionDigits(minDigits);
        
        MoneyFormat format = new MoneyFormat();
        format.format = currencyInstance;
        return format;
    }
} 
