import { promiseResolve } from "../../MockAdapter";

export function DocumentApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/document/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getDocument(id));
  });

  this.api.onPost("v1/list/entity/document/search").reply(config => promiseResolve(config, {}));

  this.api.onPost("v1/list/entity/document").reply(config => {
    this.db.addDocument(config.data);
    return promiseResolve(config, this.db.getDocuments());
  });

  this.api.onPut(new RegExp(`v1/list/entity/document/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onDelete(new RegExp(`v1/list/entity/document/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeDocument(id);
    return promiseResolve(config, {});
  });
}
