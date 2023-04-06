import * as React from "react";
import { format } from "date-fns";
import { defaultComponents } from "../../../common/Default.Components";
import { stubFunction } from "../../../../js/common/utils/common";
import DuplicateCourseClassModal, { DUPLICATE_COURSE_CLASS_FORM } from "../../../../js/containers/entities/courseClasses/components/duplicate-courseClass/DuplicateCourseClassModal";
import { III_DD_MMM_YYYY } from "../../../../js/common/utils/dates/format";

describe("Virtual rendered DuplicateCourseClassModal of Class list view", () => {
  defaultComponents({
    entity: "DuplicateCourseClassModal",
    View: props => <DuplicateCourseClassModal {...props} />,
    record: mockedApi => {
      const sessions = mockedApi.db.getCourseClassTimetable();
      const earliestDate = new Date(sessions[0].start);

      return {
        classIds: [],
        daysTo: 0,
        toDate: earliestDate,
        copyTutors: true,
        copyTrainingPlans: true,
        applyDiscounts: true,
        copyCosts: true,
        copySitesAndRooms: true,
        tutorRosterOverrides: true,
        copyVetData: true,
        copyNotes: true,
        copyAssessments: true,
        copyOnlyMandatoryTags: true
      };
    },
    defaultProps: ({ mockedApi }) => ({
      opened: true,
      sessions: mockedApi.db.getCourseClassTimetable(),
      selection: mockedApi.db.getCourseClassSelectedSessions(),
      setDialogOpened: stubFunction,
      earliest: new Date().toISOString(),
      hasZeroWages: false,
      closeMenu: jest.fn(),
    }),
    render: ({
      screen, fireEvent, viewProps, initialValues
    }) => {
      const selection = viewProps.selection;
      expect(screen.getByText(`Duplicate ${selection.length} class${selection.length === 1 ? "" : "es"}`)).toBeTruthy();

      const earliestDate = new Date(viewProps.sessions[0].start);

      fireEvent.click(screen.getByText("Duplicate"));

      setTimeout(() => {
        expect(screen.getByRole(DUPLICATE_COURSE_CLASS_FORM)).toHaveFormValues({
          daysTo: initialValues.daysTo,
          toDate: format(earliestDate, III_DD_MMM_YYYY),
          copyTutors: initialValues.copyTutors,
          copyTrainingPlans: initialValues.copyTrainingPlans,
          applyDiscounts: initialValues.applyDiscounts,
          copyCosts: initialValues.copyCosts,
          copySitesAndRooms: initialValues.copySitesAndRooms,
          tutorRosterOverrides: initialValues.tutorRosterOverrides,
          copyVetData: initialValues.copyVetData,
          copyNotes: initialValues.copyNotes,
          copyAssessments: initialValues.copyAssessments,
          copyOnlyMandatoryTags: initialValues.copyOnlyMandatoryTags
        });
      }, 500);
    }
  });
});
