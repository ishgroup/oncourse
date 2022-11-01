/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { DefaultEpic } from "../../common/Default.Epic";
import { getContactTags } from "../../../js/containers/entities/contacts/actions";
import { EpicGetContactTags } from "../../../js/containers/entities/contacts/epics/EpicGetContactTags";
import { getMenuTags } from "../../../js/common/components/list-view/utils/listFiltersUtils";
import { GET_ENTITY_TAGS_REQUEST_FULFILLED } from "../../../js/containers/tags/actions";
import { setListMenuTags } from "../../../js/common/components/list-view/actions";

describe("Get contact tags epic tests", () => {
  it("EpicGetContactTags should returns correct values", () => DefaultEpic({
    action: getContactTags(),
    epic: EpicGetContactTags,
    processData: mockedApi => {
      const contactTags = mockedApi.db.getTags();
      const courseTags = mockedApi.db.getTags();
      const checklists = mockedApi.db.getTags();

      const contactMenuTags = getMenuTags(contactTags, []);
      const checkedChecklists = getMenuTags(checklists, []);
      const uncheckedChecklists = [...checkedChecklists];

      const enrolledTags = getMenuTags(courseTags, [], "Enrolled",
        "studentCourseClass.course",
        "Course",
        "student+.enrolments+.courseClass+.course");

      const teachingTags = getMenuTags(courseTags, [],
        "Teaching",
        "tutorCourseClass.course",
        "Course",
        "tutor+.courseClassRoles+.courseClass+.course");

      const menuTags = contactMenuTags.concat(enrolledTags, teachingTags);

      return [
        setListMenuTags(
          menuTags,
          checkedChecklists,
          uncheckedChecklists
        ),
        {
          type: GET_ENTITY_TAGS_REQUEST_FULFILLED,
          payload: { tags: contactTags, entityName: "Contact" }
        }
      ];
    }
  }));
});
