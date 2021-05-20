import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function PaymentOutApiMock() {
  this.api.onGet(new RegExp(`v1/list/entity/paymentOut/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getPaymentOut(id));
  });

  this.api.onPut("v1/list/entity/paymentOut").reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/paymentOut").reply(config => {
    this.db.createPaymentOut(config.data);
    return promiseResolve(config, this.db.getPaymentsOut());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/paymentOut/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removePaymentOut(id);
    return promiseResolve(config, {});
  });
}
