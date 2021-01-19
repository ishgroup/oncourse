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

package ish.oncourse.server.entity.mixins

import com.google.inject.Inject
import com.google.inject.Injector
import groovy.transform.CompileDynamic
import ish.oncourse.cayenne.PersistentObjectI
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.Application
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.server.cayenne.Banking
import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.server.cayenne.ClassCost
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.Discount
import ish.oncourse.server.cayenne.DiscountCourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.FieldConfigurationScheme
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceDueDate
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.Product
import ish.oncourse.server.cayenne.ProductItem
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.cayenne.Session
import ish.oncourse.server.cayenne.SessionModule
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.cayenne.VoucherProduct
import ish.oncourse.server.cayenne.WaitingList
import ish.print.PrintableObject

class MixinHelper {

	static Injector injector

	static initMixins() {
		Account.mixin(AccountMixin)
		AccountTransaction.mixin(AccountTransactionMixin)
		Application.mixin(ApplicationMixin)
		BigDecimal.mixin(BigDecimalMixin)
		Certificate.mixin(CertificateMixin)
		ClassCost.mixin(ClassCostMixin)
		Contact.mixin(ContactMixin)
		CorporatePass.mixin(CorporatePassMixin)
		CourseClass.mixin(CourseClassMixin)
		CourseClassTutor.mixin(CourseClassTutorMixin)
		DiscountCourseClass.mixin(DiscountCourseClassMixin)
		Discount.mixin(DiscountMixin)
		Enrolment.mixin(EnrolmentMixin)
		FieldConfigurationScheme.mixin(FieldConfigurationSchemeMixin)
		InvoiceDueDate.mixin(InvoiceDueDateMixin)
		InvoiceLine.mixin(InvoiceLineMixin)
		Invoice.mixin(InvoiceMixin)
		Outcome.mixin(OutcomeMixin)
		PaymentIn.mixin(PaymentInMixin)
		PaymentOut.mixin(PaymentOutMixin)
		Script.mixin(ScriptMixin)
		Session.mixin(SessionMixin)
		SessionModule.mixin(SessionModuleMixin)
		Student.mixin(StudentMixin)
		SystemUser.mixin(SystemUserMixin)
		WaitingList.mixin(WaitingListMixin)
		Banking.mixin(BankingMixin)
		Product.mixin(ProductMixin)
		ArticleProduct.mixin(ArticleProductMixin)
		VoucherProduct.mixin(VoucherProductMixin)
		ProductItem.mixin(ProductItemMixin)
		Message.mixin(MessageMixin)
	}

	@Inject
    MixinHelper(Injector injector) {
		MixinHelper.injector = injector

		initMixins()
	}

	static <T> T getService(Class<T> clazz) {
		return injector.getInstance(clazz)
	}

	@CompileDynamic
	static getProperty(Object object, String propertyName) {
		try {
			return object."${propertyName}"
		} catch (MissingPropertyException e) {
			if (object instanceof PersistentObjectI || object instanceof PrintableObject) {
				return object.getValueForKey(propertyName)
			} else {
				throw e
			}
		}
	}
}
