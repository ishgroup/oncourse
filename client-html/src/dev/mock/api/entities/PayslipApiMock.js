import { promiseResolve } from "../../MockAdapter";
export function PayslipApiMock(mock) {
    this.api.onGet(new RegExp(`v1/list/entity/payslip/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getPayslip(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/payslip/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
    this.api.onPost("v1/list/entity/payslip").reply(config => {
        this.db.createPayslip(config.data);
        return promiseResolve(config, this.db.getPayslips());
    });
}
//# sourceMappingURL=PayslipApiMock.js.map