import { promiseResolve } from "../../MockAdapter";

export function CheckoutApiMock() {
  this.api.onPost("v1/checkout").reply(config => {
    return promiseResolve(config, this.db.getProcessedCheckout());
  });

  this.api.onGet("v1/checkout/saleRelations").reply(config => {
    return promiseResolve(config, []);
  });

  this.api.onGet("v1/checkout/discount").reply(config => {
    return promiseResolve(config, []);
  });
}