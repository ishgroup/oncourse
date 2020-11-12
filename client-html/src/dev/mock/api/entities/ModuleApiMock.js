import { promiseResolve } from "../../MockAdapter";
export function ModuleApiMock() {
    this.api.onGet(new RegExp(`v1/list/entity/module/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getModule(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/module/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
    this.api.onPost("v1/list/entity/module").reply(config => {
        this.db.createModule(config.data);
        return promiseResolve(config, this.db.getModules());
    });
    this.api.onDelete(new RegExp(`v1/list/entity/module/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        this.db.removeModule(id);
        return promiseResolve(config, this.db.getModules());
    });
}
//# sourceMappingURL=ModuleApiMock.js.map