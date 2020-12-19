import { promiseResolve } from "../../MockAdapter";

export function ImportTemplateApiMock() {
  this.api.onGet(new RegExp(`v1/list/entity/import/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getImportTemplate(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/import/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/import").reply(config => {
    this.db.createImportTemplate(config.data);
    return promiseResolve(config, this.db.getImportTemplates());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/import/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeImportTemplate(id);
    return promiseResolve(config, this.db.getImportTemplates());
  });
}
