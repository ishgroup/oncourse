import { promiseResolve } from "../../MockAdapter";

export function ScriptApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/script/execute/\\d+`))
    .reply(config => promiseResolve(config, null, { "content-type": "application/pdf" }));

  this.api.onGet(new RegExp(`v1/list/entity/script/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getScript(id));
  });

  this.api.onPatch(new RegExp(`v1/list/entity/script/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/script").reply(config => {
    this.db.createScript(config.data);
    return promiseResolve(config, this.db.getScripts());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/script/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeScript(id);
    return promiseResolve(config, this.db.getScripts());
  });
}
