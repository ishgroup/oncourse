package ish.oncourse.enrol.components.checkout;

import ish.math.Money;
import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.model.Course;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Voucher;
import ish.oncourse.services.voucher.IVoucherService;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.MoneyFormatter;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.text.Format;
import java.text.ParseException;
import java.util.List;

public class ProductListItem {
	private static final Logger logger = LogManager.getLogger();
	@Parameter(required = true)
	private PurchaseController purchaseController;

	@Property
	@Parameter(required = true)
	private ish.oncourse.model.ProductItem productItem;

	@Parameter(required = true)
	@Property
	private Integer contactIndex;

	@Parameter(required = true)
	@Property
	private Integer productItemIndex;

	@Parameter(required = false)
	private ProductItemDelegate delegate;

	@Property
	@Parameter(required = true)
	private Boolean checked;

	@Parameter
	private Block blockToRefresh;

	@Property
	private Course course;

    @Property
    private VoucherValue voucherValue;


	@Inject
	private Request request;

    @Inject
    private IVoucherService voucherService;

	@SetupRender
	void beforeRender() {

        if (productItem instanceof Voucher)
        {
            voucherValue = new VoucherValue();
            voucherValue.setVoucher((Voucher) productItem);
        }
	}


    public boolean isVoucherProduct(){
        return productItem instanceof Voucher;
    }


    public String getEnrolmentClass() {
        return checked ? StringUtils.EMPTY: "disabled";
    }

	public Money getPrice() {
        InvoiceLine invoiceLine = productItem.getInvoiceLine();
        if(invoiceLine != null){
            return invoiceLine.getPriceEachIncTax();
        }
		return Money.ZERO;
	}



	@OnEvent(value = "selectProductEvent")
	public Object selectProductItem(Integer contactIndex, Integer productItemIndex) {
		if (!request.isXHR())
			return null;

		MoneyFormatter formatter = MoneyFormatter.getInstance();

        String priceValue =  StringUtils.trimToNull(request.getParameter("priceValue"));
        Money price = Money.ZERO;
        if (priceValue != null)
        {
			try {
				price = (Money) formatter.stringToValue(priceValue);
			} catch (ParseException e) {
				price = null;
			}
        }

		if (delegate != null) {
			delegate.onChange(contactIndex, productItemIndex, price);
			if (blockToRefresh != null)
				return blockToRefresh;
		}
		return null;
	}

	@OnEvent(value = "updateProductEvent")
	public Object updateProductEvent(Integer contactIndex, Integer productItemIndex) {
		if (!request.isXHR())
			return null;
		if (delegate != null) {
			delegate.onUpdate(contactIndex, productItemIndex, getUpdatedPrice());
			if (blockToRefresh != null)
				return blockToRefresh;
		}
		return null;
	}
	
	private Money getUpdatedPrice() {
		MoneyFormatter formatter = MoneyFormatter.getInstance();

		String priceValue =  StringUtils.trimToNull(request.getParameter("priceValue"));
		Money price = Money.ZERO;
		if (priceValue != null) {
			try {
				price =  (Money) formatter.stringToValue(priceValue);
			} catch (ParseException e) {
				logger.info(e);
			}
		}
		return price;
	}

    public Format moneyFormat(Money money)
    {
        return FormatUtils.chooseMoneyFormat(money);
    }


	public static interface ProductItemDelegate {
		public void onChange(Integer contactIndex, Integer productItemIndex, Money price);

		public void onUpdate(Integer contactIndex, Integer productItemIndex, Money price);

	}

    public Integer[] getActionContext() {
        return new Integer[]{contactIndex, productItemIndex};
    }

    public class VoucherValue
    {
        private Voucher voucher;

        public boolean hasPrice()
        {
           return !voucherService.isVoucherWithoutPrice(voucher.getVoucherProduct());
        }

        public boolean hasCourses()
        {
           return !voucher.getVoucherProduct().getRedemptionCourses().isEmpty();
        }

        public Voucher getVoucher() {
            return voucher;
        }

        public void setVoucher(Voucher voucher) {
            this.voucher = voucher;
        }

        public Money getValue()
        {
            return voucher.getVoucherProduct().getValue();
        }

        public List<Course> getCourses()
        {
            return voucher.getVoucherProduct().getRedemptionCourses();
        }
    }
	
	public boolean isEditablePrice() {
		return isVoucherProduct() && !voucherValue.hasPrice();
	}

}
