import * as React from "react";
import { format } from "date-fns";
import { defaultComponents } from "../../../common/Default.Components";
import AttendanceActionModal
  from "../../../../js/containers/entities/courseClasses/components/attendance/AttendanceActionModal";
import { appendTimezone, III_DD_MMM_YYYY_HH_MM_SS } from "ish-ui";

describe("Virtual rendered AttendanceActionModalForm of Class edit view", () => {
  defaultComponents({
    entity: "AttendanceActionModalForm",
    View: props => <AttendanceActionModal {...props} />,
    record: mockedApi => ({ sessionId: 4671, sessions: mockedApi.db.getCourseClassTimetable(), }),
    defaultProps: ({ mockedApi }) => ({
      changeType: "Attended",
      reset: jest.fn(),
      setAttendanceChangeType: jest.fn(),
      fetching: false,
      invalid: false,
      sessions: mockedApi.db.getCourseClassTimetable(),
      change: jest.fn(),
      handleSubmit: jest.fn(),
      onSubmit: jest.fn(),
    }),
    render: ({ screen, initialValues, mockedApi }) => {
      const bindedSession = initialValues.sessions.find(s => s.id === initialValues.sessionId);
      const studentAttendance = mockedApi.db.getCourseClassAttendanceStudents().find(a => a.sessionId === initialValues.sessionId);

      const fromLabel = format(
        bindedSession.siteTimezone
          ? appendTimezone(new Date(bindedSession.start), bindedSession.siteTimezone)
          : new Date(bindedSession.start),
        III_DD_MMM_YYYY_HH_MM_SS
      );

      const untilLabel = format(
        bindedSession.siteTimezone
          ? appendTimezone(new Date(bindedSession.end), bindedSession.siteTimezone)
          : new Date(bindedSession.end),
        III_DD_MMM_YYYY_HH_MM_SS
      );

      expect(screen.getByText("Attended")).toBeTruthy();
      expect(screen.getByLabelText("Attended from").value).toBe(fromLabel);
      expect(screen.getByLabelText("Attended until").value).toBe(untilLabel);

      expect(screen.getByLabelText("Note").value).toBe(studentAttendance.note);
    }
  });
});
