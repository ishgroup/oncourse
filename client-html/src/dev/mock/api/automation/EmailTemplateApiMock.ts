import { promiseResolve } from "../../MockAdapter";

export function EmailTemplateApiMock() {
  this.api.onGet(new RegExp(`v1/list/entity/emailTemplate/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getEmailTemplate(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/emailTemplate/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/emailTemplate").reply(config => {
    this.db.createEmailTemplate(config.data);
    return promiseResolve(config, this.db.getEmailTemplates());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/emailTemplate/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeEmailTemplate(id);
    return promiseResolve(config, this.db.getEmailTemplates());
  });

  this.api.onGet("/v1/list/entity/emailTemplate/template").reply(config => {
    const entityName = config.params.entityName;
    return promiseResolve(config, this.db.getEmailTemplatesByEntityName(entityName));
  });
}
