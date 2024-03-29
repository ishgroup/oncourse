import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function DataCollectionRulesApiMock() {
  /**
   * Data Collection Rules items
   * */
  this.api.onGet("/v1/datacollection/rule").reply(config => promiseResolve(config, this.db.dataCollectionRules));
  /**
   * Update Data Collection Rule with success or error
   * */
  this.api.onPut(new RegExp(`v1/datacollection/rule/.+`)).reply(config => {
    const id = getParamsId(config);
    const data = JSON.parse(config.data);
    this.db.updateCollectionRule(id, data);
    return promiseResolve(config, JSON.parse(JSON.stringify(this.db.dataCollectionRules)));
  });
  /**
   * Create Data Collection Rule
   * */
  this.api.onPost("v1/datacollection/rule").reply(config => {
    return promiseResolve(config, JSON.parse(JSON.stringify(this.db.dataCollectionRules)));
  });
  /**
   * Delete Data Collection Rule
   * */
  this.api.onDelete(new RegExp(`v1/datacollection/rule/.+`)).reply(config => {
    const id = getParamsId(config);
    this.db.deleteCollectionRule(id);
    return promiseResolve(config, JSON.parse(JSON.stringify(this.db.dataCollectionRules)));
  });
}
