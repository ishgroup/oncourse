/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DefaultEpic } from "../common/Default.Epic";
import { EpicGetAllTags } from "../../js/containers/tags/epics/EpicGetAllTags";
import { GET_ALL_TAGS_FULFILLED, getAllTags } from "../../js/containers/tags/actions";
import { plainTagToCatalogItem } from "../../js/containers/tags/utils";

describe("Get all tags epic tests", () => {
  it("EpicGetAllTags should returns correct values", () => DefaultEpic({
    action: getAllTags(),
    epic: EpicGetAllTags,
    processData: mockedApi => {
      const tagsResponse = mockedApi.db.getPlainTags();
      const checklistsResponse = mockedApi.db.getPlainTags();

      const allTags = tagsResponse.rows.map(plainTagToCatalogItem);
      const allChecklists = checklistsResponse.rows.map(plainTagToCatalogItem);

      return [
        {
          type: GET_ALL_TAGS_FULFILLED,
          payload: { allTags, allChecklists }
        }
      ];
    }
  }));
});
