import { promiseResolve } from "../../MockAdapter";
export function RoomApiMock(mock) {
    this.api.onGet(new RegExp(`v1/list/entity/room/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getRoom(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/room/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
}
//# sourceMappingURL=RoomApiMock.js.map