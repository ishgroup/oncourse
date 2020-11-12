import { promiseResolve } from "../../MockAdapter";

export function ArticleProductApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/articleProduct/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getArticleProduct(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/articleProduct/\\d+`)).reply(config => {
    return promiseResolve(config, JSON.parse(config.data));
  });

  this.api.onPost("v1/list/entity/articleProduct").reply(config => {
    this.db.createArticleProduct(config.data);
    return promiseResolve(config, this.db.getArticleProducts());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/articleProduct/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeArticleProduct(id);
    return promiseResolve(config, this.db.getArticleProducts());
  });
}
