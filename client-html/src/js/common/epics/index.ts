/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from 'redux-observable';
import { EpicCheckPermission } from './EpicCheckPermission';
import { EpicExecuteActionsQueue } from './EpicExecuteActionsQueue';
import { EpicGetCommonPlainRecords } from './EpicGetCommonPlainRecords';
import { EpicGetEmailTemplatesWithKeyCode } from './EpicGetEmailTemplatesWithKeyCode';
import { EpicGetLogoPreferences } from './EpicGetLogoPreferences';
import { EpicGetScripts } from './EpicGetScripts';
import { EpicGetUserPreferences } from './EpicGetUserPreferences';
import { EpicInterruptProcess } from './EpicInterruptProcess';
import { EpicManageProcess } from './EpicManageProcess';
import { EpicSetUserPreference } from './EpicSetUserPreference';

export const EpicCommon = combineEpics(
  EpicGetScripts,
  EpicManageProcess,
  EpicInterruptProcess,
  EpicCheckPermission,
  EpicSetUserPreference,
  EpicGetUserPreferences,
  EpicExecuteActionsQueue,
  EpicGetLogoPreferences,
  EpicGetCommonPlainRecords,
  EpicGetEmailTemplatesWithKeyCode,
);
