import { mockedEditView } from "../../common/MockedEditView.Components";
import CourseClassEditView from "../../../js/containers/entities/courseClasses/components/CourseClassEditView";

describe("Virtual rendered CourseClassEditView", () => {
  mockedEditView({
    entity: "CourseClass",
    EditView: CourseClassEditView,
    record: mockedApi => ({
      ...mockedApi.db.getCourseClass(1),
      trainingPlan: mockedApi.db.getCourseClassTrainingPlan(),
      sessions: mockedApi.db.getCourseClassTimetable(),
      tutors: mockedApi.db.getCourseClassTutors(1),
      budget: mockedApi.db.getCourseClassBudget("1"),
      studentAttendance: mockedApi.db.getCourseClassAttendanceStudents(),
      notes: [],
      assessments: mockedApi.db.getCourseClassAssessment(1),
    }),
    state: ({ mockedApi }) => ({
      taxes: { items: mockedApi.db.getPlainTaxesFormatted() },
      export: { contracts: mockedApi.db.getAvetmissExportPlainListFormatted() },
    }),
    render: ({
      screen, initialValues, formRoleName, fireEvent
    }) => {
      fireEvent.click(screen.getByTestId("expand-button-2"));

      const tutors = {};

      initialValues.tutors.forEach((tutor, index) => {
        fireEvent.click(screen.getByTestId(`expand-button-course-class-tutor-${index}`));

        tutors[`tutors[${index}].contactId`] = tutor.tutorName;
        tutors[`tutors[${index}].roleId`] = tutor.roleName;
        tutors[`tutors[${index}].confirmedOn`] = tutor.confirmedOn;
        tutors[`tutors[${index}].isInPublicity`] = tutor.isInPublicity;
      });

      expect(screen.getByRole(formRoleName)).toHaveFormValues({
        courseId: initialValues.courseName,
        minStudentAge: initialValues.minStudentAge,
        maxStudentAge: initialValues.maxStudentAge,
        isActive: initialValues.isActive,
        isShownOnWeb: initialValues.isShownOnWeb,
        message: initialValues.message,
        webDescription: initialValues.webDescription,

        suppressAvetmissExport: initialValues.suppressAvetmissExport,
        deliveryMode: initialValues.deliveryMode,
        fundingSource: initialValues.fundingSource,
        vetFundingSourceStateID: initialValues.vetFundingSourceStateID,
        detBookingId: initialValues.detBookingId,
        vetCourseSiteID: initialValues.vetCourseSiteID.toString(),
        vetPurchasingContractID: initialValues.vetPurchasingContractID,
        vetPurchasingContractScheduleID: initialValues.vetPurchasingContractScheduleID,
        qualificationHours: initialValues.qualificationHours,
        nominalHours: initialValues.nominalHours,
        classroomHours: initialValues.classroomHours,
        studentContactHours: initialValues.studentContactHours,
        reportableHours: initialValues.reportableHours,

        // ...tutors,
      });
    }
  });
});
