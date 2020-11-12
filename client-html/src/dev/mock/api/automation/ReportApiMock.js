import { promiseResolve } from "../../MockAdapter";
export function ReportApiMock(mock) {
    this.api.onGet(new RegExp(`v1/list/entity/report/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getReport(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/report/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
    this.api.onPost("v1/list/entity/report").reply(config => {
        this.db.createReport(config.data);
        return promiseResolve(config, this.db.getReports());
    });
    this.api.onDelete(new RegExp(`v1/list/entity/report/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        this.db.removeReport(id);
        return promiseResolve(config, this.db.getReports());
    });
}
//# sourceMappingURL=ReportApiMock.js.map