/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { setCommonPlainSearch } from "../../../common/actions/CommonPlainRecordsActions";

export const setFundingInvoiceCompaniesSearch = (search: string) => setCommonPlainSearch(
  "Contact",
  `isCompany == true and ~"${search}"`
);

