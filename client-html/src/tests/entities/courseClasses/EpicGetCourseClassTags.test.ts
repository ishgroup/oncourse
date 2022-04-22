import { DefaultEpic } from "../../common/Default.Epic";
import { getCourseClassTags } from "../../../js/containers/entities/courseClasses/actions";
import { EpicGetCourseClassTags } from "../../../js/containers/entities/courseClasses/epics/EpicGetCourseClassTags";
import { getMenuTags } from "../../../js/common/components/list-view/utils/listFiltersUtils";
import { GET_LIST_TAGS_FULFILLED, GET_ENTITY_TAGS_REQUEST_FULFILLED } from "../../../js/containers/tags/actions";
import { SET_LIST_MENU_TAGS } from "../../../js/common/components/list-view/actions";

describe("Get course class tags epic tests", () => {
  it("EpicGetCourseClassTags should returns correct values", () => DefaultEpic({
    action: () => getCourseClassTags(),
    epic: EpicGetCourseClassTags,
    processData: mockedApi => {
      const courseClassTags = mockedApi.db.getTags();
      const courseTags = mockedApi.db.getTags();

      const menuTags = getMenuTags(courseClassTags, [], null, null, "CourseClass")
        .concat(getMenuTags(courseTags, [], "Courses", null, "Course", "course"));

      return [
        {
          type: GET_LIST_TAGS_FULFILLED
        },
        {
          type: SET_LIST_MENU_TAGS,
          payload: { menuTags }
        },
        {
          type: GET_ENTITY_TAGS_REQUEST_FULFILLED,
          payload: { tags: courseClassTags, entityName: "CourseClass" }
        }
      ];
    }
  }));
});
