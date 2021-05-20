import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function CustomFieldApiMock(mock) {
  /**
   * Custom Field items
   * */
  this.api.onGet("v1/preference/field/type").reply(config => promiseResolve(config, this.db.customFields));
  /**
   * Mock Custom Fields save success or error
   * */
  this.api.onPost("v1/preference/field/type").reply(config => {
    const data = JSON.parse(config.data);
    data.forEach(i => {
      if (!i.id) i.id = Math.random().toString();
    });
    this.db.saveCustomFields(data);
    return promiseResolve(config, this.db.customFields);
  });
  /**
   * Mock Custom Field delete
   * */
  this.api.onDelete(new RegExp(`v1/preference/field/type/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeCustomField(id);
    return promiseResolve(config, this.db.customFields);
  });
}
