import { DefaultEpic } from "../../../common/Default.Epic";
import { EpicGetCourseClassAttendance } from "../../../../js/containers/entities/courseClasses/components/attendance/epics/EpicGetCourseClassAttendance";
import { GET_COURSE_CLASS_ATTENDANCE } from "../../../../js/containers/entities/courseClasses/components/attendance/actions";

const id = 1;

describe("Get course class attendance epic tests", () => {
  it("EpicGetCourseClassAttendance should returns correct values", () => DefaultEpic({
    action: {
      typpe: GET_COURSE_CLASS_ATTENDANCE,
      payload: id,
    },
    epic: EpicGetCourseClassAttendance,
    processData: () => []
  }));
});

