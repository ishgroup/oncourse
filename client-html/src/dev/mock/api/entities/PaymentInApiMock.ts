import { promiseResolve } from "../../MockAdapter";

export function PaymentInApiMock() {
  this.api.onGet(new RegExp(`v1/list/entity/paymentIn/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getPaymentIn(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/paymentIn/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/paymentIn").reply(config => {
    this.db.createPaymentIn(config.data);
    return promiseResolve(config, this.db.getPaymentsIn());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/paymentIn/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removePaymentIn(id);
    return promiseResolve(config, this.db.getPaymentsIn());
  });

  this.api.onPost(new RegExp(`v1/list/entity/paymentIn/reverse/\\d+`)).reply(config => promiseResolve(config, {}));
}
