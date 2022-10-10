import { initialize } from "redux-form";
import { DefaultEpic } from "../../../common/Default.Epic";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../js/common/components/list-view/constants";
import { EpicGetCourseClassCosts } from "../../../../js/containers/entities/courseClasses/components/budget/epics/EpicGetCourseClassCosts";
import { GET_COURSE_CLASS_COSTS } from "../../../../js/containers/entities/courseClasses/components/budget/actions";

const id = "1";

describe("Get course class cost epic tests", () => {
  it("EpicGetCourseClassCosts should returns correct values", () => DefaultEpic({
    action: {
      type: GET_COURSE_CLASS_COSTS,
      payload: id,
    },
    epic: EpicGetCourseClassCosts,
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
      const budget = mockedApi.db.getCourseClassBudget(id);

      const studentFee = budget.find(b => b.invoiceToStudent);

      if (studentFee && studentFee.paymentPlan.length) {
        studentFee.paymentPlan.sort((a, b) => (a.dayOffset > b.dayOffset ? 1 : -1));
      }

      if (!s.form[LIST_EDIT_VIEW_FORM_NAME] || !s.form[LIST_EDIT_VIEW_FORM_NAME].initial || s.form[LIST_EDIT_VIEW_FORM_NAME].initial.id !== Number(id)) {
        return [];
      }
      return [initialize(LIST_EDIT_VIEW_FORM_NAME, { ...s.form[LIST_EDIT_VIEW_FORM_NAME].values, budget })];
    }
  }));
});
