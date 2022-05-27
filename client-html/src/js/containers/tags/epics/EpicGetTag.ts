/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { Tag } from "@api/model";
import { initialize } from "redux-form";
import { Create, Request } from "../../../common/epics/EpicUtils";
import TagsService from "../services/TagsService";
import { GET_TAG_REQUEST } from "../actions";
import { TAGS_FORM_NAME } from "../constants";

const request: Request = {
  type: GET_TAG_REQUEST,
  getData: id => TagsService.getTag(id),
  processData: (tag: Tag) => [initialize(TAGS_FORM_NAME, tag)]
};

export const EpicGetTag: Epic<any, any> = Create(request);
