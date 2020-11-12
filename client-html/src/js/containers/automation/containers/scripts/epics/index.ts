/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicGetRunScriptResult } from "./EpicGetRunScriptResult";
import { EpicGetScriptItem } from "./EpicGetScriptItem";
import { EpicOpenRunScriptPdf } from "./EpicOpenRunScriptPdf";
import { EpicSaveScriptItem } from "./EpicSaveScriptItem";
import { EpicCreateScriptItem } from "./EpicCreateScriptItem";
import { EpicDeleteScriptItem } from "./EpicDeleteScriptItem";
import { EpicRunScriptItem } from "./EpicRunScriptItem";
import { EpicGetScriptsList } from "./EpicGetScriptsList";

export const EpicScripts = combineEpics(
  EpicGetScriptItem,
  EpicSaveScriptItem,
  EpicCreateScriptItem,
  EpicDeleteScriptItem,
  EpicRunScriptItem,
  EpicGetScriptsList,
  EpicGetRunScriptResult,
  EpicOpenRunScriptPdf
);
