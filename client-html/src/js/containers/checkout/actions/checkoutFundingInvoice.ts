/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType } from "../../../common/actions/ActionUtils";
import { clearCommonPlainSearch, getCommonPlainRecords, setCommonPlainSearch } from "../../../common/actions/CommonPlainRecordsActions";
import { CHECKOUT_CONTACT_COLUMNS } from "../constants";

export const CHECKOUT_FUNDING_INVOICE_ADD_COMPANY = "checkout/funding/invoice/add/company";
export const CHECKOUT_FUNDING_INVOICE_TRACK_AMOUNT_OWING = "checkout/funding/invoice/track/amount/owing";
export const CHECKOUT_FUNDING_INVOICE_GET_TRAINING_PLANS = _toRequestType("checkout/funding/invoice/get/training/plans");

export const fundingInvoicePlainSearchCompanyKey: string = "fundingInvoiceCompanies";

export const setFundingInvoiceCompaniesSearch = (search: string) => setCommonPlainSearch(
  fundingInvoicePlainSearchCompanyKey,
  `isCompany == true and ~"${search}"`,
  "Contact"
);

export const onClearFundingInvoiceCompaniesSearch = () => clearCommonPlainSearch(fundingInvoicePlainSearchCompanyKey);

export const getFundingInvoiceCompanies = (offset: number) =>
  getCommonPlainRecords(fundingInvoicePlainSearchCompanyKey, offset, CHECKOUT_CONTACT_COLUMNS);

export const checkoutFundingInvoiceAddCompany = (company: any) => ({
  type: CHECKOUT_FUNDING_INVOICE_ADD_COMPANY,
  payload: { company }
});

export const checkoutFundingInvoiceToggleTrackAmountOwing = () => ({
  type: CHECKOUT_FUNDING_INVOICE_TRACK_AMOUNT_OWING
});

export const checkoutFundingInvoiceGetTrainingPlans = classId => ({
  type: CHECKOUT_FUNDING_INVOICE_GET_TRAINING_PLANS,
  payload: { classId }
});
