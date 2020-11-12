import { promiseReject, promiseResolve } from "../../MockAdapter";
import { ValidationError } from "@api/model";

export function TaxTypesApiMock(mock) {
  /**
   * Tax type items
   **/

  this.returnError = false;

  this.api.onGet("v1/preference/tax").reply(config => {
    return promiseResolve(config, this.db.taxTypes);
  });
  /**
   * Mock Tax Types save success or error
   **/
  this.api.onPost("v1/preference/tax").reply(config => {
    this.returnError = !this.returnError;

    if (this.returnError) {
      const errorObj: ValidationError = {
        id: "5684452",
        propertyName: "payableAccountId",
        errorMessage: "Payable Account Id Id is invalid"
      };

      return promiseReject(config, errorObj);
    }
    const data = JSON.parse(config.data);
    data.forEach(i => {
      if (!i.id) i.id = Math.random().toString();
    });
    this.db.saveTaxType(data);
    return promiseResolve(config, this.db.taxTypes);
  });
  /**
   * Mock Tax Type delete
   **/
  this.api.onDelete(new RegExp(`v1/preference/tax/\\d+`)).reply(config => {
    const id = config.url.split("/")[3];
    this.db.removeTaxType(id);
    return promiseResolve(config, this.db.taxTypes);
  });
}
