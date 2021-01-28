import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function InvoiceApiMock() {
  this.api.onGet(new RegExp(`v1/list/entity/invoice/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getInvoice(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/invoice/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/invoice").reply(config => {
    this.db.createInvoice(config.data);
    return promiseResolve(config, this.db.getInvoices());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/invoice/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeInvoice(id);
    return promiseResolve(config, {});
  });

  this.api.onPost(new RegExp(`v1/list/entity/invoice/contra/\\d+`)).reply(config => promiseResolve(config, {}));
}
