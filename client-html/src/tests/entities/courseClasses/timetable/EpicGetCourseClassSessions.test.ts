import { initialize } from "redux-form";
import { DefaultEpic } from "../../../common/Default.Epic";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../js/common/components/list-view/constants";
import { courseClassTimetablePath } from "../../../../js/constants/Api";
import { getCourseClassSessionsWarnings } from "../../../../js/containers/entities/courseClasses/actions";
import { GET_COURSE_CLASS_SESSIONS } from "../../../../js/containers/entities/courseClasses/components/timetable/actions";
import { EpicGetCourseClassSessions } from "../../../../js/containers/entities/courseClasses/components/timetable/epics/EpicGetCourseClassSessions";

const id = "1";

describe("Get course class sessions epic tests", () => {
  it("EpicGetCourseClassSessions should returns correct values", () => DefaultEpic({
    action: {
      type: GET_COURSE_CLASS_SESSIONS,
      payload: id,
    },
    epic: EpicGetCourseClassSessions,
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
      const sessions = mockedApi.db.getCourseClassTimetable();

      if (!s.form[LIST_EDIT_VIEW_FORM_NAME] || !s.form[LIST_EDIT_VIEW_FORM_NAME].initial || s.form[LIST_EDIT_VIEW_FORM_NAME].initial.id !== Number(id)) {
        return [];
      }
      const sessionsMap = sessions.map(session => ({ ...session }));
      sessions.sort((a, b) => (new Date(a.start) > new Date(b.start) ? 1 : -1));

      const warningsAccess = s.access[courseClassTimetablePath] && s.access[courseClassTimetablePath]["POST"];

      return [
        initialize(LIST_EDIT_VIEW_FORM_NAME, { ...s.form[LIST_EDIT_VIEW_FORM_NAME].values, sessions, openedSession: {} }),
        ...warningsAccess ? [getCourseClassSessionsWarnings(Number(id), sessionsMap)] : []
      ];
    }
  }));
});
