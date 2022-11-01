import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { getCourseClass } from "../../../js/containers/entities/courseClasses/actions";
import { EpicGetCourseClass } from "../../../js/containers/entities/courseClasses/epics/EpicGetCourseClass";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { GET_COURSE_CLASS_TUTORS } from "../../../js/containers/entities/courseClasses/components/tutors/actions";
import { GET_COURSE_CLASS_ASSESSMENTS } from "../../../js/containers/entities/courseClasses/components/assessments/actions";
import { GET_COURSE_CLASS_SESSIONS } from "../../../js/containers/entities/courseClasses/components/timetable/actions";
import { GET_COURSE_CLASS_ATTENDANCE } from "../../../js/containers/entities/courseClasses/components/attendance/actions";
import { getNoteItems } from "../../../js/common/components/form/notes/actions";

const id: string = "1";

describe("Get course class epic tests", () => {
  it("EpicGetCourseClass should returns correct values", () => DefaultEpic({
    action: () => getCourseClass(id),
    epic: EpicGetCourseClass,
    processData: mockedApi => {
      const courseClass = mockedApi.db.getCourseClass(id);

      courseClass.tutors = [];
      courseClass.sessions = [];
      courseClass.assessments = [];
      courseClass.studentAttendance = [];
      courseClass.trainingPlan = [];
      courseClass.budget = [];
      courseClass.notes = [];

      const relatedActions = [
        {
          type: GET_COURSE_CLASS_TUTORS,
          payload: id
        },
        {
          type: GET_COURSE_CLASS_ASSESSMENTS,
          payload: id
        },
        {
          type: GET_COURSE_CLASS_SESSIONS,
          payload: id
        },
        {
          type: GET_COURSE_CLASS_ATTENDANCE,
          payload: id
        },
      ];

      return [
        {
          type: SET_LIST_EDIT_RECORD,
          payload: {
            editRecord: courseClass,
            name: `${courseClass.courseName} ${courseClass.courseCode}-${courseClass.code}`
          }
        },
        ...relatedActions,
        initialize(LIST_EDIT_VIEW_FORM_NAME, courseClass),
        getNoteItems("CourseClass", id as any, LIST_EDIT_VIEW_FORM_NAME),
      ];
    }
  }));
});
