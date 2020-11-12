import { promiseResolve } from "../../MockAdapter";
export function SalesApiMock(mock) {
    this.api.onGet(new RegExp(`v1/list/entity/sales/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getProductItem(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/sales/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
    this.api.onPost("v1/list/entity/sales").reply(config => {
        this.db.createProductItem(config.data);
        return promiseResolve(config, this.db.getProductItems());
    });
    this.api.onDelete(new RegExp(`v1/list/entity/sales/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        this.db.removeProductItem(id);
        return promiseResolve(config, this.db.getProductItems());
    });
}
//# sourceMappingURL=SalesApiMock.js.map