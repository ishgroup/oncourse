import { promiseResolve } from "../../MockAdapter";
export function PaymentOutApiMock() {
    this.api.onGet(new RegExp(`v1/list/entity/paymentOut/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getPaymentOut(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/paymentOut/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
    this.api.onPost("v1/list/entity/paymentOut").reply(config => {
        this.db.createPaymentOut(config.data);
        return promiseResolve(config, this.db.getPaymentsOut());
    });
    this.api.onDelete(new RegExp(`v1/list/entity/paymentOut/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        this.db.removePaymentOut(id);
        return promiseResolve(config, this.db.getPaymentsOut());
    });
}
//# sourceMappingURL=PaymentOutApiMock.js.map