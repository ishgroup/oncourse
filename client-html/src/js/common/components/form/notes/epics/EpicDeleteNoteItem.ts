/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../epics/EpicUtils";
import FetchErrorHandler from "../../../../api/fetch-errors-handlers/FetchErrorHandler";
import { DELETE_NOTE_ITEM } from "../actions";
import NotesService from "../services/NotesService";

const request: EpicUtils.Request<any, number> = {
  type: DELETE_NOTE_ITEM,
  hideLoadIndicator: true,
  getData: id => NotesService.remove(id),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to delete note")
};

export const EpicDeleteNoteItem: Epic<any, any> = EpicUtils.Create(request);
