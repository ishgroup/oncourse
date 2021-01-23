import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function CertificateApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/certificate/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getCertificate(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/certificate/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/certificate").reply(config => {
    this.db.createCertificate(config.data);
    return promiseResolve(config, this.db.getCertificates());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/certificate/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeCertificate(id);
    return promiseResolve(config, {});
  });

  this.api.onPost("v1/list/entity/certificate/revoke").reply(config => promiseResolve(config, {}));

  this.api.onPost("v1/list/entity/certificate/validation").reply(config => promiseResolve(config, this.db.validateCertificateStatus()));
}
