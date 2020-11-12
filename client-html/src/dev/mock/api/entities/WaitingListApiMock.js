import { promiseResolve } from "../../MockAdapter";
export function WaitingListApiMock(mock) {
    /**
     * List items
     **/
    this.api.onGet(new RegExp(`v1/list/entity/waitingList/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getWaitingList(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/waitingList/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
}
//# sourceMappingURL=WaitingListApiMock.js.map