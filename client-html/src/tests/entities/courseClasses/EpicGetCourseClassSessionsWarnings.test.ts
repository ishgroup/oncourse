import { DefaultEpic } from "../../common/Default.Epic";
import { getCourseClassSessionsWarnings, setCourseClassSessionsWarnings } from "../../../js/containers/entities/courseClasses/actions";
import { EpicGetCourseClassSessionsWarnings } from "../../../js/containers/entities/courseClasses/epics/EpicGetCourseClassSessionsWarnings";

describe("Get course class sessions warnings epic tests", () => {
  it("EpicGetCourseClassSessionsWarnings should returns correct values", () => DefaultEpic({
    action: mockedApi => getCourseClassSessionsWarnings(1, mockedApi.db.getCourseClassTimetable()),
    epic: EpicGetCourseClassSessionsWarnings,
    processData: mockedApi => {
      const warnings = mockedApi.db.getCourseClassTimetableSessions();

      return [setCourseClassSessionsWarnings(warnings)];
    }
  }));
});
