import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function CorporatePassApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/corporatepass/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getCorporatePass(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/corporatepass/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onDelete(new RegExp(`v1/list/entity/corporatepass/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeCorporatePass(id);
    return promiseResolve(config, {});
  });

  this.api.onPost("v1/list/entity/corporatepass").reply(config => {
    this.db.createCorporatePass(config.data);
    return promiseResolve(config, this.db.getCorporatePasses());
  });
}
