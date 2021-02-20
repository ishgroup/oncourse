import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_DATA_COLLECTION_RULES_FULFILLED,
  getDataCollectionRules
} from "../../../js/containers/preferences/actions";
import { EpicGetDataCollectionRules } from "../../../js/containers/preferences/containers/data-collection-rules/epics/EpicGetDataCollectionRules";

describe("Get data collection rules epic tests", () => {
  it("EpicGetDataCollectionRules should returns correct values", () => DefaultEpic({
    action: getDataCollectionRules(),
    epic: EpicGetDataCollectionRules,
    processData: mockedApi => {
      const dataCollectionRules = mockedApi.db.dataCollectionRules;

      return [
        {
          type: GET_DATA_COLLECTION_RULES_FULFILLED,
          payload: { dataCollectionRules }
        }
      ];
    }
  }));
});
