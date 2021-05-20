import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function PaymentInApiMock() {
  this.api.onGet(new RegExp(`v1/list/entity/paymentIn/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getPaymentIn(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/paymentIn/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/paymentIn").reply(config => {
    this.db.createPaymentIn(config.data);
    return promiseResolve(config, this.db.getPaymentsIn());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/paymentIn/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removePaymentIn(id);
    return promiseResolve(config, {});
  });

  this.api.onPost(new RegExp(`v1/list/entity/paymentIn/reverse/\\d+`)).reply(config => promiseResolve(config, {}));
}
