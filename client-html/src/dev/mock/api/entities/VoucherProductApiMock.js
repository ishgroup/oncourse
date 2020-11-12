import { promiseResolve } from "../../MockAdapter";
export function VoucherProductApiMock(mock) {
    this.api.onGet(new RegExp(`v1/list/entity/voucherProduct/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getVoucherProduct(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/voucherProduct/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
    this.api.onPost("v1/list/entity/voucherProduct").reply(config => {
        this.db.createVoucherProduct(config.data);
        return promiseResolve(config, this.db.getVoucherProducts());
    });
    this.api.onDelete(new RegExp(`v1/list/entity/voucherProduct/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        this.db.removeVoucherProduct(id);
        return promiseResolve(config, this.db.getVoucherProducts());
    });
}
//# sourceMappingURL=VoucherProductApiMock.js.map