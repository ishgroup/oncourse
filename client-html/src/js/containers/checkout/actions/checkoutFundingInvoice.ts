/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { clearCommonPlainSearch, getCommonPlainRecords, setCommonPlainSearch } from "../../../common/actions/CommonPlainRecordsActions";
import { CHECKOUT_CONTACT_COLUMNS } from "../constants";

export const fundingInvoicePlainSearchCompanyKey: string = "fundingInvoiceCompanies";

export const setFundingInvoiceCompaniesSearch = (search: string) => setCommonPlainSearch(
  fundingInvoicePlainSearchCompanyKey,
  `isCompany == true and ~"${search}"`,
  "Contact"
);

export const onClearFundingInvoiceCompaniesSearch = () => clearCommonPlainSearch(fundingInvoicePlainSearchCompanyKey);

export const getFundingInvoiceCompanies = (offset: number) =>
  getCommonPlainRecords(fundingInvoicePlainSearchCompanyKey, offset, CHECKOUT_CONTACT_COLUMNS);
