package ish.oncourse.server.lifecycle


import groovy.transform.CompileStatic
import ish.common.types.*
import ish.math.Money
import ish.oncourse.generator.DataGenerator
import ish.oncourse.server.cayenne.*
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

import java.time.LocalDate

@CompileStatic
class SampleEntityBuilder {

    private ObjectContext ctx
    private Account accountPayable
    private Account accountIncome
    private Account accountReceivable

    private SampleEntityBuilder(ObjectContext ctx) {
        this.ctx = ctx
        accountPayable = SelectById.query(Account, 800L).selectOne(ctx)
        accountIncome = SelectById.query(Account, 500L).selectOne(ctx)
        accountReceivable = SelectById.query(Account, 700L).selectOne(ctx)
    }

    static SampleEntityBuilder newBuilder(ObjectContext ctx) {
        return new SampleEntityBuilder(ctx)
    }

    
    Enrolment createEnrolment(InvoiceLine invoiceLine, Student student, CourseClass courseClass) {
        Enrolment enrl = this.ctx.newObject(Enrolment.class)
        enrl.addToAbstractInvoiceLines(invoiceLine)
        enrl.setSource(PaymentSource.SOURCE_WEB)
        enrl.setStatus(EnrolmentStatus.SUCCESS)
        enrl.setStudent(student)
        enrl.setCourseClass(courseClass)
        return enrl
    }

    
    CourseClass createCourseClass(Course course) {
        CourseClass c = this.ctx.newObject(CourseClass.class)
        c.setIsCancelled(false)
        c.setCode("12345")
        c.setMaximumPlaces(3)
        c.setCourse(course)
        c.setFeeExGst(new Money(123, 25))
        c.setMaterials("Test materials")
        c.setMaximumPlaces(18)
        c.setMinimumPlaces(10)
        c.setAttendanceType(CourseClassAttendanceType.NO_INFORMATION)

        c.setIncomeAccount(accountIncome)

        Tax tax = this.ctx.newObject(Tax.class)
        tax.setCreatedOn(new Date())
        tax.setIsGSTTaxType(false)

        tax.setPayableToAccount(accountPayable)
        tax.setReceivableFromAccount(accountReceivable)
        tax.setTaxCode("AVVB")
        c.setTax(tax)

        return c
    }

    
    Course createCourse() {
        Course c = this.ctx.newObject(Course.class)
        c.setCode("123")
        c.setFieldOfEducation("computers skills")
        c.setName("computer literance")
        c.setFieldOfEducation("math")
        Qualification qualification = SelectById.query(Qualification.class, 1).selectOne(ctx)
        c.setQualification(qualification)
        c.setFieldConfigurationSchema(DataGenerator.valueOf(ctx).getFieldConfigurationScheme())
        c.setFeeHelpClass(false)
        return c
    }

    
    InvoiceLine createInvoiceLine(Invoice invoice) {
        InvoiceLine invLine = this.ctx.newObject(InvoiceLine.class)
        invLine.setTitle("Test invoice line")
        invLine.setDescription("Test invoice line.")

        Tax tax = this.ctx.newObject(Tax.class)
        tax.setCreatedOn(new Date())
        tax.setIsGSTTaxType(false)

        tax.setPayableToAccount(accountPayable)
        tax.setReceivableFromAccount(accountReceivable)
        tax.setTaxCode("AVVB")
        tax.setRate(new BigDecimal("0.1"))
        invLine.setTax(tax)

        Account invoiceLineAccount = accountIncome
        invLine.setAccount(invoiceLineAccount)
        invoice.setDebtorsAccount(invoiceLineAccount)

        Account prepaidFeesAccount = SelectById.query(Account.class, 1000).selectOne(ctx)
        invLine.setPrepaidFeesAccount(prepaidFeesAccount)

        invLine.setDiscountEachExTax(new Money(123, 25))
        invLine.setInvoice(invoice)
        invLine.setPriceEachExTax(new Money(123, 25))
        invLine.setQuantity(new BigDecimal(5))
        invLine.setPrepaidFeesAccount(SelectById.query(Account.class, 900).selectOne(ctx))

        invLine.setTaxEach(new Money(10, 1))
        invLine.setUnit("unit")
        return invLine
    }

    
    Invoice createInvoice(Contact contact) {
        Invoice inv = this.ctx.newObject(Invoice.class)
        inv.setAmountOwing(new Money(new BigDecimal(10)))
        inv.setContact(contact)
        inv.setCustomerReference("customer reference")
        inv.setDateDue(LocalDate.now())
        inv.setDescription("test description")
        inv.setInvoiceDate(LocalDate.now())
        inv.setPublicNotes("test public notes")
        inv.setShippingAddress("test shipping address")
        inv.setSource(PaymentSource.SOURCE_WEB)
        return inv
    }

    
    Student createStudent(Contact contact) {
        Student st = this.ctx.newObject(Student.class)
        st.setContact(contact)
        Country country = SelectById.query(Country.class, 1).selectOne(ctx)
        st.setCountryOfBirth(country)
        st.setDisabilityType(AvetmissStudentDisabilityType.HEARING)
        st.setEnglishProficiency(AvetmissStudentEnglishProficiency.VERY_WELL)
        st.setHighestSchoolLevel(AvetmissStudentSchoolLevel.COMPLETED_YEAR_11)
        st.setIndigenousStatus(AvetmissStudentIndigenousStatus.ABORIGINAL)
        st.setIsOverseasClient(true)
        st.setIsStillAtSchool(true)
        Language lang = SelectById.query(Language.class, 2).selectOne(ctx)
        st.setLanguage(lang)
        st.setPriorEducationCode(AvetmissStudentPriorEducation.MISC)
        st.setYearSchoolCompleted(1989)
        return st
    }

    
    Contact createContact() {
        Contact c = this.ctx.newObject(Contact.class)
        Country country = SelectById.query(Country.class, 1).selectOne(ctx)
        c.setCountry(country)
        c.setBirthDate(LocalDate.now().minusDays(1))
        c.setEmail("test@gmail.com")
        c.setLastName("Putin")
        c.setFax("777-771")
        c.setFirstName("Vladimir")
        c.setHomePhone("777-771")
        c.setIsCompany(true)
        c.setGender(Gender.MALE)
        c.setAllowEmail(true)
        c.setAllowPost(true)
        c.setAllowSms(true)
        c.setMobilePhone("777-771")
        c.setPostcode("777AB")
        c.setState("NSW")
        c.setStreet("30 Avoca st.")
        c.setSuburb("Randwick")
        return c
    }

    
    Message createSMSMessage() {
        Message message = this.ctx.newObject(Message.class)
        message.setSmsText("sms")
        return message
    }

    
    Message buildMessage(Contact contact, Message message, MessageType type) {
        message.setContact(contact)
        message.setType(type)

        switch (type) {
            case MessageType.SMS:
                message.setDestinationAddress(contact.getMobilePhone())
                break
            case MessageType.EMAIL:
                message.setDestinationAddress(contact.getEmail())
                break
            case MessageType.POST:
                message.setDestinationAddress(contact.getAddress())
                break
        }

        message.setNumberOfAttempts(3)
        message.setStatus(MessageStatus.QUEUED)
        return message
    }

    
    Message createEmailMessage() {
        Message message = this.ctx.newObject(Message.class)
        message.setEmailBody("emailBody")
        message.setEmailFrom("emailFrom")
        message.setEmailHtmlBody("emailHtmlBody")
        message.setEmailSubject("emailSubject")
        return message
    }
}
