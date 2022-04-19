import { DefaultEpic } from "../../../common/Default.Epic";
import { EpicCreateClassCost } from "../../../../js/containers/entities/courseClasses/components/budget/epics/EpicCreateClassCost";
import { postCourseClassCost } from "../../../../js/containers/entities/courseClasses/components/budget/actions";

describe("Create course class cost epic tests", () => {
  it("EpicCreateClassCost should returns correct values", () => DefaultEpic({
    action: mockedApi => postCourseClassCost(mockedApi.db.getCourseClassBudget("1").find(c => c.courseClassid === 1)),
    epic: EpicCreateClassCost,
    processData: () => []
  }));
});
