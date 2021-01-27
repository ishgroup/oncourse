import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function DiscountApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/discount/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getDiscount(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/discount/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/discount").reply(config => {
    this.db.createDiscount(config.data);
    return promiseResolve(config, this.db.getDiscounts());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/discount/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeDiscount(id);
    return promiseResolve(config, {});
  });
}
