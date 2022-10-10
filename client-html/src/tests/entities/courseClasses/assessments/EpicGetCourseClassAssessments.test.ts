import { initialize } from "redux-form";
import { DefaultEpic } from "../../../common/Default.Epic";
import { GET_COURSE_CLASS_ASSESSMENTS } from "../../../../js/containers/entities/courseClasses/components/assessments/actions";
import { EpicGetCourseClassAssessments } from "../../../../js/containers/entities/courseClasses/components/assessments/epics/EpicGetCourseClassAssessments";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../js/common/components/list-view/constants";

const id = "1";

describe("Get course class assessments epic tests", () => {
  it("EpicGetCourseClassAssessments should returns correct values", () => DefaultEpic({
    action: {
      type: GET_COURSE_CLASS_ASSESSMENTS,
      payload: id,
    },
    epic: EpicGetCourseClassAssessments,
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
      const assessments = mockedApi.db.getCourseClassAssessment(id);

      if (!s.form[LIST_EDIT_VIEW_FORM_NAME] || !s.form[LIST_EDIT_VIEW_FORM_NAME].initial || s.form[LIST_EDIT_VIEW_FORM_NAME].initial.id !== Number(id)) {
        return [];
      }

      assessments.forEach((a, index) => {
        a.index = index;
      });

      return [initialize(LIST_EDIT_VIEW_FORM_NAME, { ...s.form[LIST_EDIT_VIEW_FORM_NAME].values, assessments })];
    }
  }));
});
