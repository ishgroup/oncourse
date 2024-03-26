/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Tag } from "@api/model";
import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import { Create, Request } from "../../../common/epics/EpicUtils";
import { GET_TAG_REQUEST } from "../actions";
import TagsService from "../services/TagsService";

const request: Request<any, { form: string, id: number }> = {
  type: GET_TAG_REQUEST,
  getData: ({ id }) => TagsService.getTag(id),
  processData: (tag: Tag, s, { form }) => [initialize(form, tag)]
};

export const EpicGetTag: Epic<any, any> = Create(request);
