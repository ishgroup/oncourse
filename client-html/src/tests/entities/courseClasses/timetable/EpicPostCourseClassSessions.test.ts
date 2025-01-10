import { DefaultEpic } from "../../../common/Default.Epic";
import { postCourseClassSessions } from "../../../../js/containers/entities/courseClasses/components/timetable/actions";
import { EpicPostCourseClassSessions } from "../../../../js/containers/entities/courseClasses/components/timetable/epics/EpicPostCourseClassSessions";

describe("Update course class sessions epic tests", () => {
  it("EpicPostCourseClassSessions should returns correct values", () => DefaultEpic({
    action: mockedApi => postCourseClassSessions(1, mockedApi.db.getCourseClassTimetable()),
    epic: EpicPostCourseClassSessions,
    processData: () => []
  }));
});
