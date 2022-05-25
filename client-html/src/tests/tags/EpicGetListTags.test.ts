/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DefaultEpic } from "../common/Default.Epic";
import {
  GET_ENTITY_TAGS_REQUEST_FULFILLED,
  getListTags
} from "../../js/containers/tags/actions";
import { EpicGetListTags } from "../../js/containers/tags/epics/EpicGetListTags";
import { setListMenuTags } from "../../js/common/components/list-view/actions";
import { getMenuTags } from "../../js/common/components/list-view/utils/listFiltersUtils";

const entityName: string = "Course";

describe("Get list tags epic tests", () => {
  it("EpicGetListTags should returns correct values", () => DefaultEpic({
    action: getListTags(entityName),
    epic: EpicGetListTags,
    processData: mockedApi => {
      const tags = mockedApi.db.getTags();
      const checklists = mockedApi.db.getTags();
      const menuTags = getMenuTags(tags, []);
      const checkedChecklists = getMenuTags(checklists, []);
      const uncheckedChecklists = [...checkedChecklists];

      return [
        setListMenuTags(menuTags, checkedChecklists, uncheckedChecklists),
        {
          type: GET_ENTITY_TAGS_REQUEST_FULFILLED,
          payload: { tags, entityName }
        }
      ];
    }
  }));
});
