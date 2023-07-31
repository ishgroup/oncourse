/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { getMenuTags } from "ish-ui";
import TagsService from "../../../tags/services/TagsService";
import { GET_ENTITY_TAGS_REQUEST_FULFILLED } from "../../../tags/actions";
import { setListMenuTags } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_COURSE_CLASS_TAGS } from "../actions";

const request: EpicUtils.Request<any, never> = {
  type: GET_COURSE_CLASS_TAGS,
  getData: async () => {
    const courseClassTags = await TagsService.getTags("CourseClass");
    const courseTags = await TagsService.getTags("Course");
    const checklists = await TagsService.getChecklists("CourseClass");

    return { courseClassTags, courseTags, checklists };
  },
  processData: ({ courseClassTags, courseTags, checklists }) => {
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
  },
  processError: response => FetchErrorHandler(response, "Failed to get contact tags")
};

export const EpicGetCourseClassTags: Epic<any, any> = EpicUtils.Create(request);
