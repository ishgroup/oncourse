import { DefaultEpic } from "../../../common/Default.Epic";
import { EpicDeleteClassCost } from "../../../../js/containers/entities/courseClasses/components/budget/epics/EpicDeleteClassCost";
import { deleteCourseClassCost } from "../../../../js/containers/entities/courseClasses/components/budget/actions";

describe("Delete course class cost epic tests", () => {
  it("EpicDeleteClassCost should returns correct values", () => DefaultEpic({
    action: () => deleteCourseClassCost(1),
    epic: EpicDeleteClassCost,
    processData: () => []
  }));
});
