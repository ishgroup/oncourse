/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Banking } from "@api/model";
import { format } from "date-fns";
import { YYYY_MM_DD_MINUSED } from "ish-ui";

import { initialize } from "redux-form";
import { ofType } from "redux-observable";
import { mergeMap } from "rxjs/operators";
import { setListEditRecord } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { getAccountTransactionLockedDate } from "../../../preferences/actions";
import { getDepositAccounts, INIT_DEPOSIT } from "../actions";

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
