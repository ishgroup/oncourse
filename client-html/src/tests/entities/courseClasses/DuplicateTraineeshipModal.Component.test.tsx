/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { format } from "date-fns";
import DuplicateTraineeshipModal, { DUPLICATE_TRAINEESHIP_FORM } from
    "../../../js/containers/entities/courseClasses/components/duplicate-courseClass/DuplicateTraineeshipModal";
import { stubFunction } from "../../../js/common/utils/common";
import { defaultComponents } from "../../common/Default.Components";
import { III_DD_MMM_YYYY } from "../../../js/common/utils/dates/format";

describe("Virtual rendered DuplicateTraineeshipModal of Class list view", () => {
  defaultComponents({
    entity: "DuplicateTraineeshipModal",
    View: props => <div><DuplicateTraineeshipModal {...props} /></div>,
    record: mockedApi => {
      const sessions = mockedApi.db.getCourseClassTimetable();
      const earliestDate = new Date(sessions[0].start);

      return {
        daysTo: 0,
        toDate: earliestDate,
        applyDiscounts: true,
        copyAssessments: true,
        copyCosts: true,
        copyNotes: true,
        copyOnlyMandatoryTags: true,
        tutorRosterOverrides: true,
        copySitesAndRooms: true,
        copyTrainingPlans: true,
        copyTutors: true,
        copyVetData: true
      };
    },
    defaultProps: ({ mockedApi }) => ({
      opened: true,
      sessions: mockedApi.db.getCourseClassTimetable(),
      selection: mockedApi.db.getCourseClassSelectedSessions(),
      setDialogOpened: stubFunction,
      closeMenu: stubFunction
    }),
    state: ({ viewProps }) => ({
      form: {
        [DUPLICATE_TRAINEESHIP_FORM]: {
          values: viewProps.values
        }
      }
    }),
    render: ({ screen, fireEvent, initialValues }) => {
      expect(screen.getByText("Duplicate traineeship class")).toBeTruthy();

      fireEvent.click(screen.getByLabelText("Tutors for each session"));
      fireEvent.click(screen.getByLabelText("Site and room for each session"));
      fireEvent.click(screen.getByLabelText("Budget"));
      fireEvent.click(screen.getByLabelText("Training plan"));
      fireEvent.click(screen.getByLabelText("Discounts"));
      fireEvent.click(screen.getByLabelText("Payable time"));
      fireEvent.click(screen.getByLabelText("VET fields"));
      fireEvent.click(screen.getByLabelText("Assessment task"));
      fireEvent.click(screen.getByLabelText("Tags"));
      fireEvent.click(screen.getByLabelText("Class notes"));

      fireEvent.click(screen.getByText("Duplicate and enrol"));

      setTimeout(() => {
        expect(screen.getByRole(DUPLICATE_TRAINEESHIP_FORM)).toHaveFormValues({
          daysTo: initialValues.daysTo,
          toDate: format(initialValues.toDate, III_DD_MMM_YYYY),
          copyTutors: initialValues.copyTutors,
          copySitesAndRooms: initialValues.copySitesAndRooms,
          copyCosts: initialValues.copyCosts,
          copyTrainingPlans: initialValues.copyTrainingPlans,
          applyDiscounts: initialValues.applyDiscounts,
          tutorRosterOverrides: initialValues.tutorRosterOverrides,
          copyVetData: initialValues.copyVetData,
          copyAssessments: initialValues.copyAssessments,
          copyOnlyMandatoryTags: initialValues.copyOnlyMandatoryTags,
          copyNotes: initialValues.copyNotes,
        });
      }, 500);
    }
  });
});
