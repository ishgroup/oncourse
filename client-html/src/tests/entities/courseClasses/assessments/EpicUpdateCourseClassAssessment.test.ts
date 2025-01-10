import { DefaultEpic } from "../../../common/Default.Epic";
import { updateCourseClassAssessment } from "../../../../js/containers/entities/courseClasses/components/assessments/actions";
import { EpicUpdateCourseClassAssessment } from "../../../../js/containers/entities/courseClasses/components/assessments/epics/EpicUpdateCourseClassAssessment";

describe("Update course class assessment epic tests", () => {
  it("EpicUpdateCourseClassAssessment should returns correct values", () => DefaultEpic({
    action: mockedApi => updateCourseClassAssessment(mockedApi.db.getCourseClassAssessment(1).find(a => a.id === 1)),
    epic: EpicUpdateCourseClassAssessment,
    processData: () => []
  }));
});
