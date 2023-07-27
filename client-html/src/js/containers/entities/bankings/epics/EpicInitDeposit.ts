/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ofType } from "redux-observable";

import { initialize } from "redux-form";
import { getDepositAccounts, INIT_DEPOSIT } from "../actions";
import { mergeMap } from "rxjs/operators";
import { setListEditRecord } from "../../../../common/components/list-view/actions";
import { Banking } from "@api/model";
import { format } from "date-fns";
import { getAccountTransactionLockedDate } from "../../../preferences/actions";
import { YYYY_MM_DD_MINUSED } from  "ish-ui";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

export const EpicInitDeposit = actions$ => {
  return actions$.pipe(
    ofType(INIT_DEPOSIT),
    mergeMap(() => {
      const initialValue: Banking = {
        settlementDate: format(new Date(), YYYY_MM_DD_MINUSED)
      };
      return [
        setListEditRecord(initialValue),
        initialize(LIST_EDIT_VIEW_FORM_NAME, initialValue),
        getAccountTransactionLockedDate(),
        getDepositAccounts()
      ];
    })
  );
};
