import { DefaultEpic } from "../../common/Default.Epic";
import { getContactTags } from "../../../js/containers/entities/contacts/actions";
import { EpicGetContactTags } from "../../../js/containers/entities/contacts/epics/EpicGetContactTags";
import { getMenuTags } from "../../../js/common/components/list-view/utils/listFiltersUtils";
import { GET_ENTITY_TAGS_REQUEST_FULFILLED, GET_LIST_TAGS_FULFILLED } from "../../../js/containers/tags/actions";
import { SET_LIST_MENU_TAGS } from "../../../js/common/components/list-view/actions";

describe("Get contact tags epic tests", () => {
  it("EpicGetContactTags should returns correct values", () => DefaultEpic({
    action: getContactTags(),
    epic: EpicGetContactTags,
    processData: mockedApi => {
      const contactTags = mockedApi.db.getTags();
      const courseTags = mockedApi.db.getTags();

      const contactMenuTags = getMenuTags(contactTags, []);

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
        {
          type: GET_LIST_TAGS_FULFILLED
        },
        {
          type: SET_LIST_MENU_TAGS,
          payload: { menuTags }
        },
        {
          type: GET_ENTITY_TAGS_REQUEST_FULFILLED,
          payload: { tags: contactTags, entityName: "Contact" }
        }
      ];
    }
  }));
});
