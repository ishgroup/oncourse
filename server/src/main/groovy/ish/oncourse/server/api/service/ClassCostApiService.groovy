/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.oncourse.server.api.dao.DiscountDao
import static ish.oncourse.server.api.servlet.ApiFilter.validateOnly
import ish.common.types.AccountType
import ish.common.types.ClassCostFlowType
import ish.common.types.ClassCostRepetitionType
import ish.common.types.KeyCode
import ish.common.types.Mask
import ish.math.Money
import ish.oncourse.server.api.dao.AccountDao
import ish.oncourse.server.api.dao.ClassCostDao
import ish.oncourse.server.api.dao.CourseClassDao
import ish.oncourse.server.api.dao.TaxDao
import ish.oncourse.server.api.v1.function.DiscountFunctions
import ish.oncourse.server.api.v1.model.ClassCostDTO
import ish.oncourse.server.api.v1.model.ClassCostFlowTypeDTO
import ish.oncourse.server.api.v1.model.ClassCostRepetitionTypeDTO
import ish.oncourse.server.api.v1.model.CourseClassDiscountDTO
import ish.oncourse.server.api.v1.model.CourseClassPaymentPlanDTO
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.ClassCost
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassPaymentPlanLine
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.DiscountCourseClass
import ish.oncourse.server.cayenne.Tax
import ish.oncourse.server.security.api.IPermissionService
import ish.util.MoneyUtil
import org.apache.cayenne.ObjectContext

class ClassCostApiService extends EntityApiService<ClassCostDTO, ClassCost, ClassCostDao>{

    @Inject
    private IPermissionService permissionService

    @Inject
    private CourseClassDao courseClassDao

    @Inject
    private CourseClassApiService classApiService

    @Inject
    private TaxDao taxDao

    @Inject
    private AccountDao accountDao

    @Inject
    private DiscountDao discountDao

    @Inject
    private ContactApiService contactApiService

    @Inject
    private CourseClassTutorApiService tutorService

    @Override
    Class<ClassCost> getPersistentClass() {
        return ClassCost
    }

    @Override
    ClassCostDTO toRestModel(ClassCost cayenneModel) {
        ClassCostDTO ccDto = new ClassCostDTO()
        ccDto.id = cayenneModel.id
        ccDto.taxId = cayenneModel.tax?.id
        ccDto.courseClassid = cayenneModel.courseClass.id
        ccDto.description = cayenneModel.description
        ccDto.flowType = ClassCostFlowTypeDTO.values()[0].fromDbType(cayenneModel.flowType)
        ccDto.repetitionType = ClassCostRepetitionTypeDTO.values()[0].fromDbType(cayenneModel.repetitionType)
        ccDto.isOverriden = cayenneModel.flowType == ClassCostFlowType.WAGES && cayenneModel.perUnitAmountExTax != null
        if (ccDto.isOverriden) {
            ccDto.perUnitAmountExTax  = cayenneModel.perUnitAmountExTax?.toBigDecimal()
        } else {
            ccDto.perUnitAmountExTax = cayenneModel.calcPerUnitAmountExTax?.toBigDecimal()
        }

        if (cayenneModel.flowType in [ClassCostFlowType.INCOME , ClassCostFlowType.EXPENSE ]) {
            ccDto.perUnitAmountIncTax = MoneyUtil.getPriceIncTax(cayenneModel.perUnitAmountExTax, cayenneModel.tax.rate, cayenneModel.taxAdjustment).toBigDecimal()
        }

        ccDto.payableOnEnrolment = cayenneModel.payableOnEnrolment
        ccDto.invoiceToStudent = cayenneModel.invoiceToStudent
        ccDto.contactId = cayenneModel.contact?.id
        ccDto.contactName = cayenneModel.contact?.fullName
        ccDto.isSunk = cayenneModel.isSunk
        ccDto.maximumCost = cayenneModel.maximumCost?.toBigDecimal()
        ccDto.minimumCost = cayenneModel.minimumCost?.toBigDecimal()
        ccDto.actualAmount = cayenneModel.calcActualCost?.toBigDecimal()
        ccDto.onCostRate = cayenneModel.onCostRate
        ccDto.unitCount = cayenneModel.unitCount
        ccDto.courseClassTutorId = cayenneModel.tutorRole?.id
        ccDto.tutorRole = cayenneModel.tutorRole?.definedTutorRole?.name

        if (ClassCostDao.studentFee.call(cayenneModel)) {
            ccDto.setAccountId(cayenneModel.courseClass.incomeAccount?.id)
            ccDto.setTaxId(cayenneModel.tax?.id)
            cayenneModel.courseClass.paymentPlanLines.each { pp ->
                ccDto.paymentPlan << new CourseClassPaymentPlanDTO(dayOffset: pp.dayOffset, amount: pp.amount.toBigDecimal())
            }
        } else if (ClassCostFlowType.DISCOUNT == cayenneModel.flowType) {
            ccDto.courseClassDiscount = new CourseClassDiscountDTO()
            ccDto.courseClassDiscount.forecast = cayenneModel.discountCourseClass.predictedStudentsPercentage
            ccDto.courseClassDiscount.discountOverride = cayenneModel.discountCourseClass.discountDollar?.toBigDecimal()
            ccDto.courseClassDiscount.discount = DiscountFunctions.toRestDiscount(cayenneModel.discountCourseClass.discount, false)
        }
        ccDto
    }

