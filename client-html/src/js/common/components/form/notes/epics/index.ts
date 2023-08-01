/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineEpics } from "redux-observable";
import { EpicDeleteNoteItem } from "./EpicDeleteNoteItem";
import { EpicGetNoteItems } from "./EpicGetNoteItems";
import { EpicPostNoteItem } from "./EpicPostNoteItem";
import { EpicPutNoteItem } from "./EpicPutNoteItem";

export const EpicNotes = combineEpics(EpicGetNoteItems, EpicPostNoteItem, EpicPutNoteItem, EpicDeleteNoteItem);
