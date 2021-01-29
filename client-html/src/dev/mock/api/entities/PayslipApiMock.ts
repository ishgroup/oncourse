import { promiseResolve } from "../../MockAdapter";
import { getParamsId } from "../../mockUtils";

export function PayslipApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/payslip/\\d+`)).reply(config => {
    const id = getParamsId(config);
    return promiseResolve(config, this.db.getPayslip(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/payslip/\\d+`)).reply(config => promiseResolve(config, JSON.parse(config.data)));

  this.api.onPost("v1/list/entity/payslip").reply(config => {
    this.db.createPayslip(config.data);
    return promiseResolve(config, this.db.getPayslips());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/payslip/\\d+`)).reply(config => {
    const id = getParamsId(config);
    this.db.removePayslip(id);
    return promiseResolve(config, {});
  });
}
