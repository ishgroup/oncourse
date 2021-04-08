import { promiseResolve } from "../../MockAdapter";

export function FundingContractsApiMock() {
  this.api.onGet("v1/preference/fundingcontract").reply(config => {
    return promiseResolve(config, this.db.fundingContracts);
  });

  this.api.onPatch("v1/preference/fundingcontract").reply(config => {
    return promiseResolve(config, this.db.fundingContracts);
  });
}
