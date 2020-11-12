import { promiseResolve } from "../../MockAdapter";
export function MessageApiMock(mock) {
    this.api.onGet(new RegExp(`v1/list/entity/message/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getMessage(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/message/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
    this.api.onDelete(new RegExp(`v1/list/entity/message/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        this.db.removeMessage(id);
        return promiseResolve(config, this.db.getMessages());
    });
}
//# sourceMappingURL=MessageApiMock.js.map