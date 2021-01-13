import { promiseResolve } from "../../MockAdapter";

export function BankingApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/banking/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getBanking(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/banking/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/banking").reply(config => {
    this.db.createBanking(config.data);
    return promiseResolve(config, this.db.getBankings());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/banking/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeBanking(id);
    return promiseResolve(config, this.db.getBankings());
  });

  this.api.onGet(new RegExp(`v1/list/entity/banking/depositPayments/\\d+/\\d+`)).reply(config => promiseResolve(config, this.db.getDepositPayment()));
}
