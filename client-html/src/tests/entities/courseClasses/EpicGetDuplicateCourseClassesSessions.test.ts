import { DefaultEpic } from "../../common/Default.Epic";
import { getDuplicateCourseClassesSessions, setDuplicateCourseClassesSessions } from "../../../js/containers/entities/courseClasses/actions";
import { EpicGetDuplicateCourseClassesSessions } from "../../../js/containers/entities/courseClasses/epics/EpicGetDuplicateCourseClassesSessions";

const selection = ["3"];

describe("Get duplicate course classes sessions epic tests", () => {
  it("EpicGetDuplicateCourseClassesSessions should returns correct values", () => DefaultEpic({
    action: () => getDuplicateCourseClassesSessions(selection),
    epic: EpicGetDuplicateCourseClassesSessions,
    processData: mockedApi => {
      const sessionsResponse = mockedApi.db.getCourseClassTimetable();
      const tagsResponse = mockedApi.db.getTimetableSessionsTags(selection.toString());
      const sessions = sessionsResponse.map((s, index) => ({ ...s, tags: tagsResponse[index] }));
      const from = new Date(sessions[0].start);

      return [setDuplicateCourseClassesSessions(sessions, from, false)];
    }
  }));
});
