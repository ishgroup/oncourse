/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createStringEnum } from "@api/model";

export const CHECKOUT_CONTACT_COLUMNS = "firstName,lastName,email,birthDate,isCompany,invoiceTerms,message";

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

export type CheckoutCurrentStep = keyof typeof CheckoutCurrentStep;
