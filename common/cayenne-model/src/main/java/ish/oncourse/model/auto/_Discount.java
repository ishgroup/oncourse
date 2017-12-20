package ish.oncourse.model.auto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Property;

import ish.common.types.DiscountType;
import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.model.College;
import ish.oncourse.model.CorporatePassDiscount;
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.model.DiscountMembership;
import ish.oncourse.model.InvoiceLineDiscount;

/**
 * Class _Discount was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Discount extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CODE_PROPERTY = "code";
    public static final String COMBINATION_TYPE_PROPERTY = "combinationType";
    public static final String CREATED_PROPERTY = "created";
    public static final String DETAIL_PROPERTY = "detail";
    public static final String DISCOUNT_AMOUNT_PROPERTY = "discountAmount";
    public static final String DISCOUNT_RATE_PROPERTY = "discountRate";
    public static final String DISCOUNT_TYPE_PROPERTY = "discountType";
    public static final String HIDE_ON_WEB_PROPERTY = "hideOnWeb";
    public static final String IS_AVAILABLE_ON_WEB_PROPERTY = "isAvailableOnWeb";
    public static final String MAXIMUM_DISCOUNT_PROPERTY = "maximumDiscount";
    public static final String MIN_ENROLMENTS_PROPERTY = "minEnrolments";
    public static final String MIN_VALUE_PROPERTY = "minValue";
    public static final String MINIMUM_DISCOUNT_PROPERTY = "minimumDiscount";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String ROUNDING_MODE_PROPERTY = "roundingMode";
    public static final String STUDENT_AGE_PROPERTY = "studentAge";
    public static final String STUDENT_AGE_OPERATOR_PROPERTY = "studentAgeOperator";
    public static final String STUDENT_ENROLLED_WITHIN_DAYS_PROPERTY = "studentEnrolledWithinDays";
    public static final String STUDENT_POSTCODES_PROPERTY = "studentPostcodes";
    public static final String STUDENTS_QUALIFIER_PROPERTY = "studentsQualifier";
    public static final String TIME_ZONE_PROPERTY = "timeZone";
    public static final String VALID_FROM_PROPERTY = "validFrom";
    public static final String VALID_FROM_OFFSET_PROPERTY = "validFromOffset";
    public static final String VALID_TO_PROPERTY = "validTo";
    public static final String VALID_TO_OFFSET_PROPERTY = "validToOffset";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String CORPORATE_PASS_DISCOUNTS_PROPERTY = "corporatePassDiscounts";
    public static final String DISCOUNT_CONCESSION_TYPES_PROPERTY = "discountConcessionTypes";
    public static final String DISCOUNT_COURSE_CLASSES_PROPERTY = "discountCourseClasses";
    public static final String DISCOUNT_MEMBERSHIP_PRODUCTS_PROPERTY = "discountMembershipProducts";
    public static final String INVOICE_LINE_DISCOUNTS_PROPERTY = "invoiceLineDiscounts";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = Property.create("angelId", Long.class);
    public static final Property<String> CODE = Property.create("code", String.class);
    public static final Property<Boolean> COMBINATION_TYPE = Property.create("combinationType", Boolean.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<String> DETAIL = Property.create("detail", String.class);
    public static final Property<Money> DISCOUNT_AMOUNT = Property.create("discountAmount", Money.class);
    public static final Property<BigDecimal> DISCOUNT_RATE = Property.create("discountRate", BigDecimal.class);
    public static final Property<DiscountType> DISCOUNT_TYPE = Property.create("discountType", DiscountType.class);
    public static final Property<Boolean> HIDE_ON_WEB = Property.create("hideOnWeb", Boolean.class);
    public static final Property<Boolean> IS_AVAILABLE_ON_WEB = Property.create("isAvailableOnWeb", Boolean.class);
    public static final Property<Money> MAXIMUM_DISCOUNT = Property.create("maximumDiscount", Money.class);
    public static final Property<Integer> MIN_ENROLMENTS = Property.create("minEnrolments", Integer.class);
    public static final Property<Money> MIN_VALUE = Property.create("minValue", Money.class);
    public static final Property<Money> MINIMUM_DISCOUNT = Property.create("minimumDiscount", Money.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<String> NAME = Property.create("name", String.class);
    public static final Property<MoneyRounding> ROUNDING_MODE = Property.create("roundingMode", MoneyRounding.class);
    public static final Property<Integer> STUDENT_AGE = Property.create("studentAge", Integer.class);
    public static final Property<String> STUDENT_AGE_OPERATOR = Property.create("studentAgeOperator", String.class);
    public static final Property<Integer> STUDENT_ENROLLED_WITHIN_DAYS = Property.create("studentEnrolledWithinDays", Integer.class);
    public static final Property<String> STUDENT_POSTCODES = Property.create("studentPostcodes", String.class);
    public static final Property<byte[]> STUDENTS_QUALIFIER = Property.create("studentsQualifier", byte[].class);
    public static final Property<String> TIME_ZONE = Property.create("timeZone", String.class);
    public static final Property<Date> VALID_FROM = Property.create("validFrom", Date.class);
    public static final Property<Integer> VALID_FROM_OFFSET = Property.create("validFromOffset", Integer.class);
    public static final Property<Date> VALID_TO = Property.create("validTo", Date.class);
    public static final Property<Integer> VALID_TO_OFFSET = Property.create("validToOffset", Integer.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<List<CorporatePassDiscount>> CORPORATE_PASS_DISCOUNTS = Property.create("corporatePassDiscounts", List.class);
    public static final Property<List<DiscountConcessionType>> DISCOUNT_CONCESSION_TYPES = Property.create("discountConcessionTypes", List.class);
    public static final Property<List<DiscountCourseClass>> DISCOUNT_COURSE_CLASSES = Property.create("discountCourseClasses", List.class);
    public static final Property<List<DiscountMembership>> DISCOUNT_MEMBERSHIP_PRODUCTS = Property.create("discountMembershipProducts", List.class);
    public static final Property<List<InvoiceLineDiscount>> INVOICE_LINE_DISCOUNTS = Property.create("invoiceLineDiscounts", List.class);

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setCode(String code) {
        writeProperty("code", code);
    }
    public String getCode() {
        return (String)readProperty("code");
    }

    public void setCombinationType(Boolean combinationType) {
        writeProperty("combinationType", combinationType);
    }
    public Boolean getCombinationType() {
        return (Boolean)readProperty("combinationType");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setDetail(String detail) {
        writeProperty("detail", detail);
    }
    public String getDetail() {
        return (String)readProperty("detail");
    }

    public void setDiscountAmount(Money discountAmount) {
        writeProperty("discountAmount", discountAmount);
    }
    public Money getDiscountAmount() {
        return (Money)readProperty("discountAmount");
    }

    public void setDiscountRate(BigDecimal discountRate) {
        writeProperty("discountRate", discountRate);
    }
    public BigDecimal getDiscountRate() {
        return (BigDecimal)readProperty("discountRate");
    }

    public void setDiscountType(DiscountType discountType) {
        writeProperty("discountType", discountType);
    }
    public DiscountType getDiscountType() {
        return (DiscountType)readProperty("discountType");
    }

    public void setHideOnWeb(Boolean hideOnWeb) {
        writeProperty("hideOnWeb", hideOnWeb);
    }
    public Boolean getHideOnWeb() {
        return (Boolean)readProperty("hideOnWeb");
    }

    public void setIsAvailableOnWeb(Boolean isAvailableOnWeb) {
        writeProperty("isAvailableOnWeb", isAvailableOnWeb);
    }
    public Boolean getIsAvailableOnWeb() {
        return (Boolean)readProperty("isAvailableOnWeb");
    }

    public void setMaximumDiscount(Money maximumDiscount) {
        writeProperty("maximumDiscount", maximumDiscount);
    }
    public Money getMaximumDiscount() {
        return (Money)readProperty("maximumDiscount");
    }

    public void setMinEnrolments(Integer minEnrolments) {
        writeProperty("minEnrolments", minEnrolments);
    }
    public Integer getMinEnrolments() {
        return (Integer)readProperty("minEnrolments");
    }

    public void setMinValue(Money minValue) {
        writeProperty("minValue", minValue);
    }
    public Money getMinValue() {
        return (Money)readProperty("minValue");
    }

    public void setMinimumDiscount(Money minimumDiscount) {
        writeProperty("minimumDiscount", minimumDiscount);
    }
    public Money getMinimumDiscount() {
        return (Money)readProperty("minimumDiscount");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void setRoundingMode(MoneyRounding roundingMode) {
        writeProperty("roundingMode", roundingMode);
    }
    public MoneyRounding getRoundingMode() {
        return (MoneyRounding)readProperty("roundingMode");
    }

    public void setStudentAge(Integer studentAge) {
        writeProperty("studentAge", studentAge);
    }
    public Integer getStudentAge() {
        return (Integer)readProperty("studentAge");
    }

    public void setStudentAgeOperator(String studentAgeOperator) {
        writeProperty("studentAgeOperator", studentAgeOperator);
    }
    public String getStudentAgeOperator() {
        return (String)readProperty("studentAgeOperator");
    }

    public void setStudentEnrolledWithinDays(Integer studentEnrolledWithinDays) {
        writeProperty("studentEnrolledWithinDays", studentEnrolledWithinDays);
    }
    public Integer getStudentEnrolledWithinDays() {
        return (Integer)readProperty("studentEnrolledWithinDays");
    }

    public void setStudentPostcodes(String studentPostcodes) {
        writeProperty("studentPostcodes", studentPostcodes);
    }
    public String getStudentPostcodes() {
        return (String)readProperty("studentPostcodes");
    }

    public void setStudentsQualifier(byte[] studentsQualifier) {
        writeProperty("studentsQualifier", studentsQualifier);
    }
    public byte[] getStudentsQualifier() {
        return (byte[])readProperty("studentsQualifier");
    }

    public void setTimeZone(String timeZone) {
        writeProperty("timeZone", timeZone);
    }
    public String getTimeZone() {
        return (String)readProperty("timeZone");
    }

    public void setValidFrom(Date validFrom) {
        writeProperty("validFrom", validFrom);
    }
    public Date getValidFrom() {
        return (Date)readProperty("validFrom");
    }

    public void setValidFromOffset(Integer validFromOffset) {
        writeProperty("validFromOffset", validFromOffset);
    }
    public Integer getValidFromOffset() {
        return (Integer)readProperty("validFromOffset");
    }

    public void setValidTo(Date validTo) {
        writeProperty("validTo", validTo);
    }
    public Date getValidTo() {
        return (Date)readProperty("validTo");
    }

    public void setValidToOffset(Integer validToOffset) {
        writeProperty("validToOffset", validToOffset);
    }
    public Integer getValidToOffset() {
        return (Integer)readProperty("validToOffset");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void addToCorporatePassDiscounts(CorporatePassDiscount obj) {
        addToManyTarget("corporatePassDiscounts", obj, true);
    }
    public void removeFromCorporatePassDiscounts(CorporatePassDiscount obj) {
        removeToManyTarget("corporatePassDiscounts", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<CorporatePassDiscount> getCorporatePassDiscounts() {
        return (List<CorporatePassDiscount>)readProperty("corporatePassDiscounts");
    }


    public void addToDiscountConcessionTypes(DiscountConcessionType obj) {
        addToManyTarget("discountConcessionTypes", obj, true);
    }
    public void removeFromDiscountConcessionTypes(DiscountConcessionType obj) {
        removeToManyTarget("discountConcessionTypes", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DiscountConcessionType> getDiscountConcessionTypes() {
        return (List<DiscountConcessionType>)readProperty("discountConcessionTypes");
    }


    public void addToDiscountCourseClasses(DiscountCourseClass obj) {
        addToManyTarget("discountCourseClasses", obj, true);
    }
    public void removeFromDiscountCourseClasses(DiscountCourseClass obj) {
        removeToManyTarget("discountCourseClasses", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DiscountCourseClass> getDiscountCourseClasses() {
        return (List<DiscountCourseClass>)readProperty("discountCourseClasses");
    }


    public void addToDiscountMembershipProducts(DiscountMembership obj) {
        addToManyTarget("discountMembershipProducts", obj, true);
    }
    public void removeFromDiscountMembershipProducts(DiscountMembership obj) {
        removeToManyTarget("discountMembershipProducts", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DiscountMembership> getDiscountMembershipProducts() {
        return (List<DiscountMembership>)readProperty("discountMembershipProducts");
    }


    public void addToInvoiceLineDiscounts(InvoiceLineDiscount obj) {
        addToManyTarget("invoiceLineDiscounts", obj, true);
    }
    public void removeFromInvoiceLineDiscounts(InvoiceLineDiscount obj) {
        removeToManyTarget("invoiceLineDiscounts", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<InvoiceLineDiscount> getInvoiceLineDiscounts() {
        return (List<InvoiceLineDiscount>)readProperty("invoiceLineDiscounts");
    }


}
