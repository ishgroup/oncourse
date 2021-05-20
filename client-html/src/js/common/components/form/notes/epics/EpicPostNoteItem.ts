/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Note } from "@api/model";
import * as EpicUtils from "../../../../epics/EpicUtils";
import FetchErrorHandler from "../../../../api/fetch-errors-handlers/FetchErrorHandler";
import { POST_NOTE_ITEM } from "../actions";
import NotesService from "../services/NotesService";

const request: EpicUtils.Request<any, { note: Note }> = {
  type: POST_NOTE_ITEM,
  hideLoadIndicator: true,
  getData: ({ note }) => NotesService.create(note.entityName, note.entityId, note),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to create note")
};

export const EpicPostNoteItem: Epic<any, any> = EpicUtils.Create(request);
