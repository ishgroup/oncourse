/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType } from "../../../../actions/ActionUtils";
import { IAction } from "../../../../actions/IshAction";
import { Note } from "@api/model";
import { EntityName } from "../../../../../model/entities/common";

export const GET_NOTE_ITEMS = _toRequestType("get/list/entity/note");

export const PUT_NOTE_ITEM = _toRequestType("put/list/entity/note");

export const POST_NOTE_ITEM = _toRequestType("post/list/entity/note");

export const DELETE_NOTE_ITEM = _toRequestType("delete/list/entity/note");

export const getNoteItems = (entityName: EntityName, entityId: number, form: string): IAction<any> => ({
  type: GET_NOTE_ITEMS,
  payload: { entityName, entityId, form }
});

export const putNoteItem = (note: Note) => ({
  type: PUT_NOTE_ITEM,
  payload: { note }
});

export const postNoteItem = (note: Note) => ({
  type: POST_NOTE_ITEM,
  payload: { note }
});

export const deleteNoteItem = (noteId: number) => ({
  type: DELETE_NOTE_ITEM,
  payload: noteId
});
