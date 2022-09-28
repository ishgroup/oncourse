/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Account, AccountType } from "@api/model";
import { Dispatch } from "redux";
import { getCommonPlainRecords, setCommonPlainSearch } from "../../../../common/actions/CommonPlainRecordsActions";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../../constants/Config";

export const getPlainAccounts = (dispatch: Dispatch, type?: AccountType) => {
  dispatch(setCommonPlainSearch("Account", `isEnabled is true${type ? ` and type is ${type.toUpperCase()}` : ""}`));
  dispatch(getCommonPlainRecords("Account", 0, "description,accountCode,type,tax.id", true, "description", PLAIN_LIST_MAX_PAGE_SIZE));
};