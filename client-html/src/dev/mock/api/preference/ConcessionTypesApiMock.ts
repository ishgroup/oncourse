import { ValidationError } from "@api/model";
import { promiseReject, promiseResolve } from "../../MockAdapter";

export function ConcessionTypesApiMock(mock) {
  /**
   * Concession type items
   * */

  this.returnError = false;

  this.api.onGet("v1/preference/concession/type").reply(config => promiseResolve(config, this.db.concessionTypes));
  /**
   * Mock Concession Types save success or error
   * */
  this.api.onPost("v1/preference/concession/type").reply(config => {
    const data = JSON.parse(config.data);
    data.forEach(i => {
      if (!i.id) i.id = Math.random().toString();
    });
    this.db.saveConcessionType(data);
    return promiseResolve(config, this.db.concessionTypes);
  });
  /**
   * Mock Concession Type delete
   * */
  this.api.onDelete(new RegExp(`v1/preference/concession/type/\\d+`)).reply(config => {
    const id = config.url.split("/")[4];
    this.db.removeConcessionType(id);
    return promiseResolve(config, this.db.concessionTypes);
  });
}
