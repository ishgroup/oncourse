import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function EnrolmentApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/enrolment/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getEnrolment(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/enrolment/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/enrolment").reply(config => {
    this.db.createEnrolment(config.data);
    return promiseResolve(config, this.db.getEnrolments());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/enrolment/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removeEnrolment(id);
    return promiseResolve(config, {});
  });

  this.api.onPost("v1/list/entity/enrolment/cancel").reply(config => promiseResolve(config, {}));
}
