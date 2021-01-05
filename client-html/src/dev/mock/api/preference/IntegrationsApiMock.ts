import { promiseResolve } from "../../MockAdapter";

export function IntegrationsApiMock(mock) {
  this.returnConfigured = false;
  /**
   * Integrations items
   * */
  this.api.onGet("/v1/integration").reply(config => {
    if (this.returnConfigured) {
      this.db.integrations.map(item => ({ ...item, verificationCode: true }));
    } else {
      this.db.integrations.map(item => ({ ...item, verificationCode: false }));
    }

    this.returnConfigured = !this.returnConfigured;

    return promiseResolve(config, this.db.getIntegrations());
  });

  this.api.onPost("/v1/integration").reply(config => {
    const data = JSON.parse(config.data);
    this.db.createIntegration(data);
    return promiseResolve(config, this.db.getIntegrations());
  });

  this.api.onPut(new RegExp(`v1/integration/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onDelete(new RegExp(`v1/integration/\\d+`)).reply(config => {
    const id = config.url.split("/")[2];
    this.db.removeIntegration(id);
    return promiseResolve(config, this.db.getIntegrations());
  });
}
