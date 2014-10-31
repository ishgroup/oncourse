package ish.oncourse.webservices.replication.builders;

import ish.common.types.*;
import ish.math.Money;
import ish.math.MoneyRounding;
import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.SupportedVersions;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * The bash commond line gets all getters from the Java Class:
 * cat ContactStub.java  | grep --color -E 'public\ [a-zA-Z]{1,}\ (get|is)[a-zA-Z_-0-9]{1,}\(\)' | awk '{print "contactStub."$3";"}' | uniq | sort
 */

public class StubBuilderTestHelper<E extends Queueable, S extends GenericReplicationStub> {
    private static final Logger LOGGER = Logger.getLogger(StubBuilderTestHelper.class);

    private E entity;
    private String[] excludeProperties;


    public StubBuilderTestHelper(E entity, String... excludeProperty) {
        if (entity == null)
            throw new IllegalArgumentException("parameter \"entity\" is null");
        this.entity = entity;
        this.excludeProperties = excludeProperty;
    }

    public boolean shouldProcess(String propertyName) {
        if (propertyName.equals("class"))
            return false;
        if (excludeProperties != null) {
            for (String excludeProperty : excludeProperties) {
                if (propertyName.equals(excludeProperty))
                    return false;
            }
        }

        return true;


    }


    public void assertStubBuilder(AbstractWillowStubBuilder<E, S> stubBuilder) {
        LOGGER.info(String.format("test stubBuilder - \"%s\"", stubBuilder.getClass().getSimpleName()));
        GenericReplicationStub stub = stubBuilder.convert(entity, SupportedVersions.V4);

        PropertyDescriptor[] contactDescriptors = PropertyUtils.getPropertyDescriptors(stub);
        for (PropertyDescriptor descriptor : contactDescriptors) {
            String propertyName = descriptor.getName();
            if (shouldProcess(propertyName)) {
                LOGGER.info(String.format("test property - \"%s\"", propertyName));
                Object stubValue = getStubValueBy(descriptor, stub);
                assertTrue(String.format("property \"%s\" is not set", propertyName), stubValue != null);
                Object entityValue = getEntityValueBy(descriptor);
                entityValue = convert(entityValue, stubValue.getClass());
                assertEquals("test property - " + descriptor.getName(), entityValue, stubValue);
            }
        }

    }

