/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { getMenuTags } from "ish-ui";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { setListMenuTags } from "../../../../common/components/list-view/actions";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ENTITY_TAGS_REQUEST_FULFILLED } from "../../../tags/actions";
import TagsService from "../../../tags/services/TagsService";
import { GET_CONTACT_TAGS } from "../actions";

const request: EpicUtils.Request<any, never> = {
  type: GET_CONTACT_TAGS,
  getData: async () => {
    const contactTags = await TagsService.getTags("Contact");
    const courseTags = await TagsService.getTags("Course");
    const checklists = await TagsService.getChecklists("Contact");

    return { contactTags, courseTags, checklists };
  },
  processData: ({ contactTags, courseTags, checklists }) => {
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
  },
  processError: response => FetchErrorHandler(response, "Failed to get contact tags")
};

export const EpicGetContactTags: Epic<any, any> = EpicUtils.Create(request);
