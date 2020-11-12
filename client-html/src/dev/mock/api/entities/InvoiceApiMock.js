import { promiseResolve } from "../../MockAdapter";
export function InvoiceApiMock() {
    this.api.onGet(new RegExp(`v1/list/entity/invoice/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getInvoice(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/invoice/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
    this.api.onPost("v1/list/entity/invoice").reply(config => {
        this.db.createInvoice(config.data);
        return promiseResolve(config, this.db.getInvoices());
    });
    this.api.onDelete(new RegExp(`v1/list/entity/invoice/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        this.db.removeInvoice(id);
        return promiseResolve(config, this.db.getInvoices());
    });
}
//# sourceMappingURL=InvoiceApiMock.js.map