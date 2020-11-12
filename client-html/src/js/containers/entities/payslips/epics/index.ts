/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicGetPayslip } from "./EpicGetPayslip";
import { EpicUpdatePayslipItem } from "./EpicUpdatePayslipItem";
import { EpicCreatePayslip } from "./EpicCreatePayslip";
import { EpicDeletePayslip } from "./EpicDeletePayslip";
import { EpicExecutePayslip } from "./EpicExecutePayslip";
import { EpicOnPayslipGenerated } from "./EpicOnPayslipGenerated";

export const EpicPayslip = combineEpics(
  EpicGetPayslip,
  EpicUpdatePayslipItem,
  EpicCreatePayslip,
  EpicDeletePayslip,
  EpicExecutePayslip,
  EpicOnPayslipGenerated
);
