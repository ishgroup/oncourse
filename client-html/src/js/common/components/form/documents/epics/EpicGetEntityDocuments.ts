/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { Note } from "@api/model";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../epics/EpicUtils";
import FetchErrorHandler from "../../../../api/fetch-errors-handlers/FetchErrorHandler";
import { EntityName } from "../../../../../model/entities/common";
import { GET_ENTITY_DOCUMENTS } from "../actions";
import DocumentsService from "../services/DocumentsService";

const request: EpicUtils.Request<Note[], { entityName: EntityName; entityId: number; form: string }> = {
  type: GET_ENTITY_DOCUMENTS,
  getData: ({ entityName, entityId }) => DocumentsService.getDocumentsAttachedTo(entityName, entityId),
  processData: (documents, s, { form }) => [initialize(form, { ...s.form[form].initial, documents })],
  processError: response => FetchErrorHandler(response, "Failed to get documents")
};

export const EpicGetEntityDocuments: Epic<any, any> = EpicUtils.Create(request);