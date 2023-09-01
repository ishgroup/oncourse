import { DefaultEpic } from "../../../common/Default.Epic";
import {
  EpicUpdateCourseClassTrainingPlans
} from "../../../../js/containers/entities/courseClasses/components/attendance/epics/EpicUpdateCourseClassTrainingPlans";
import {
  updateCourseClassTrainingPlans
} from "../../../../js/containers/entities/courseClasses/components/attendance/actions";

describe("Update course class training plans epic tests", () => {
  it("EpicUpdateCourseClassTrainingPlans should returns correct values", () => DefaultEpic({
    action: mockedApi => updateCourseClassTrainingPlans(1, mockedApi.db.getCourseClassTrainingPlan()),
    epic: EpicUpdateCourseClassTrainingPlans,
    processData: () => []
  }));
});
