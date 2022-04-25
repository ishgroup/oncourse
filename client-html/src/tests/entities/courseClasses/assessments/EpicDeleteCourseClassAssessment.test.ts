import { DefaultEpic } from "../../../common/Default.Epic";
import { deleteCourseClassAssessment } from "../../../../js/containers/entities/courseClasses/components/assessments/actions";
import { EpicDeleteCourseClassAssessment } from "../../../../js/containers/entities/courseClasses/components/assessments/epics/EpicDeleteCourseClassAssessment";

describe("Delete course class assessment epic tests", () => {
  it("EpicDeleteCourseClassAssessment should returns correct values", () => DefaultEpic({
    action: () => deleteCourseClassAssessment(1),
    epic: EpicDeleteCourseClassAssessment,
    processData: () => []
  }));
});
