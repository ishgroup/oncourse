/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DefaultEpic } from "../../common/Default.Epic";
import { getCourseClassTags } from "../../../js/containers/entities/courseClasses/actions";
import { EpicGetCourseClassTags } from "../../../js/containers/entities/courseClasses/epics/EpicGetCourseClassTags";
import { getMenuTags } from "ish-ui";
import { GET_ENTITY_TAGS_REQUEST_FULFILLED } from "../../../js/containers/tags/actions";
import { setListMenuTags } from "../../../js/common/components/list-view/actions";

describe("Get course class tags epic tests", () => {
  it("EpicGetCourseClassTags should returns correct values", () => DefaultEpic({
    action: () => getCourseClassTags(),
    epic: EpicGetCourseClassTags,
    processData: mockedApi => {
      const courseClassTags = mockedApi.db.getTags();
      const courseTags = mockedApi.db.getTags();
      const checklists = mockedApi.db.getTags();

      const menuTags = getMenuTags(courseClassTags, [], null, null, "CourseClass")
        .concat(getMenuTags(courseTags, [], "Courses", null, "Course", "course"));

      const checkedChecklists = getMenuTags(checklists, []);
      const uncheckedChecklists = [...checkedChecklists];

      return [
        setListMenuTags(
          menuTags,
          checkedChecklists,
          uncheckedChecklists
        ),
        {
          type: GET_ENTITY_TAGS_REQUEST_FULFILLED,
          payload: { tags: courseClassTags, entityName: "CourseClass" }
        }
      ];
    }
  }));
});
