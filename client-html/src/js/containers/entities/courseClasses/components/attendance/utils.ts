/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AttendanceType, TutorAttendanceType } from "@api/model";

export const getStudentAttendanceLabel = (type: AttendanceType | TutorAttendanceType): string => {
  switch (type) {
    default:
      return type;
    case "Partial":
      return "Partial attendance";
    case "Unmarked":
      return "Not marked";
  }
};

export const getTrainingPlanAttendanceLabel = (type: AttendanceType): string => {
  switch (type) {
    default:
      return type;
    case "Attended":
      return "Assigned";
    case "Unmarked":
      return "Not assigned";
  }
};
