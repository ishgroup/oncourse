import { DefaultEpic } from "../../../common/Default.Epic";
import { EpicUpdateClassCost } from "../../../../js/containers/entities/courseClasses/components/budget/epics/EpicUpdateClassCost";
import { putCourseClassCost } from "../../../../js/containers/entities/courseClasses/components/budget/actions";

describe("Update course class cost epic tests", () => {
  it("EpicUpdateClassCost should returns correct values", () => DefaultEpic({
    action: mockedApi => putCourseClassCost(mockedApi.db.getCourseClassBudget(1).find(c => c.courseClassid === 1)),
    epic: EpicUpdateClassCost,
    processData: () => []
  }));
});
