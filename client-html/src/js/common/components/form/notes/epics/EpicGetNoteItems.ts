/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Note } from "@api/model";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../epics/EpicUtils";
import FetchErrorHandler from "../../../../api/fetch-errors-handlers/FetchErrorHandler";
import { GET_NOTE_ITEMS } from "../actions";
import NotesService from "../services/NotesService";

const request: EpicUtils.Request<Note[], { entityName: string; entityId: number; form: string }> = {
  type: GET_NOTE_ITEMS,
  hideLoadIndicator: true,
  getData: ({ entityName, entityId }) => NotesService.get(entityName, entityId),
  processData: (notes, s, { form }) => {
    notes.sort((a, b) => (a.modified > b.modified ? -1 : 1));
    return s.form[form] ? [initialize(form, { ...s.form[form].initial, notes })] : [];
  },
  processError: response => FetchErrorHandler(response, "Failed to get notes")
};

export const EpicGetNoteItems: Epic<any, any> = EpicUtils.Create(request);
