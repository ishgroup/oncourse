import { promiseResolve } from "../../MockAdapter";

export function SiteApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/site/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getSite(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/site/\\d+`)).reply(config => {
    return promiseResolve(config, JSON.parse(config.data));
  });

}
