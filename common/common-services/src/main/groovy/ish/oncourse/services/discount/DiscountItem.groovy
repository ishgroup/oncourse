package ish.oncourse.services.discount

import groovy.transform.CompileStatic
import ish.math.Money
import ish.oncourse.model.Discount
import ish.oncourse.util.FormatUtils
import org.apache.commons.lang.StringUtils

import java.text.Format

@CompileStatic
class DiscountItem {
    private static final String DIVIDER = " / "
    List<Discount> discounts
    private Money feeIncTax


    private Format feeFormat
    private String title

    DiscountItem init() {
        List<String> strings = new ArrayList<>()
        for (Discount discount : discounts) {
            strings.add(discount.getName())
        }
        title = StringUtils.join(strings, DIVIDER)
        feeFormat = FormatUtils.chooseMoneyFormat(feeIncTax)
        return this
    }

    String getTitle() {
        return title;
    }

    Money getFeeIncTax() {
        return feeIncTax
    }


    Format getFeeFormat() {
        return feeFormat
    }

}