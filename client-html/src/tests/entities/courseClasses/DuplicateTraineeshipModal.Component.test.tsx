/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import DuplicateTraineeshipModal, { DUPLICATE_TRAINEESHIP_FORM } from
    "../../../js/containers/entities/courseClasses/components/duplicate-courseClass/DuplicateTraineeshipModal";
import { stubFunction } from "../../../js/common/utils/common";
import { defaultComponents } from "../../common/Default.Components";

describe("Virtual rendered DuplicateTraineeshipModal of Class list view", () => {
  defaultComponents({
    entity: "DuplicateTraineeshipModal",
    View: props => <div><DuplicateTraineeshipModal {...props} /></div>,
    record: () => ({}),
    defaultProps: ({ mockedApi }) => ({
      opened: true,
      sessions: mockedApi.db.getCourseClassTimetable(),
      selection: mockedApi.db.getCourseClassSelectedSessions(),
      setDialogOpened: stubFunction,
      closeMenu: stubFunction
    }),
    state: () => ({
      form: {
        [DUPLICATE_TRAINEESHIP_FORM]: {
          values: {
            daysTo: 0,
            toDate: new Date().toISOString(),
            applyDiscounts: true,
            copyAssessments: true,
            copyCosts: true,
            copyNotes: true,
            copyOnlyMandatoryTags: true,
            copyPayableTimeForSessions: true,
            copySitesAndRooms: true,
            copyTrainingPlans: true,
            copyTutors: true,
            copyVetData: true,
          }
        }
      }
    }),
    render: ({ screen, fireEvent }) => {
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
          daysTo: 0,
          copyTutors: true,
          copySitesAndRooms: true,
          copyCosts: true,
          copyTrainingPlans: true,
          applyDiscounts: true,
          copyPayableTimeForSessions: true,
          copyVetData: true,
          copyAssessments: true,
          copyOnlyMandatoryTags: true,
          copyNotes: true,
        });
      }, 500);
    }
  });
});
