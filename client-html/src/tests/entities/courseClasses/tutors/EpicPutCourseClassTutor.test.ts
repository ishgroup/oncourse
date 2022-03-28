import { DefaultEpic } from "../../../common/Default.Epic";
import { putCourseClassTutor } from "../../../../js/containers/entities/courseClasses/components/tutors/actions";
import { EpicPutCourseClassTutor } from "../../../../js/containers/entities/courseClasses/components/tutors/epics/EpicPutCourseClassTutor";

describe("Update course class tutor epic tests", () => {
  it("EpicPutCourseClassTutor should returns correct values", () => DefaultEpic({
    action: mockedApi => putCourseClassTutor(mockedApi.db.getCourseClassTutors(1).find(c => c.classId === 1)),
    epic: EpicPutCourseClassTutor,
    processData: () => []
  }));
});
