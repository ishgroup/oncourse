import { promiseResolve } from "../../MockAdapter";

export function EnrolmentApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/enrolment/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getEnrolment(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/enrolment/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/enrolment").reply(config => {
    this.db.createEnrolment(config.data);
    return promiseResolve(config, this.db.getEnrolments());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/enrolment/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeEnrolment(id);
    return promiseResolve(config, this.db.getEnrolments());
  });

  this.api.onPost("v1/list/entity/enrolment/cancel").reply(config => promiseResolve(config, {}));
}