    /**
     * Get value for the descriptor from the entity object
     *
     * @param descriptor -  property descriptor
     * @return - value for the descriptor from the entity object
     */
    public Object getEntityValueBy(PropertyDescriptor descriptor) {
        String propertyName = descriptor.getName();
        String simpleName = this.entity.getClass().getSimpleName();

        if (entity instanceof PaymentIn) {
            switch (propertyName) {
                case "privateNotes":
                    propertyName = PaymentIn.STATUS_NOTES_PROPERTY;
                    break;
                case PaymentIn.CONFIRMATION_STATUS_PROPERTY:
                    return ((PaymentIn) entity).getConfirmationStatus().getDatabaseValue();
            }
        } else if (entity instanceof PaymentOut) {
            switch (propertyName) {
                case "amount":
                    propertyName = PaymentOut.TOTAL_AMOUNT_PROPERTY;
                    break;
                case "type":
                    return PaymentType.CREDIT_CARD.getDatabaseValue();
                case PaymentOut.CONFIRMATION_STATUS_PROPERTY:
                    return ((PaymentOut) entity).getConfirmationStatus().getDatabaseValue();
            }
        } else if (entity instanceof Taggable) {
            if (propertyName.equals("entityName")) {
                propertyName = Taggable.ENTITY_IDENTIFIER_PROPERTY;
            } else if (propertyName.equals("tagId")) {
                return ((Taggable) entity).getTaggableTags().get(0).getTag().getId();
            }
        } else if (entity instanceof TutorRole) {
            if (propertyName.equals("confirmedOn")) {
                propertyName = TutorRole.CONFIRMED_DATE_PROPERTY;
            }
        } else if (entity instanceof WaitingList) {
            if (propertyName.equals("studentCount")) {
                propertyName = WaitingList.POTENTIAL_STUDENTS_PROPERTY;
            }
        } else if (entity instanceof Certificate) {
            if (propertyName.equals("qualification")) {
                propertyName = Certificate.IS_QUALIFICATION_PROPERTY;
            }
        } else if (entity instanceof Invoice) {
            switch (propertyName) {
                case "corporatePassId":
                    return ((Invoice) entity).getCorporatePassUsed().getId();
                case Invoice.CONFIRMATION_STATUS_PROPERTY:
                    return ((Invoice) entity).getConfirmationStatus().getDatabaseValue();
            }
        } else if (entity instanceof Product) {
            if (propertyName.equals("expiryType")) {
                return ((Product) entity).getExpiryType().getDatabaseValue();
            }
        } else if (entity instanceof Voucher) {
            if (propertyName.equals(Voucher.CONFIRMATION_STATUS_PROPERTY)) {
                return ((Voucher) entity).getConfirmationStatus().getDatabaseValue();
            } else if (propertyName.equals(Voucher.STATUS_PROPERTY)) {
                return ((Voucher) entity).getStatus().getDatabaseValue();
            }
        } else if (entity instanceof ProductItem) {
            if (propertyName.equals("status")) {
                return ((ProductItem) entity).getStatus().getDatabaseValue();
            }
        } else if (entity instanceof VoucherPaymentIn) {
            if (propertyName.equals("paymentInId")) {
                return ((VoucherPaymentIn) entity).getPayment().getId();
            }
            if (propertyName.equals("status")) {
                return ((VoucherPaymentIn) entity).getStatus().getDatabaseValue();
            }
        } else if (entity instanceof EntityRelation) {
            if (propertyName.equals("fromEntityIdentifier")) {
                return ((EntityRelation) entity).getFromEntityIdentifier().getDatabaseValue();
            }
            if (propertyName.equals("toEntityIdentifier")) {
                return ((EntityRelation) entity).getToEntityIdentifier().getDatabaseValue();
            }

        } else if (entity instanceof CourseClass) {
            if (propertyName.equals("attendanceType")) {
                return ((CourseClass) entity).getAttendanceType().getDatabaseValue();
            }
        } else if (entity instanceof Enrolment) {
            switch (propertyName) {
                case "creditLevel":
                    return ((Enrolment) entity).getCreditLevel().getDatabaseValue();
                case "creditProviderType":
                    return ((Enrolment) entity).getCreditProviderType().getDatabaseValue();
                case "creditTotal":
                    return ((Enrolment) entity).getCreditTotal().getDatabaseValue();
                case "creditType":
                    return ((Enrolment) entity).getCreditType().getDatabaseValue();
                case "feeStatus":
                    return ((Enrolment) entity).getFeeStatus().getDatabaseValue();
                case "feeHelpStatus":
                    return ((Enrolment) entity).getFeeHelpStatus().getDatabaseValue();
                case "creditFoeId":
                    propertyName = Enrolment.CREDIT_FOEID_PROPERTY;
                    break;
                case Enrolment.CONFIRMATION_STATUS_PROPERTY:
                    return ((Enrolment) entity).getConfirmationStatus().getDatabaseValue();
            }
        } else if (entity instanceof Student) {
            if (propertyName.equals("citizenship")) {
                return ((Student) entity).getCitizenship().getDatabaseValue();
            }
        } else if (entity instanceof Outcome) {
            if (propertyName.equals("status")) {
                return ((Outcome) entity).getStatus().getDatabaseValue();
            }
        } else if (entity instanceof Application) {
            if (propertyName.equals(Application.CONFIRMATION_STATUS_PROPERTY)) {
                return ((Application) entity).getConfirmationStatus().getDatabaseValue();
            }
        }


        if (propertyName.equals("willowId")) {
            return entity.getId();
        }

        PropertyDescriptor expectedDescriptor;
        try {
            expectedDescriptor = PropertyUtils.getPropertyDescriptor(entity, propertyName);

            /**
             * Workaround for Boolean properties (JavaBean's name convention for Object's properties requries "get" prefix not "is")
             */
            if (expectedDescriptor == null && descriptor.getPropertyType().equals(Boolean.class)) {
                propertyName = "is" + WordUtils.capitalize(propertyName);
                expectedDescriptor = PropertyUtils.getPropertyDescriptor(entity, propertyName);
            }

        } catch (Throwable e) {
            throw new IllegalArgumentException(String.format("cannot get %s's PropertyDescriptor for property \"%s\"", simpleName, propertyName), e);
        }

        Object expected;
        if (expectedDescriptor != null) {
            try {
                expected = PropertyUtils.getProperty(entity, expectedDescriptor.getName());
            } catch (Throwable e) {
                throw new IllegalArgumentException(String.format("cannot get %s's value for property  \"%s\"", simpleName, propertyName), e);
            }
        } else {

            /**
             * Workaround for stub properties like getCountyId - entity has property without id and returns entiry not primary key
             */
            if (propertyName.endsWith("Id")) {
                propertyName = propertyName.substring(0, propertyName.length() - 2);
                try {
                    expected = PropertyUtils.getProperty(entity, propertyName);
                    Method method = ReflectionUtils.findMethod(expected.getClass(), "getId");
                    expected = method.invoke(expected);
                } catch (Throwable e) {
                    throw new IllegalArgumentException(String.format("cannot get %s's value for property  \"%s\"", simpleName, propertyName), e);
                }
            } else if (propertyName.equals("entityIdentifier")) {
                expected = entity.getObjectId().getEntityName();
            } else {
                throw new IllegalArgumentException(String.format("cannot get %s's value for property  \"%s\"", simpleName, propertyName));
            }
        }
        return expected;
    }

