import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function VoucherProductApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/voucherProduct/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getVoucherProduct(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/voucherProduct/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/voucherProduct").reply(config => {
    this.db.createVoucherProduct(config.data);
    return promiseResolve(config, this.db.getVoucherProducts());
  });
}
