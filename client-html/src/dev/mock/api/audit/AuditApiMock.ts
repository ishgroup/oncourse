import { promiseResolve } from "../../MockAdapter";

export function auditApiMock(mock) {
  /**
   * List items
   * */
  this.api.onGet(new RegExp(`v1/list/entity/audit/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getAudit(id));
  });
}
