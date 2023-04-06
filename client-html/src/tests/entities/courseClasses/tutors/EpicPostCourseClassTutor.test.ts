import { DefaultEpic } from "../../../common/Default.Epic";
import { postCourseClassTutor } from "../../../../js/containers/entities/courseClasses/components/tutors/actions";
import { EpicPostCourseClassTutor } from "../../../../js/containers/entities/courseClasses/components/tutors/epics/EpicPostCourseClassTutor";

describe("Create course class tutor epic tests", () => {
  it("EpicPostCourseClassTutor should returns correct values", () => DefaultEpic({
    action: mockedApi => postCourseClassTutor(mockedApi.db.getCourseClassTutors("1").find(c => c.classId === 1)),
    epic: EpicPostCourseClassTutor,
    processData: () => []
  }));
});
