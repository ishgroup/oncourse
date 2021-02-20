import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  CREATE_DATA_COLLECTION_RULE_FULFILLED,
  createDataCollectionRule
} from "../../../js/containers/preferences/actions";
import { EpicCreateDataCollectionRule } from "../../../js/containers/preferences/containers/data-collection-rules/epics/EpicCreateDataCollectionRule";

describe("Create data collection rule epic tests", () => {
  it("EpicCreateDataCollectionRule should returns correct values", () => DefaultEpic({
    action: mockedApi => createDataCollectionRule(mockedApi.db.dataCollectionRules[0]),
    epic: EpicCreateDataCollectionRule,
    processData: mockedApi => {
      const dataCollectionRules = mockedApi.db.dataCollectionRules;

      return [
        {
          type: CREATE_DATA_COLLECTION_RULE_FULFILLED,
          payload: { dataCollectionRules }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "New rule was successfully created" }
        }
      ];
    }
  }));
});
