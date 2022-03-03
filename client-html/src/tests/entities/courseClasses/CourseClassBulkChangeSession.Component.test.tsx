/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import CourseClassBulkChangeSession, {
  initialValues, COURSE_CLASS_BULK_UPDATE_FORM
} from "../../../js/containers/entities/courseClasses/components/timetable/CourseClassBulkChangeSession";
import { defaultComponents } from "../../common/Default.Components";

describe("Virtual rendered CourseClassBulkChangeSession of Class edit view", () => {
  defaultComponents({
    entity: "CourseClassBulkChangeSession",
    View: props => <div><CourseClassBulkChangeSession {...props} /></div>,
    record: mockedApi => ({ sessions: mockedApi.db.getCourseClassTimetable() }),
    defaultProps: ({ mockedApi }) => ({
      opened: true,
      onClose: jest.fn(),
      handleSubmit: jest.fn(),
      tutors: [],
      sessions: mockedApi.db.getCourseClassTimetable(),
      selection: mockedApi.db.getCourseClassSelectedSessions(),
      initialValues: {
        ...initialValues,
        payableDuration: 2,
        duration: 3,
        siteTimezone: "Australia/West",
        start: "2021-11-25T17:05:00.000Z",
        moveForward: "2",
      },
      rooms: [],
    }),
    render: ({ screen, fireEvent }) => {
      expect(screen.getByText("Bulk change")).toBeTruthy();
      expect(screen.getByText("Update 0 timetable event")).toBeTruthy();

      fireEvent.click(screen.getByLabelText("Location"));
      fireEvent.click(screen.getByTestId("input-payableDurationChecked", { selector: 'input' }));
      fireEvent.click(screen.getByTestId("input-startChecked", { selector: 'input' }));
      fireEvent.click(screen.getByTestId("input-durationChecked", { selector: 'input' }));
      fireEvent.click(screen.getByTestId("input-moveForwardChecked", { selector: 'input' }));

      expect(screen.getByRole(COURSE_CLASS_BULK_UPDATE_FORM)).toHaveFormValues({
        locationChecked: true,
        roomId: "",
        payableDurationChecked: true,
        actualPayableDuration: "2min",
        startChecked: true,
        start: "01:05",
        durationChecked: true,
        duration: "3min",
        moveForwardChecked: true,
        moveForward: 2,
        moveBackwardChecked: false,
        moveBackward: null,
      });
    }
  });
});
