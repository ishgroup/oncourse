import { ValidationError } from "@api/model";
import { promiseReject, promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function CustomFieldApiMock(mock) {
  /**
   * Custom Field items
   * */

  this.returnError = false;

  this.api.onGet("v1/preference/field/type").reply(config => promiseResolve(config, this.db.customFields));
  /**
   * Mock Custom Fields save success or error
   * */
  this.api.onPost("v1/preference/field/type").reply(config => {
    this.returnError = !this.returnError;

    if (this.returnError) {
      const errorObj: ValidationError = {
        id: "5684452",
        propertyName: "defaultValue",
        errorMessage: "Default Value is invalid"
      };

      return promiseReject(config, errorObj);
    }
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
