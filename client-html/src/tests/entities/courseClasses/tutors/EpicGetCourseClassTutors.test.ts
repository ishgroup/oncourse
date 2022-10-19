import { initialize } from "redux-form";
import { DefaultEpic } from "../../../common/Default.Epic";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../js/common/components/list-view/constants";
import { GET_COURSE_CLASS_TUTORS, setCourseClassTutorNamesWarnings } from "../../../../js/containers/entities/courseClasses/components/tutors/actions";
import { EpicGetCourseClassTutors } from "../../../../js/containers/entities/courseClasses/components/tutors/epics/EpicGetCourseClassTutors";

const id = "1";

describe("Get course class tutors epic tests", () => {
  it("EpicGetCourseClassTutors should returns correct values", () => DefaultEpic({
    action: {
      type: GET_COURSE_CLASS_TUTORS,
      payload: id,
    },
    epic: EpicGetCourseClassTutors,
    initialValues: ({ mockedApi }) => ({
      ...mockedApi.db.getCourseClass(id),
      trainingPlan: mockedApi.db.getCourseClassTrainingPlan(),
      sessions: mockedApi.db.getCourseClassTimetable(),
      tutors: mockedApi.db.getCourseClassTutors(id),
      budget: mockedApi.db.getCourseClassBudget(id),
      studentAttendance: mockedApi.db.getCourseClassAttendanceStudents(),
      notes: [],
      assessments: mockedApi.db.getCourseClassAssessment(id),
    }),
    store: ({ values }) => ({
      form: {
        [LIST_EDIT_VIEW_FORM_NAME]: {
          initial: values,
          values
        }
      }
    }),
    processData: (mockedApi, s) => {
      const tutors = mockedApi.db.getCourseClassTutors(id);
      const warnings = {};

      if (!s.form[LIST_EDIT_VIEW_FORM_NAME] || !s.form[LIST_EDIT_VIEW_FORM_NAME].initial || s.form[LIST_EDIT_VIEW_FORM_NAME].initial.id !== Number(id)) {
        return [];
      }
      return [
        setCourseClassTutorNamesWarnings(warnings),
        initialize(LIST_EDIT_VIEW_FORM_NAME, { ...s.form[LIST_EDIT_VIEW_FORM_NAME].values, tutors })
      ];
    }
  }));
});
