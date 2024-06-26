/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { initialize } from "redux-form";
import { DefaultEpic } from "../common/Default.Epic";
import { EpicCreateTag } from "../../js/containers/tags/epics/EpicCreateTag";
import { getAllTags, createTag } from "../../js/containers/tags/actions";
import { TAGS_FORM_NAME } from "../../js/containers/tags/constants";
import { FETCH_SUCCESS } from "../../js/common/actions";

describe("Create tag epic tests", () => {
  it("EpicCreateTag should returns correct values", () => DefaultEpic({
    action: mockedApi => createTag(TAGS_FORM_NAME,mockedApi.db.getTag(1)),
    epic: EpicCreateTag,
    processData: mockedApi => {
      const tag = mockedApi.db.getTag(1);
      
      return [
        initialize(TAGS_FORM_NAME, tag),
        {
          type: FETCH_SUCCESS,
          payload: { message: `${tag.name} was successfully created` }
        },
        getAllTags(tag.name)
      ];
    }
  }));
});
