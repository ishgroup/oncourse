import { promiseResolve } from "../../MockAdapter";
export function AvetmissExportApiMock(mock) {
    this.api.onPut("/v1/export/avetmiss8/outcomes").reply(config => {
        return promiseResolve(config, "0e8eb6ef-5ea9-452c-bc07-396a301bef8a");
    });
    this.api.onGet("v1/control/0e8eb6ef-5ea9-452c-bc07-396a301bef8a").reply(config => {
        return promiseResolve(config, { status: "Finished", message: null });
    });
    this.api.onGet("/v1/export/avetmiss8/outcomes/0e8eb6ef-5ea9-452c-bc07-396a301bef8a").reply(config => {
        return promiseResolve(config, this.db.getExportOutcomes());
    });
    this.api.onGet("/v1/export/avetmiss8/settings").reply(config => {
        return promiseResolve(config, this.db.getExportSettings());
    });
    this.api.onGet('/v1/export/avetmiss8/uploads').reply(config => {
        return promiseResolve(config, this.db.getAvetmissExportUploads());
    });
    this.api.onPut('/v1/export/avetmiss8/uploads').reply(config => {
        return promiseResolve(config, {});
    });
    this.api.onPut('/v1/export/avetmiss8').reply(config => {
        return promiseResolve(config, "0e8eb6ef-5ea9-452c-bc07-396a301bef8a");
    });
    this.api.onGet("/v1/export/avetmiss8/0e8eb6ef-5ea9-452c-bc07-396a301bef8a").reply(config => {
        return promiseResolve(config, "");
    });
}
//# sourceMappingURL=AvetmissExportApiMock.js.map