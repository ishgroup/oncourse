import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_CONTACT_PRIOR_LEARNINGS_FULFILLED,
  getContactPriorLearnings
} from "../../../js/containers/entities/contacts/actions";
import {
  contactPriorLearningsMap,
  EpicGetContactPriorLearnings
} from "../../../js/containers/entities/contacts/epics/EpicGetContactPriorLearnings";

describe("Get contact prior learnings epic tests", () => {
  it("EpicGetContactPriorLearnings should returns correct values", () => DefaultEpic({
    action: getContactPriorLearnings(1),
    epic: EpicGetContactPriorLearnings,
    processData: mockedApi => {
      const response = mockedApi.db.getPlainPriorLearnings();
      const priorLearnings = response.rows.map(contactPriorLearningsMap);

      return [
        {
          type: GET_CONTACT_PRIOR_LEARNINGS_FULFILLED,
          payload: { priorLearnings }
        }
      ];
    }
  }));
});
