/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from 'redux-observable';
import { EpicCheckoutCreateContact } from './contact/EpicCheckoutCreateContact';
import { EpicCheckoutGetContact } from './contact/EpicCheckoutGetContact';
import { EpicCheckoutGetRelatedContacts } from './contact/EpicCheckoutGetRelatedContacts';
import { EpicCheckoutUpdateContact } from './contact/EpicCheckoutUpdateContact';
import { EpicTriggerFundingInvoiceCalculate } from './fundingInvoice/EpicTriggerFundingInvoiceCalculate';
import { EpicCheckoutGetClassPaymentPlans } from './item/EpicCheckoutGetClassPaymentPlans';
import { EpicGetCourseClassList } from './item/EpicCheckoutGetCourseClassList';
import { EpicCheckoutGetMemberShip } from './item/EpicCheckoutGetMembership';
import { EpicCheckoutGetProduct } from './item/EpicCheckoutGetProduct';
import { EpicCheckoutGetVoucher } from './item/EpicCheckoutGetVoucher';
import { EpicGetItemRelations } from './item/EpicGetItemRelations';
import { EpicCheckoutGetActivePaymentTypes } from './payment/EpicCheckoutGetActivePaymentTypes';
import { EpicCheckoutGetPaymentStatusDetails } from './payment/EpicCheckoutGetPaymentStatusDetails';
import { EpicCheckoutGetSavedCard } from './payment/EpicCheckoutGetSavedCard';
import { EpicCheckoutProcessEwayPayment } from './payment/EpicCheckoutProcessEwayPayment';
import { EpicCheckoutProcessPayment } from './payment/EpicCheckoutProcessPayment';
import { EpicCheckoutProcessStripeCCPayment } from './payment/EpicCheckoutProcessStripeCCPayment';
import { EpicCompleteWindcavePaymentRedirect } from './payment/EpicCompleteWindcavePaymentRedirect';
import { EpicGetPaymentInfoByReference } from './payment/EpicGetPaymentInfoByReference';
import { EpicCheckoutGetPromoCode } from './summary/EpicCheckoutGetPromoCode';
import { EpicCheckoutGetVoucherToRedeem } from './summary/EpicCheckoutGetVoucherToRedeem';
import { EpicGetPreviousCredit } from './summary/EpicGetPreviousCredit';
import { EpicGetPreviousOwing } from './summary/EpicGetPreviousOwing';
import { EpicGetVoucherRedeemer } from './summary/EpicGetVoucherRedeemer';
import { EpicTriggerPricesUpdate } from './summary/EpicTriggerPricesUpdate';
import { EpicUpdateClassesDiscounts } from './summary/EpicUpdateClassesDiscounts';
import { EpicUpdateSummaryPrices } from './summary/EpicUpdateSummaryPrices';

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
  EpicCheckoutProcessPayment,
  EpicCompleteWindcavePaymentRedirect,
  EpicCheckoutProcessEwayPayment,
  EpicGetPaymentInfoByReference,
  EpicCheckoutProcessStripeCCPayment,
  EpicCheckoutGetPromoCode,
  EpicCheckoutGetRelatedContacts,
  EpicGetCourseClassList,
  EpicCheckoutGetClassPaymentPlans,
  EpicCheckoutGetPaymentStatusDetails,
  EpicUpdateSummaryPrices,
  EpicGetPreviousOwing,
  EpicGetPreviousCredit,
  EpicCheckoutGetVoucherToRedeem,
  EpicGetVoucherRedeemer,
  EpicTriggerFundingInvoiceCalculate
);
