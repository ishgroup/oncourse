package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import ish.common.types.PaymentSource;
import ish.common.types.VoucherStatus;
import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.VoucherPaymentIn;

/**
 * Class _Voucher was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Voucher extends ProductItem {

    public static final String CODE_PROPERTY = "code";
    public static final String EXPIRY_DATE_PROPERTY = "expiryDate";
    public static final String ID_KEY_PROPERTY = "idKey";
    public static final String REDEEMED_COURSES_COUNT_PROPERTY = "redeemedCoursesCount";
    public static final String REDEMPTION_VALUE_PROPERTY = "redemptionValue";
    public static final String SOURCE_PROPERTY = "source";
    public static final String STATUS_PROPERTY = "status";
    public static final String CONTACT_PROPERTY = "contact";
    public static final String PAYMENTS_PROPERTY = "payments";
    public static final String VOUCHER_PAYMENT_INS_PROPERTY = "voucherPaymentIns";

    public static final String ID_PK_COLUMN = "id";

    public void setCode(String code) {
        writeProperty("code", code);
    }
    public String getCode() {
        return (String)readProperty("code");
    }

    public void setExpiryDate(Date expiryDate) {
        writeProperty("expiryDate", expiryDate);
    }
    public Date getExpiryDate() {
        return (Date)readProperty("expiryDate");
    }

    public void setIdKey(String idKey) {
        writeProperty("idKey", idKey);
    }
    public String getIdKey() {
        return (String)readProperty("idKey");
    }

    public void setRedeemedCoursesCount(Integer redeemedCoursesCount) {
        writeProperty("redeemedCoursesCount", redeemedCoursesCount);
    }
    public Integer getRedeemedCoursesCount() {
        return (Integer)readProperty("redeemedCoursesCount");
    }

    public void setRedemptionValue(Money redemptionValue) {
        writeProperty("redemptionValue", redemptionValue);
    }
    public Money getRedemptionValue() {
        return (Money)readProperty("redemptionValue");
    }

    public void setSource(PaymentSource source) {
        writeProperty("source", source);
    }
    public PaymentSource getSource() {
        return (PaymentSource)readProperty("source");
    }

    public void setStatus(VoucherStatus status) {
        writeProperty("status", status);
    }
    public VoucherStatus getStatus() {
        return (VoucherStatus)readProperty("status");
    }

    public void setContact(Contact contact) {
        setToOneTarget("contact", contact, true);
    }

    public Contact getContact() {
        return (Contact)readProperty("contact");
    }


    public void addToPayments(PaymentIn obj) {
        addToManyTarget("payments", obj, true);
    }
    public void removeFromPayments(PaymentIn obj) {
        removeToManyTarget("payments", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<PaymentIn> getPayments() {
        return (List<PaymentIn>)readProperty("payments");
    }


    public void addToVoucherPaymentIns(VoucherPaymentIn obj) {
        addToManyTarget("voucherPaymentIns", obj, true);
    }
    public void removeFromVoucherPaymentIns(VoucherPaymentIn obj) {
        removeToManyTarget("voucherPaymentIns", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<VoucherPaymentIn> getVoucherPaymentIns() {
        return (List<VoucherPaymentIn>)readProperty("voucherPaymentIns");
    }


}
