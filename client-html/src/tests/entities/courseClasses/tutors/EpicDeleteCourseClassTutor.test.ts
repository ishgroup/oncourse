import { DefaultEpic } from "../../../common/Default.Epic";
import { deleteCourseClassTutor } from "../../../../js/containers/entities/courseClasses/components/tutors/actions";
import { EpicDeleteCourseClassTutor } from "../../../../js/containers/entities/courseClasses/components/tutors/epics/EpicDeleteCourseClassTutor";

describe("Delete course class tutor epic tests", () => {
  it("EpicDeleteCourseClassTutor should returns correct values", () => DefaultEpic({
    action: () => deleteCourseClassTutor(1),
    epic: EpicDeleteCourseClassTutor,
    processData: () => []
  }));
});
