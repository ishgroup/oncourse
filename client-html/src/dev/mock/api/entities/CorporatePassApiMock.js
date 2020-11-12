import { promiseResolve } from "../../MockAdapter";
export function CorporatePassApiMock(mock) {
    this.api.onGet(new RegExp(`v1/list/entity/corporatepass/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getCorporatePass(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/corporatepass/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
    this.api.onPost("v1/list/entity/corporatepass").reply(config => {
        this.db.createCorporatePass(config.data);
        return promiseResolve(config, this.db.getCorporatePasses());
    });
}
//# sourceMappingURL=CorporatePassApiMock.js.map