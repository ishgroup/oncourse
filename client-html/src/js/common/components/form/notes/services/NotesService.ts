/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Note, NoteApi } from "@api/model";
import { DefaultHttpService } from "../../../../services/HttpService";

class NotesService {
  readonly service = new DefaultHttpService();

  readonly notesApi = new NoteApi(this.service);

  public get(entityName: string, entityId: number): Promise<Note[]> {
    return this.notesApi.get(entityName, entityId);
  }

  public create(entityName: string, entityId: number, note: Note): Promise<any> {
    return this.notesApi.create(entityName, entityId, note);
  }

  public remove(noteId: number): Promise<any> {
    return this.notesApi.remove(noteId);
  }

  public update(noteId: number, note: Note): Promise<any> {
    return this.notesApi.update(noteId, note);
  }

  public validateUpdate(noteId: number, note: Note): Promise<any> {
    return this.service.PUT(`/v1/list/entity/note/${noteId}`, note, {
      headers: {"X-validate-only": "true"}
    });
  }

  public validateRemove(noteId: number): Promise<any> {
    return this.service.DELETE(`/v1/list/entity/note/${noteId}`, {
      headers: {"X-validate-only": "true"}
    });
  }

  public validateCreate(entityName: string, entityId: number, note: Note): Promise<any> {
    return this.service.POST("/v1/list/entity/note", note, {
      params: {entityName, entityId},
      headers: {"X-validate-only": "true"}
    });
  }
}

export default new NotesService();
