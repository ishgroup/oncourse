/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createStringEnum } from "@api/model";

export const CHECKOUT_CONTACT_COLUMNS = "firstName,lastName,email,birthDate,isCompany,invoiceTerms,message";

export const CHECKOUT_MEMBERSHIP_COLUMNS = "sku,name,priceExTax,price_with_tax,expiryType,expiryDays";

export const CHECKOUT_VOUCHER_COLUMNS = "sku,name,priceExTax,expiryDays";

export const CHECKOUT_PRODUCT_COLUMNS = "sku,name,price_with_tax";

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
  + "isDistantLearningCourse,"
  + "message,"
  + "relatedFundingSource.id,"
  + "sessions.id,"
  + "relatedFundingSource.fundingProvider.id,"
  + "vetPurchasingContractID";

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
  [CheckoutPage.default]: "Type in student name or code in order to search",
  [CheckoutPage.contacts]: "Search for a contact by name.",
  [CheckoutPage.items]: "Search for a course, product, membership or voucher by name or code.",
  [CheckoutPage.promocodes]: "Search for a promotional discount by code",
  [CheckoutPage.summary]: "Summary",
  [CheckoutPage.payments]: "",
  [CheckoutPage.previousCredit]: "Previous credit notes",
  [CheckoutPage.previousOwing]: "Previous owing invoices",
  [CheckoutPage.fundingInvoiceCompanies]: "Search for a company by name",
  [CheckoutPage.fundingInvoiceSummary]: "Funding invoice"
};
