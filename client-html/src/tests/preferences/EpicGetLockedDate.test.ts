/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_ACCOUNT_TRANSACTION_LOCKED_DATE_FULFILLED,
  getAccountTransactionLockedDate
} from "../../js/containers/preferences/actions";
import { EpicGetLockedDate } from "../../js/containers/preferences/epics/EpicGetLockedDate";

describe("Get account transaction locked date epic tests", () => {
  it("EpicGetLockedDate should returns correct values", () => DefaultEpic({
    action: getAccountTransactionLockedDate(),
    epic: EpicGetLockedDate,
    processData: mockedApi => {
      const lockedDate = mockedApi.db.preferencesLockedDate();
      return [
        {
          type: GET_ACCOUNT_TRANSACTION_LOCKED_DATE_FULFILLED,
          payload: lockedDate
        }
      ];
    }
  }));
});
