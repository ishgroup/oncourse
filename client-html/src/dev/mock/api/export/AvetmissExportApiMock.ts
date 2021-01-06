import { promiseResolve } from "../../MockAdapter";

export function AvetmissExportApiMock(mock) {
  this.api.onPut("/v1/export/avetmiss8/outcomes").reply(config => promiseResolve(config, this.db.outcomesID));

  this.api.onGet(`v1/control/${this.db.outcomesID}`).reply(config => promiseResolve(config, this.db.getExportProcess()));

  this.api.onGet(`/v1/export/avetmiss8/outcomes/${this.db.outcomesID}`).reply(config => promiseResolve(config, this.db.getExportOutcomes()));

  this.api.onGet("/v1/export/avetmiss8/settings").reply(config => promiseResolve(config, this.db.getExportSettings()));

  this.api.onGet('/v1/export/avetmiss8/uploads').reply(config => promiseResolve(config, this.db.getAvetmissExportUploads()));

  this.api.onPut('/v1/export/avetmiss8/uploads').reply(config => promiseResolve(config, {}));

  this.api.onPut('/v1/export/avetmiss8').reply(config => promiseResolve(config, this.db.outcomesID));

  this.api.onGet(`/v1/export/avetmiss8/${this.db.outcomesID}`).reply(config => promiseResolve(config, ""));
}
