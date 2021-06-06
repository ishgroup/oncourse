/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Account, AccountType } from "@api/model";
import { sortDefaultSelectItems } from "../../../common/utils/common";

export const getAccountsList = (accounts: Account[], type: AccountType) => {
  const list = accounts
    .filter(acc => type === acc.type)
    .map(acc => ({
      value: String(acc.id),
      label: `${acc.description} ${acc.accountCode}`
    }));

  list.sort(sortDefaultSelectItems);

  return list;
};
