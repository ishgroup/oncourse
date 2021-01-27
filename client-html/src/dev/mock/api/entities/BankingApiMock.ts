import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function BankingApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/banking/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getBanking(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/banking/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/banking").reply(config => {
    this.db.createBanking(config.data);
    return promiseResolve(config, this.db.getBankings());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/banking/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeBanking(id);
    return promiseResolve(config, this.db.getBankings());
  });

  this.api.onGet(new RegExp(`v1/list/entity/banking/depositPayments/\\d+/\\d+`)).reply(config => promiseResolve(config, this.db.getDepositPayment()));

  this.api.onPost("v1/list/entity/banking/reconcile").reply(config => promiseResolve(config, {}));
}
