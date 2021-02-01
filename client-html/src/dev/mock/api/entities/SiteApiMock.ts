import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function SiteApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/site/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getSite(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/site/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/site").reply(config => {
    this.db.createSite(config.data);
    return promiseResolve(config, {});
  });
}