    @Override
    ClassCost toCayenneModel(ClassCostDTO dto, ClassCost cayenneModel) {

        boolean
        ObjectContext context = cayenneModel.context
        CourseClass courseClass
        if (dto.courseClassid) {
            courseClass = courseClassDao.getById(context, dto.courseClassid)
            cayenneModel.courseClass = courseClass
            courseClass.modifiedOn = new Date()
        }

        cayenneModel.description = dto.description
        cayenneModel.flowType = dto.flowType.dbType
        cayenneModel.repetitionType = dto.repetitionType?.dbType
        cayenneModel.perUnitAmountExTax = Money.valueOf(dto.perUnitAmountExTax)
        cayenneModel.unitCount = dto.unitCount

        if (dto.maximumCost != null) {
            cayenneModel.maximumCost = Money.valueOf(dto.maximumCost)
        } else {
            cayenneModel.maximumCost = null
        }

        if (dto.minimumCost != null) {
            cayenneModel.minimumCost = Money.valueOf(dto.minimumCost)
        } else {
            cayenneModel.minimumCost = null
        }

        switch (cayenneModel.flowType) {
            case ClassCostFlowType.INCOME:
                updateTax(cayenneModel, dto)
                cayenneModel.isSunk = false
                if (cayenneModel.payableOnEnrolment) {
                    cayenneModel.invoiceToStudent = true
                    cayenneModel.repetitionType = ClassCostRepetitionType.PER_ENROLMENT
                    if (courseClass) {
                        courseClass.tax = cayenneModel.tax
                        courseClass.incomeAccount = accountDao.getById(context, dto.accountId)
                        updatePaymentPlan(dto, courseClass)
                    }
                } else {
                    cayenneModel.invoiceToStudent = false
                    updateContact(cayenneModel, dto)
                }
                break
            case ClassCostFlowType.EXPENSE:
                cayenneModel.invoiceToStudent = false
                cayenneModel.isSunk = dto.isSunk
                updateTax(cayenneModel, dto)
                updateContact(cayenneModel, dto)
                break
            case ClassCostFlowType.WAGES:
                if (dto.courseClassTutorId) {
                    cayenneModel.tutorRole = tutorService.getEntityAndValidateExistence(context, dto.courseClassTutorId)
                }
                if (!dto.isOverriden) {
                    cayenneModel.perUnitAmountExTax = null
                }
                updateContact(cayenneModel, dto)
                cayenneModel.onCostRate = dto.onCostRate
                cayenneModel.isSunk = dto.isSunk
                cayenneModel.invoiceToStudent = false
                break
            case ClassCostFlowType.DISCOUNT:
                DiscountCourseClass dcc
                if (cayenneModel.discountCourseClass) {
                    dcc = cayenneModel.discountCourseClass
                } else {
                    dcc = context.newObject(DiscountCourseClass)
                    dcc.courseClass = courseClass
                    dcc.discount = discountDao.getById(context, dto.courseClassDiscount.discount.id)
                    cayenneModel.discountCourseClass = dcc
                }

                if (dto.courseClassDiscount.discountOverride != null) {
                    dcc.discountDollar = Money.valueOf(dto.courseClassDiscount.discountOverride)
                } else {
                    dcc.discountDollar = null
                }

                if (dto.courseClassDiscount.forecast != null) {
                    dcc.predictedStudentsPercentage = dto.courseClassDiscount.forecast
                } else {
                    dcc.predictedStudentsPercentage = null
                }

                break
        }
        cayenneModel
    }

