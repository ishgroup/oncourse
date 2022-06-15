import { DefaultEpic } from "../../../common/Default.Epic";
import { createCourseClassAssessment } from "../../../../js/containers/entities/courseClasses/components/assessments/actions";
import { EpicCreateCourseClassAssessment } from "../../../../js/containers/entities/courseClasses/components/assessments/epics/EpicCreateCourseClassAssessment";

describe("Create course class assessment epic tests", () => {
  it("EpicCreateCourseClassAssessment should returns correct values", () => DefaultEpic({
    action: mockedApi => createCourseClassAssessment(mockedApi.db.getCourseClassAssessment(1).find(a => a.id === 1)),
    epic: EpicCreateCourseClassAssessment,
    processData: () => []
  }));
});
