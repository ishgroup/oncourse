import { promiseReject, promiseResolve } from "../../MockAdapter";
import { ValidationError} from "@api/model";

export function PaymentApiMock(mock) {
  /**
   * Payment type items
   **/

  this.returnError = false;

  this.api.onGet("/v1/preference/payment/type").reply(config => {
    return promiseResolve(config, this.db.paymentTypes);
  });
  /**
   * Mock Payment Types save success or error
   **/
  this.api.onPost("v1/preference/payment/type").reply(config => {
    this.returnError = !this.returnError;

    if (this.returnError) {
      const errorObj: ValidationError = {
        id: "886543",
        propertyName: "undepositAccountId",
        errorMessage: "Undeposit Account Id is invalid"
      };

      return promiseReject(config, errorObj);
    }
    const data = JSON.parse(config.data);
    data.forEach(i => {
      if (!i.id) i.id = Math.random().toString();
    });
    this.db.savePaymentType(data);
    return promiseResolve(config, this.db.paymentTypes);
  });
  /**
   * Mock Payment Type delete
   **/
  this.api.onDelete(new RegExp(`v1/preference/payment/type/\\d+`)).reply(config => {
    const id = config.url.split("/")[4];
    this.db.removePaymentType(id);
    return promiseResolve(config, this.db.paymentTypes);
  });
}
