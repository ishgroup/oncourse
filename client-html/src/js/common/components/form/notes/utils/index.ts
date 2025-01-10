/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Note } from "@api/model";
import { QueuedAction } from "../../../../../model/common/ActionsQueue";
import { fieldUpdateHandler } from "../../../../utils/actionsQueue";
import { postNoteItem, putNoteItem } from "../actions";
import NotesService from "../services/NotesService";

export const validateNoteCreate = (note: Note) => NotesService.validateCreate(note.entityName, note.entityId, note);
export const validateNoteUpdate = (note: Note) => NotesService.validateUpdate(note.id, note);

export const notesAsyncValidate = (values, dispatch, props, blurredField) => fieldUpdateHandler(
  values,
  dispatch,
  props,
  blurredField,
  "Note",
  "notes",
  validateNoteCreate,
  validateNoteUpdate,
  postNoteItem,
  putNoteItem
);

export const processNotesAsyncQueue = async (actions: QueuedAction[]) => {
  const noteActions = actions.filter(a => a.entity === "Note");

  await noteActions.map(a => {
    const {payload} = a.actionBody;

    switch (a.method) {
      case "POST": {
        return () => NotesService.create(payload.note.entityName, payload.note.entityId, payload.note);
      }
      case "PUT": {
        return () => NotesService.update(payload.note.id, payload.note);
      }
      case "DELETE": {
        return () => NotesService.remove(payload);
      }
    }
    return () => Promise.resolve();
  })
    .reduce(async (a, b) => {
      await a;
      await b();
    }, Promise.resolve());
};
