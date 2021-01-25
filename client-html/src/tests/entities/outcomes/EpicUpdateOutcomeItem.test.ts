import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateOutcomeItem } from "../../../js/containers/entities/outcomes/epics/EpicUpdateOutcomeItem";
import { GET_OUTCOME_ITEM, updateOutcome } from "../../../js/containers/entities/outcomes/actions";

describe("Update outcome epic tests", () => {
  it("EpicUpdateOutcomeItem should returns correct values", () => DefaultEpic({
    action: mockedApi => updateOutcome("1", mockedApi.db.getOutcome(1)),
    epic: EpicUpdateOutcomeItem,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Outcome Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Outcome", listUpdate: true, savedID: "1" }
      },
      {
        type: GET_OUTCOME_ITEM,
        payload: "1"
      }
    ]
  }));
});
