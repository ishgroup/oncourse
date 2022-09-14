/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicGetEmailTemplatesWithKeyCode } from "./EpicGetEmailTemplatesWithKeyCode";
import { EpicManageProcess } from "./EpicManageProcess";
import { EpicInterruptProcess } from "./EpicInterruptProcess";
import { EpicGetScripts } from "./EpicGetScripts";
import { EpicCheckPermission } from "./EpicCheckPermission";
import { EpicGetUserPreferences } from "./EpicGetUserPreferences";
import { EpicSetUserPreference } from "./EpicSetUserPreference";
import { EpicExecuteActionsQueue } from "./EpicExecuteActionsQueue";
import { EpicGetCommonPlainRecords } from "./EpicGetCommonPlainRecords";

export const EpicCommon = combineEpics(
  EpicGetScripts,
  EpicManageProcess,
  EpicInterruptProcess,
  EpicCheckPermission,
  EpicSetUserPreference,
  EpicGetUserPreferences,
  EpicExecuteActionsQueue,
  EpicGetCommonPlainRecords,
  EpicGetEmailTemplatesWithKeyCode,
);
