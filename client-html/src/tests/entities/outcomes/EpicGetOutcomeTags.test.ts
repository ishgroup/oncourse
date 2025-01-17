/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DefaultEpic } from "../../common/Default.Epic";
import { setListMenuTags } from "../../../js/common/components/list-view/actions";
import { getOutcomeTags } from "../../../js/containers/entities/outcomes/actions";
import { EpicGetOutcomeTags } from "../../../js/containers/entities/outcomes/epics/EpicGetOutcomeTags";
import { getMenuTags } from "ish-ui";
import { GET_ENTITY_TAGS_REQUEST_FULFILLED } from "../../../js/containers/tags/actions";

describe("Get outcome tags epic tests", () => {
  it("EpicGetOutcomeTags should returns correct values", () => DefaultEpic({
    action: getOutcomeTags(),
    epic: EpicGetOutcomeTags,
    processData: mockedApi => {
      const tags = mockedApi.db.getTags();
      const menuTags = getMenuTags(tags, [], null, null, "Enrolment", "enrolment");

      return [
        setListMenuTags(
          menuTags,
          [],
          []
        ),
        {
          type: GET_ENTITY_TAGS_REQUEST_FULFILLED,
          payload: { tags, entityName: "Enrolment" }
        }
      ];
    }
  }));
});
