/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic, ofType } from "redux-observable";
import { mergeMap } from "rxjs/operators";
import { Observable } from "rxjs";
import { State } from "../../../../reducers/state";
import { PAYROLL_PROCESS_FINISHED } from "../../payrolls/actions";
import { openInternalLink } from "ish-ui";

export const EpicOnPayslipGenerated: Epic<any, State> = (action$: Observable<any>): any => action$.pipe(
    ofType(PAYROLL_PROCESS_FINISHED),
    mergeMap(action => {
      const { maxPayslipId } = action.payload;

      if (maxPayslipId) {
        openInternalLink(`/payslip/?search=id > ${maxPayslipId} `);
      }
      return [];
    })
  );
