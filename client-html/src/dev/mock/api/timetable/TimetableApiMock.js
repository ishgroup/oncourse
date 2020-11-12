import { promiseResolve } from "../../MockAdapter";
export function TimetableApiMock(mock) {
    this.api.onGet(new RegExp(`v1/timetable/calendar/\\d+/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const year = params[params.length - 1];
        const month = params[params.length - 2];
        return promiseResolve(config, this.db.getDates(year, month));
    });
    this.api.onPost("/v1/timetable/session").reply(config => {
        return promiseResolve(config, this.db.findTimetableSession());
    });
    this.api.onGet("/v1/timetable/session").reply(config => {
        return promiseResolve(config, this.db.getTimetableSessions(config.params.ids));
    });
    this.api.onGet("/v1/timetable/session/tag").reply(config => {
        return promiseResolve(config, this.db.getTimetableSessionsTags(config.params.ids));
    });
}
//# sourceMappingURL=TimetableApiMock.js.map