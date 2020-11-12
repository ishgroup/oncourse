import { promiseReject, promiseResolve } from "../../MockAdapter";
import { ValidationError } from "@api/model";

export function ConcessionTypesApiMock(mock) {
  /**
   * Concession type items
   **/

  this.returnError = false;

  this.api.onGet("v1/preference/concession/type").reply(config => {
    return promiseResolve(config, this.db.concessionTypes);
  });
  /**
   * Mock Concession Types save success or error
   **/
  this.api.onPost("v1/preference/concession/type").reply(config => {
    this.returnError = !this.returnError;

    if (this.returnError) {
      const errorObj: ValidationError = {
        id: "32435",
        propertyName: "name",
        errorMessage: "Name is invalid"
      };

      return promiseReject(config, errorObj);
    }
    const data = JSON.parse(config.data);
    data.forEach(i => {
      if (!i.id) i.id = Math.random().toString();
    });
    this.db.saveConcessionType(data);
    return promiseResolve(config, this.db.concessionTypes);
  });
  /**
   * Mock Concession Type delete
   **/
  this.api.onDelete(new RegExp(`v1/preference/concession/type/\\d+`)).reply(config => {
    const id = config.url.split("/")[4];
    this.db.removeConcessionType(id);
    return promiseResolve(config, this.db.concessionTypes);
  });
}
