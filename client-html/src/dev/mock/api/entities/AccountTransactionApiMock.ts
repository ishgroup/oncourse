import { promiseResolve } from "../../MockAdapter";

export function AccountTransactionApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/accountTransaction/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getAccountTransaction(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/accountTransaction/\\d+`)).reply(config => {
    return promiseResolve(config, JSON.parse(config.data));
  });

  this.api.onPost("v1/list/entity/accountTransaction").reply(config => {
    this.db.createAccountTransaction(config.data);
    return promiseResolve(config, this.db.getAccountTransactions());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/accountTransaction/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeAccountTransaction(id);
    return promiseResolve(config, this.db.getAccountTransactions());
  });
}
