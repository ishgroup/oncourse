/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicCreateScriptItem } from "./EpicCreateScriptItem";
import { EpicDeleteScriptItem } from "./EpicDeleteScriptItem";
import { EpicGetRunScriptResult } from "./EpicGetRunScriptResult";
import { EpicGetScriptItem } from "./EpicGetScriptItem";
import { EpicGetScriptsList } from "./EpicGetScriptsList";
import { EpicGetTimeZone } from "./EpicGetTimeZone";
import { EpicOpenRunScriptPdf } from "./EpicOpenRunScriptPdf";
import { EpicRunScriptItem } from "./EpicRunScriptItem";
import { EpicSaveScriptItem } from "./EpicSaveScriptItem";

export const EpicScripts = combineEpics(
  EpicGetScriptItem,
  EpicSaveScriptItem,
  EpicCreateScriptItem,
  EpicDeleteScriptItem,
  EpicRunScriptItem,
  EpicGetScriptsList,
  EpicGetRunScriptResult,
  EpicOpenRunScriptPdf,
  EpicGetTimeZone,
);
