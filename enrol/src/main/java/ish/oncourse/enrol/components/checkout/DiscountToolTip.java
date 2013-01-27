package ish.oncourse.enrol.components.checkout;

import ish.oncourse.model.Discount;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.util.List;

public class DiscountToolTip {

    @Parameter(required = true)
    @Property
    private String toolTipId;

    @Parameter(required = true)
    @Property
    private List<Discount> discounts;

    @Property
    private Discount discount;
}
