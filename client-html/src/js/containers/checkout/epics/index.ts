/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicCheckoutGetContact } from "./contact/EpicCheckoutGetContact";
import { EpicCheckoutCreateContact } from "./contact/EpicCheckoutCreateContact";
import { EpicCheckoutGetRelatedContacts } from "./contact/EpicCheckoutGetRelatedContacts";
import { EpicCheckoutUpdateContact } from "./contact/EpicCheckoutUpdateContact";
import { EpicTriggerFundingInvoiceCalculate } from "./fundingInvoice/EpicTriggerFundingInvoiceCalculate";
import { EpicGetCourseClassList } from "./item/EpicCheckoutGetCourseClassList";
import { EpicCheckoutGetMemberShip } from "./item/EpicCheckoutGetMembership";
import { EpicCheckoutGetProduct } from "./item/EpicCheckoutGetProduct";
import { EpicCheckoutGetVoucher } from "./item/EpicCheckoutGetVoucher";
import { EpicCheckoutGetActivePaymentTypes } from "./payment/EpicCheckoutGetActivePaymentTypes";
import { EpicCheckoutGetPaymentStatusDetails } from "./payment/EpicCheckoutGetPaymentStatusDetails";
import { EpicCheckoutProcessCcPayment } from "./payment/EpicCheckoutProcessCcPayment";
import { EpicCheckoutGetSavedCard } from "./payment/EpicCheckoutGetSavedCard";
import { EpicCheckoutGetPromoCode } from "./summary/EpicCheckoutGetPromoCode";
import { EpicUpdateClassesDiscounts } from "./summary/EpicUpdateClassesDiscounts";
import { EpicTriggerPricesUpdate } from "./summary/EpicTriggerPricesUpdate";
import { EpicUpdateSummaryPrices } from "./summary/EpicUpdateSummaryPrices";
import { EpicGetPreviousOwing } from "./summary/EpicGetPreviousOwing";
import { EpicCheckoutGetVoucherToRedeem } from "./summary/EpicCheckoutGetVoucherToRedeem";
import { EpicGetVoucherRedeemer } from "./summary/EpicGetVoucherRedeemer";
import { EpicCheckoutGetClassPaymentPlans } from "./item/EpicCheckoutGetClassPaymentPlans";
import { EpicGetItemRelations } from "./item/EpicGetItemRelations";

export const EpicCheckout = combineEpics(
  EpicGetItemRelations,
  EpicUpdateClassesDiscounts,
  EpicTriggerPricesUpdate,
  EpicCheckoutGetSavedCard,
  EpicCheckoutGetContact,
  EpicCheckoutCreateContact,
  EpicCheckoutUpdateContact,
  EpicCheckoutGetMemberShip,
  EpicCheckoutGetProduct,
  EpicCheckoutGetVoucher,
  EpicCheckoutGetActivePaymentTypes,
  EpicCheckoutProcessCcPayment,
  EpicCheckoutGetPromoCode,
  EpicCheckoutGetRelatedContacts,
  EpicGetCourseClassList,
  EpicCheckoutGetClassPaymentPlans,
  EpicCheckoutGetPaymentStatusDetails,
  EpicUpdateSummaryPrices,
  EpicGetPreviousOwing,
  EpicCheckoutGetVoucherToRedeem,
  EpicGetVoucherRedeemer,
  EpicTriggerFundingInvoiceCalculate
);