    /**
     * Get value for the descriptor from the stub object
     *
     * @param descriptor -  property descriptor
     * @param stub       -  stub object
     * @return - value for the descriptor from the stub object
     */
    public Object getStubValueBy(PropertyDescriptor descriptor, GenericReplicationStub stub) {
        Object actual;
        try {
            if (descriptor.getReadMethod() == null) {
                String readMethod = "is" + WordUtils.capitalize(descriptor.getName());
                Method method = ReflectionUtils.findMethod(stub.getClass(), readMethod);
                actual = method.invoke(stub);
            } else {
                actual = PropertyUtils.getProperty(stub, descriptor.getName());
            }

        } catch (Throwable e) {
            throw new IllegalArgumentException(String.format("cannot get %s's value for property  \"%s\"", stub.getClass().getSimpleName(), descriptor.getName()), e);
        }
        return actual;


    }


    /**
     * The method was introduced because there is deference between stub-property type and entity-property type, example:
     * CourseStub.nominalHours and Course.nominalHours
     *
     * @param expectedValue - entity-property value
     * @param actualClass   - stub-property type
     * @return - converted value
     */
    @SuppressWarnings("rawtypes")
    public Object convert(Object expectedValue, Class actualClass) {
        Class expectedClass = expectedValue.getClass();
        if (expectedClass == Float.class && actualClass == BigDecimal.class)
            return new BigDecimal((Float) expectedValue);
        if (expectedClass == Double.class && actualClass == BigDecimal.class)
            return new BigDecimal((Double) expectedValue);
        else if (expectedClass == Money.class && actualClass == BigDecimal.class)
            return ((Money) expectedValue).toBigDecimal();
        else if (expectedClass == DiscountType.class && actualClass == Integer.class)
            return ((DiscountType) expectedValue).getDatabaseValue();
        else if (expectedClass == MoneyRounding.class && actualClass == Integer.class)
            return ((MoneyRounding) expectedValue).getDatabaseValue();
        else if (expectedClass == PaymentSource.class && actualClass == String.class)
            return ((PaymentSource) expectedValue).getDatabaseValue();
        else if (expectedClass == EnrolmentStatus.class && actualClass == Integer.class)
            return ((EnrolmentStatus) expectedValue).getDatabaseValue();
        else if (expectedClass == EnrolmentStatus.class && actualClass == String.class)
            return ((EnrolmentStatus) expectedValue).name();
        else if (expectedClass == MessageStatus.class && actualClass == Integer.class)
            return ((MessageStatus) expectedValue).getDatabaseValue();
        else if (expectedClass == MessageType.class && actualClass == Integer.class)
            return ((MessageType) expectedValue).getDatabaseValue();
        else if (expectedClass == CreditCardType.class && actualClass == String.class)
            return ((CreditCardType) expectedValue).getDatabaseValue();
        else if (expectedClass == PaymentStatus.class && actualClass == Integer.class)
            return ((PaymentStatus) expectedValue).getDatabaseValue();
        else if (expectedClass == AttachmentInfoVisibility.class && actualClass == Integer.class)
            return ((AttachmentInfoVisibility) expectedValue).getDatabaseValue();
        else if (expectedClass == PaymentType.class && actualClass == Integer.class)
            return ((PaymentType) expectedValue).getDatabaseValue();
        else if (expectedClass == AvetmissStudentDisabilityType.class && actualClass == Integer.class)
            return ((AvetmissStudentDisabilityType) expectedValue).getDatabaseValue();
        else if (expectedClass == AvetmissStudentEnglishProficiency.class && actualClass == Integer.class)
            return ((AvetmissStudentEnglishProficiency) expectedValue).getDatabaseValue();
        else if (expectedClass == AvetmissStudentSchoolLevel.class && actualClass == Integer.class)
            return ((AvetmissStudentSchoolLevel) expectedValue).getDatabaseValue();
        else if (expectedClass == AvetmissStudentIndigenousStatus.class && actualClass == Integer.class)
            return ((AvetmissStudentIndigenousStatus) expectedValue).getDatabaseValue();
        else if (expectedClass == AvetmissStudentIndigenousStatus.class && actualClass == Integer.class)
            return ((AvetmissStudentIndigenousStatus) expectedValue).getDatabaseValue();
        else if (expectedClass == AvetmissStudentPriorEducation.class && actualClass == Integer.class)
            return ((AvetmissStudentPriorEducation) expectedValue).getDatabaseValue();
        else if (expectedClass == UsiStatus.class && actualClass == Integer.class)
            return ((UsiStatus) expectedValue).getDatabaseValue();
        else if (expectedClass == ApplicationStatus.class && actualClass == Integer.class)
            return ((ApplicationStatus) expectedValue).getDatabaseValue();
        else if (expectedClass == CourseEnrolmentType.class && actualClass == Integer.class)
            return ((CourseEnrolmentType) expectedValue).getDatabaseValue();
        return expectedValue;


    }

}
