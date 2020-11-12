import { promiseResolve } from "../../MockAdapter";
export function AccountApiMock(mock) {
    this.api.onGet(new RegExp(`v1/list/entity/account/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getAccount(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/account/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
    this.api.onPost("/v1/list/entity/account").reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
    this.api.onGet(new RegExp(`v1/list/entity/account/depositAccounts`)).reply(config => {
        return promiseResolve(config, this.db.getAccounts());
    });
}
//# sourceMappingURL=AccountApiMock.js.map