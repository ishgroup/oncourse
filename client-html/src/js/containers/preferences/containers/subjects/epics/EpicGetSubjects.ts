/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

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
import { Create, Request } from "../../../../../common/epics/EpicUtils";
import EntityService from "../../../../../common/services/EntityService";
import { SUBJECTS_ENTITY_FORM_NAME } from "../../../../../constants/Forms";
import TagsService from "../../../../tags/services/TagsService";
import { GET_SUBJECTS } from "../actions";

const request: Request = {
  type: GET_SUBJECTS,
  getData: async () => {
    const id = await EntityService.getPlainRecords(
      "Tag",
      "id",
      `nodeType = TAG and parentTag = null and specialType = SUBJECTS`,
      null,
      null,
      "name",
      true
    ).then(r => parseInt(r.rows[0]?.id));

    return TagsService.getTag(id);
  },
  processData: (tag: Tag) => [initialize(SUBJECTS_ENTITY_FORM_NAME, tag)]
};

export const EpicGetSubjects: Epic<any, any> = Create(request);