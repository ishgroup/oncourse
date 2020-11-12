import { promiseResolve } from "../../MockAdapter";

export function ExportTemplateApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/exportTemplate/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getExportTemplate(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/exportTemplate/\\d+`)).reply(config => {
    return promiseResolve(config, JSON.parse(config.data));
  });

  this.api.onPost("v1/list/entity/exportTemplate").reply(config => {
    this.db.createExportTemplate(config.data);
    return promiseResolve(config, this.db.getExportTemplates());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/exportTemplate/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeExportTemplate(id);
    return promiseResolve(config, this.db.getExportTemplates());
  });
}
