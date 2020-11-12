import { promiseResolve } from "../../MockAdapter";

export function ReportOverlayApiMock(mock) {
  this.api.onGet(new RegExp(`v1/list/entity/reportOverlay/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    return promiseResolve(config, this.db.getReportOverlay(id));
  });

  this.api.onPut(new RegExp(`v1/list/entity/reportOverlay/\\d+`)).reply(config => {
    return promiseResolve(config, JSON.parse(config.data));
  });

  this.api.onPost("v1/list/entity/reportOverlay").reply(config => {
    this.db.createReportOverlay(config.data);
    return promiseResolve(config, this.db.getReportOverlays());
  });

  this.api.onDelete(new RegExp(`v1/list/entity/reportOverlay/\\d+`)).reply(config => {
    const params = config.url.split("/");
    const id = params[params.length - 1];
    this.db.removeReportOverlay(id);
    return promiseResolve(config, this.db.getReportOverlays());
  });
}
