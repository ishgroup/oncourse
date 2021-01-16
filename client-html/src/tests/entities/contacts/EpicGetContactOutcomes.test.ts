import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_CONTACT_OUTCOMES_FULFILLED,
  getContactOutcomes
} from "../../../js/containers/entities/contacts/actions";
import {
  contactOutcomesMap,
  EpicGetContactOutcomes
} from "../../../js/containers/entities/contacts/epics/EpicGetContactOutcomes";

describe("Get contact outcomes epic tests", () => {
  it("EpicGetContactOutcomes should returns correct values", () => DefaultEpic({
    action: getContactOutcomes(1),
    epic: EpicGetContactOutcomes,
    processData: mockedApi => {
      const response = [mockedApi.db.getPlainOutcomes(), mockedApi.db.getPlainOutcomes()];
      const combinedRows = [...response[0].rows, ...response[1].rows];
      const outcomes = combinedRows.map(contactOutcomesMap);

      return [
        {
          type: GET_CONTACT_OUTCOMES_FULFILLED,
          payload: { outcomes }
        }
      ];
    }
  }));
});
