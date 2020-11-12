import { promiseResolve } from "../../MockAdapter";

export function DiscountApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/discount/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getDiscount(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/discount/\\d+`)).reply(config => {
    return promiseResolve(config, JSON.parse(config.data));
  });

  this.api.onPost("v1/list/entity/discount").reply(config => {
    this.db.createDiscount(config.data);
    return promiseResolve(config, this.db.getDiscounts());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/discount/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeDiscount(id);
    return promiseResolve(config, this.db.getDiscounts());
  });
}