    void updateTax(ClassCost cost, ClassCostDTO dto) {
        Tax tax = taxDao.getById(cost.context, dto.taxId)
        Money incTax = Money.valueOf(dto.perUnitAmountIncTax)
        Money taxAdjustment =  MoneyUtil.calculateTaxAdjustment(incTax, cost.perUnitAmountExTax, tax.rate)
        cost.tax = tax
        cost.taxAdjustment = taxAdjustment
    }


    void updateContact(ClassCost cost, ClassCostDTO dto) {
        if (dto.contactId) {
            cost.contact = contactApiService.getEntityAndValidateExistence(cost.context, dto.contactId)
        }
    }

    @Override
    void validateModelBeforeSave(ClassCostDTO dto, ObjectContext context, Long id) {

        CourseClass courseClass = null
        if (!(validateOnly.get() && id == null && dto.courseClassid == null)){
            courseClass = classApiService.getEntityAndValidateExistence(context, dto.courseClassid)
        }

        if (dto.flowType == null) {
            validator.throwClientErrorException(id, 'flowType', 'Class cost type is required')
        }

        if (dto.repetitionType == null && dto.flowType != ClassCostFlowTypeDTO.WAGES) {
            validator.throwClientErrorException(id, 'repetitionType', 'Class cost type is required')
        }

        if (dto.repetitionType != null && dto.repetitionType == ClassCostRepetitionTypeDTO.PER_UNIT && dto.unitCount == null) {
            validator.throwClientErrorException(id, 'unitCount', 'Unit count is required')
        }

        switch (dto.flowType) {
            case ClassCostFlowTypeDTO.EXPENSE:
                validateIncTax(dto,context,id)
                break
            case ClassCostFlowTypeDTO.INCOME:
                validateIncTax(dto,context,id)
                if (dto.payableOnEnrolment) {
                    if (!dto.paymentPlan.empty &&
                            dto.paymentPlan*.amount.inject { a, b -> a.add(b) } != dto.perUnitAmountIncTax ) {
                        validator.throwClientErrorException(id, 'perUnitAmountIncTax', 'Payment plan total doesn\'t match Enrolment fee ')
                    }

                    if (dto.accountId == null) {
                        validator.throwClientErrorException(id, 'accountId', 'Enroolment fee requre account')
                    }
                    Account account =  accountDao.getById(context, dto.accountId)
                    if (account == null) {
                        validator.throwClientErrorException(id, 'accountId', 'Account doesn\'t exist')
                    }

                    if (account.type != AccountType.INCOME) {
                        validator.throwClientErrorException(id, 'accountId', 'Account should be income')
                    }
                }
                break
            case ClassCostFlowTypeDTO.WAGES:

                boolean isUserCanCreateAndEditWages = permissionService.currentUserCan(KeyCode.PAYSLIP, Mask.EDIT)
                if (!isUserCanCreateAndEditWages) {
                    validator.throwForbiddenErrorException(id, 'flowType', 'You have not permission to create or edit tutor wage.')
                }

                if (!(validateOnly.get() && id == null && dto.courseClassTutorId == null)) {
                    if (dto.courseClassTutorId == null) {
                        validator.throwClientErrorException(id, 'courseClassTutorId', 'Tutor role is mandatory for wages')
                    }
                    CourseClassTutor tutor = tutorService.getEntityAndValidateExistence(context, dto.courseClassTutorId)
                    if (dto.courseClassid != tutor.courseClass.id) {
                        validator.throwClientErrorException(id, 'courseClassid', 'Tutor role is wrong')
                    }
                }

                boolean isUserCanOverrideTutorPayRate = permissionService.currentUserCan(KeyCode.SPECIAL_OVERRIDE_TUTOR_PAYRATE, Mask.ALL)

                if (!isUserCanOverrideTutorPayRate && dto.isOverriden) {
                    validator.throwForbiddenErrorException(id, 'repetitionType', 'You have not permission to override tutor pay rate.')
                }
                break
            case ClassCostFlowTypeDTO.DISCOUNT:
                if (dto.courseClassDiscount.discount.id == null && dto.courseClassid == null) {
                    validator.throwClientErrorException(id, 'courseClassid', 'Discount cost cannot be added to class')
                }
                break
            case ClassCostFlowTypeDTO.CUSTOM_INVOICE:
                validator.throwClientErrorException(id, 'flowType', 'Custom invoices cannot be modified')
        }

        if (id != null) {
            ClassCost cost = getEntityAndValidateExistence(context, id)
            if ((cost.flowType != dto.flowType.dbType) || (cost.payableOnEnrolment != dto.payableOnEnrolment)) {
                validator.throwClientErrorException(id, 'flowType', 'Cost type cannot be modified')
            }

            if (cost.courseClass != courseClass)  {
                validator.throwClientErrorException(id, 'courseClassid', 'Class cannot be modified')
            }
        } else {
            if (dto.payableOnEnrolment ) {
                validator.throwClientErrorException(id, 'payableOnEnrolment', 'Class has only one enrolment fee')
            }
        }
    }

