import { DefaultEpic } from "../../common/Default.Epic";
import { getDuplicateCourseClassesBudget, SET_DUPLICATE_COURSE_CLASSES_BUDGET } from "../../../js/containers/entities/courseClasses/actions";
import { EpicGetDuplicateTraineeshipBudget } from "../../../js/containers/entities/courseClasses/epics/EpicGetDuplicateTraineeshipBudget";

describe("Get duplicate traineeship budget epic tests", () => {
  it("EpicGetDuplicateTraineeshipBudget should returns correct values", () => DefaultEpic({
    action: () => getDuplicateCourseClassesBudget(3),
    epic: EpicGetDuplicateTraineeshipBudget,
    processData: mockedApi => {
      const budget = mockedApi.db.getCourseClassBudget("3");
      const studentFee = budget.find(b => b.invoiceToStudent);

      if (studentFee && studentFee.paymentPlan.length) {
        studentFee.paymentPlan.sort((a, b) => (a.dayOffset > b.dayOffset ? 1 : -1));
      }

      return [{
        type: SET_DUPLICATE_COURSE_CLASSES_BUDGET,
        payload: budget
      }];
    }
  }));
});
