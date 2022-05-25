/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { initialize } from "redux-form";
import { DefaultEpic } from "../common/Default.Epic";
import {
  getAllTags,
  updateTag
} from "../../js/containers/tags/actions";
import {
  TAGS_FORM_NAME
} from "../../js/containers/tags/constants";
import { FETCH_SUCCESS } from "../../js/common/actions";
import { EpicUpdateTag } from "../../js/containers/tags/epics/EpicUpdateTag";

describe("Update tag epic tests", () => {
  it("EpicUpdateTag should returns correct values", () => DefaultEpic({
    action: mockedApi => updateTag(1, mockedApi.db.getTag(1)),
    epic: EpicUpdateTag,
    processData: mockedApi => {
      const tag = mockedApi.db.getTag(1);
      
      return [
        {
          type: FETCH_SUCCESS,
          payload: { message: `${tag.type} was successfully updated` }
        },
        initialize(TAGS_FORM_NAME, tag),
        getAllTags()
      ];
    }
  }));
});
