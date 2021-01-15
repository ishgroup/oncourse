import { promiseResolve } from "../../MockAdapter";

export function CertificateApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/certificate/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getCertificate(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/certificate/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/certificate").reply(config => {
    this.db.createCertificate(config.data);
    return promiseResolve(config, this.db.getCertificates());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/certificate/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeCertificate(id);
    return promiseResolve(config, this.db.getCertificates());
  });

  this.api.onPost("v1/list/entity/certificate/revoke").reply(config => promiseResolve(config, {}));
}
