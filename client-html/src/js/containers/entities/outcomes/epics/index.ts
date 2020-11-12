/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicGetOutcome } from "./EpicGetOutcome";
import { EpicUpdateOutcomeItem } from "./EpicUpdateOutcomeItem";
import { EpicDeleteOutcome } from "./EpicDeleteOutcome";
import { EpicCreateOutcome } from "./EpicCreateOutcome";
import { EpicGetOutcomeTags } from "./EpicGetOutcomeTags";

export const EpicOutcome = combineEpics(
  EpicGetOutcome,
  EpicUpdateOutcomeItem,
  EpicDeleteOutcome,
  EpicCreateOutcome,
  EpicGetOutcomeTags
);
