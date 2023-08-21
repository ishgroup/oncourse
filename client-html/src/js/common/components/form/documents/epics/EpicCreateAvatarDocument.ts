/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Document } from "@api/model";
import { change } from "redux-form";
import { Epic } from "redux-observable";
import { DocumentExtended } from "../../../../../model/common/Documents";
import FetchErrorHandler from "../../../../api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../epics/EpicUtils";
import { CREATE_AVATAR_DOCUMENT } from "../actions";
import DocumentsService from "../services/DocumentsService";

const request: EpicUtils.Request<any, { document: DocumentExtended; form: string; documentPath: string }> = {
  type: CREATE_AVATAR_DOCUMENT,
  getData: ({document}) =>
    DocumentsService.createDocument(
      document.content.name,
      document.description,
      document.shared,
      document.access,
      document.content,
      "",
      document.content.name
    ),
  processData: (newDocument: Document, state: any, {form, documentPath}) => [change(form, documentPath, newDocument)],
  processError: (error, {form, documentPath}) => [change(form, documentPath, null), ...FetchErrorHandler(error)]
};

export const EpicCreateAvatarDocument: Epic<any, any> = EpicUtils.Create(request);