    void validateIncTax(ClassCostDTO dto, ObjectContext context, Long id) {
        if (dto.taxId == null || taxDao.getById(context, dto.taxId) == null) {
            validator.throwClientErrorException(id, 'taxId', 'Tax is required')
        }
        if (dto.perUnitAmountIncTax == null) {
            validator.throwClientErrorException(id, 'perUnitAmountIncTax', 'Total amount include tax required')
        }
    }

    @Override
    void validateModelBeforeRemove(ClassCost cayenneModel) {
        if (cayenneModel.flowType == ClassCostFlowType.INCOME && cayenneModel.payableOnEnrolment ) {
            validator.throwClientErrorException(cayenneModel.id, 'flowType', 'Class should has enrolment fee')
        }
    }

    @Override
    List<ClassCostDTO> getList(Long classId) {
        ObjectContext context = cayenneService.newContext

        CourseClass courseClass = courseClassDao.getById(context, classId)

        List<ClassCostDTO> result = courseClass.costs.collect {toRestModel(it)}
        courseClass.invoiceLines.each { il ->
            ClassCostDTO ccDto = new ClassCostDTO()
            ccDto.courseClassid = courseClass.id
            ccDto.taxId = il.tax?.id
            ccDto.description = il.title
            ccDto.flowType = ClassCostFlowTypeDTO.CUSTOM_INVOICE
            ccDto.perUnitAmountExTax = il.finalPriceToPayExTax.toBigDecimal()
            ccDto.actualAmount = il.finalPriceToPayExTax.toBigDecimal()
            ccDto.setContactId(il.enrolment?.student?.contact?.id)
            ccDto.setContactName(il.enrolment?.student?.contact?.fullName)
            ccDto.setAccountId(il.account?.id)
            ccDto.invoiceId = il.invoice.id
            result << ccDto

        }
        return result
    }

    private updatePaymentPlan(ClassCostDTO dto, CourseClass courseClass) {
        ObjectContext context = courseClass.objectContext
        context.deleteObjects(courseClass.paymentPlanLines)
        if (dto.paymentPlan.size()) {
            dto.paymentPlan.each { l ->
                CourseClassPaymentPlanLine line = context.newObject(CourseClassPaymentPlanLine)
                line.dayOffset = l.dayOffset
                line.amount = Money.valueOf(l.amount)
                line.courseClass = courseClass
            }
        }
    }

}
