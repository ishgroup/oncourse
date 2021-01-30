import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function SalesApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/sales/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getProductItem(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/sales/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/sales").reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onDelete(new RegExp(`v1/list/entity/sales/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeProductItem(id);
    return promiseResolve(config, {});
  });
}
