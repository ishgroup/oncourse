import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  DELETE_DATA_COLLECTION_RULE_FULFILLED,
  removeDataCollectionRule
} from "../../../js/containers/preferences/actions";
import { EpicDeleteDataCollectionRule } from "../../../js/containers/preferences/containers/data-collection-rules/epics/EpicDeleteDataCollectionRule";

const id = "55667";

describe("Delete data collection rule epic tests", () => {
  it("EpicDeleteDataCollectionRule should returns correct values", () => DefaultEpic({
    action: removeDataCollectionRule(id),
    epic: EpicDeleteDataCollectionRule,
    processData: mockedApi => {
      const dataCollectionRules = mockedApi.db.dataCollectionRules.filter(form => String(form.id) !== String(id));

      return [
        {
          type: DELETE_DATA_COLLECTION_RULE_FULFILLED,
          payload: { dataCollectionRules }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Rule was successfully deleted" }
        }
      ];
    }
  }));
});
