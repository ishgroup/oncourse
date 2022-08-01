/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicExecutePayslip } from "./EpicExecutePayslip";
import { EpicOnPayslipGenerated } from "./EpicOnPayslipGenerated";

export const EpicPayslip = combineEpics(
  EpicExecutePayslip,
  EpicOnPayslipGenerated
);