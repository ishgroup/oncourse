import { DefaultEpic } from "../../../common/Default.Epic";
import { EpicUpdateCourseClassStudentAttendance } from "../../../../js/containers/entities/courseClasses/components/attendance/epics/EpicUpdateCourseClassStudentAttendance";
import { updateCourseClassStudentAttendance } from "../../../../js/containers/entities/courseClasses/components/attendance/actions";

describe("Update course class student attendance epic tests", () => {
  it("EpicUpdateCourseClassStudentAttendance should returns correct values", () => DefaultEpic({
    action: mockedApi => updateCourseClassStudentAttendance(1, mockedApi.db.getCourseClassAttendanceStudents()),
    epic: EpicUpdateCourseClassStudentAttendance,
    processData: () => []
  }));
});
