/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import { createStringEnum } from "@api/model";

export const CHECKOUT_CONTACT_COLUMNS = "firstName,lastName,middleName,email,birthDate,isCompany,invoiceTerms,message";

export const CHECKOUT_MEMBERSHIP_COLUMNS = "sku,name,priceExTax,price_with_tax,expiryType,expiryDays";

export const CHECKOUT_VOUCHER_COLUMNS = "sku,name,priceExTax,expiryDays";

export const CHECKOUT_PRODUCT_COLUMNS = "sku,name,price_with_tax";

export const CHECKOUT_STORED_STATE_KEY = "stored_checkout_state";

export const CHECKOUT_COURSE_CLASS_COLUMNS = "course.name,"
  + "course.code,"
  + "code,"
  + "feeIncGst,"
  + "startDateTime,"
  + "endDateTime,"
  + "placesLeft,"
  + "room.name,"
  + "room.site.name,"
  + "room.site.localTimezone,"
  + "sessions.tutors.contact.fullName,"
  + "sessions.start,sessions.end,"
  + "paymentPlanLines.id,"
  + "course.isVET,"
  + "type,"
  + "message,"
  + "relatedFundingSource.id,"
  + "sessions.id,"
  + "relatedFundingSource.fundingProvider.id,"
  + "vetPurchasingContractID,"
  + "course.id";

export const CheckoutCurrentStep = createStringEnum([
  "shoppingCart",
  "summary",
  "fundingInvoice",
  "payment"
]);

export type CheckoutCurrentStepType = keyof typeof CheckoutCurrentStep;

export const CheckoutPage = createStringEnum([
  "default",
  "contacts",
  "items",
  "promocodes",
  "summary",
  "payments",
  "previousCredit",
  "previousOwing",
  "fundingInvoiceCompanies",
  "fundingInvoiceSummary"
]);

export type CheckoutPageType = keyof typeof CheckoutPage;

export const titles = {
  [CheckoutPage.default]: $t('type_in_student_name'),
  [CheckoutPage.contacts]: $t('search_for_a_contact'),
  [CheckoutPage.items]: $t('search_for_a_course'),
  [CheckoutPage.promocodes]: $t('search_for_a_promotional'),
  [CheckoutPage.summary]: $t('summary'),
  [CheckoutPage.payments]: "",
  [CheckoutPage.previousCredit]: $t('previous_credit_notes'),
  [CheckoutPage.previousOwing]: $t('previous_owing_invoices'),
  [CheckoutPage.fundingInvoiceCompanies]: $t('search_for_a_company'),
  [CheckoutPage.fundingInvoiceSummary]: $t('funding_invoice')
};
