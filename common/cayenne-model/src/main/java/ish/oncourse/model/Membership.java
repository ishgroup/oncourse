package ish.oncourse.model;

import java.util.Date;
import ish.common.types.PaymentSource;
import ish.common.types.ProductStatus;
import ish.oncourse.common.field.*;
import ish.oncourse.model.auto._Membership;
import ish.oncourse.utils.DateUtils;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.util.DateTimeUtil;
import ish.util.ProductUtil;
import org.apache.cayenne.exp.Expression;

@Type(value = ContextType.MEMBERSHIP)
public class Membership extends _Membership implements Queueable {
	private static final long serialVersionUID = -7624202404417919994L;
	
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public void setStatus(ProductStatus status) {
		if (ProductStatus.ACTIVE.equals(status)
			&& getInvoiceLine() != null
			&& PaymentSource.SOURCE_WEB.equals(getInvoiceLine().getInvoice().getSource())
			&& (getStatus() == null || ProductStatus.NEW.equals(getStatus()))) {

		
			Date expiry = DateUtils.endOfDay(new Date());
			Expression membershipExp = Membership.PRODUCT.eq(getProduct()).andExp(Membership.EXPIRY_DATE.gt(expiry)).andExp(Membership.STATUS.eq(ProductStatus.ACTIVE));

			for (Membership membership : membershipExp.filterObjects(getContact().getMemberships())) {

				if (!membership.equals(this)) {
					expiry.setTime(expiry.after(membership.getExpiryDate()) ? expiry.getTime() : membership.getExpiryDate().getTime());
				}
			}

			if (DateTimeUtil.getDaysLeapYearDaylightSafe(new Date(), expiry) > 0) {
				Date renewalDate = ProductUtil.calculateExpiryDate(expiry, getProduct().getExpiryType(), getProduct().getExpiryDays());
				setExpiryDate(renewalDate);
			}
				
		}
		
		super.setStatus(status);
	}

	@Property(value = FieldProperty.CUSTOM_FIELD_MEMBERSHIP, type = PropertyGetSetFactory.SET, params = {String.class, String.class})
	public void setCustomFieldValue(String key, String value) {
		setCustomFieldValue(key, value, MembershipCustomField.class);
	}

	@Property(value = FieldProperty.CUSTOM_FIELD_MEMBERSHIP, type = PropertyGetSetFactory.GET, params = {String.class})
	public String getCustomFieldValue(String key) {
		CustomField field = getCustomField(key);
		return  field == null ? null : field.getValue();
	}
}
