import { DefaultEpic } from "../../common/Default.Epic";
import {
  UPDATE_DATA_COLLECTION_RULE_FULFILLED,
  updateDataCollectionRule
} from "../../../js/containers/preferences/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicUpdateDataCollectionRule } from "../../../js/containers/preferences/containers/data-collection-rules/epics/EpicUpdateDataCollectionRule";

describe("Update data collection rule epic tests", () => {
  it("EpicUpdateDataCollectionRule should returns correct values", () => DefaultEpic({
    action: mockedApi => updateDataCollectionRule("55667", mockedApi.db.dataCollectionRules[0]),
    epic: EpicUpdateDataCollectionRule,
    processData: mockedApi => {
      const dataCollectionRules = mockedApi.db.dataCollectionRules;

      return [
        {
          type: UPDATE_DATA_COLLECTION_RULE_FULFILLED,
          payload: { dataCollectionRules }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Rule was successfully updated" }
        }
      ];
    }
  }));
});
