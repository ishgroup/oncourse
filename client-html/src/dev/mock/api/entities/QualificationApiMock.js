import { promiseResolve } from "../../MockAdapter";
export function QualificationApiMock() {
    this.api.onGet(new RegExp(`v1/list/entity/qualification/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getQualification(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/qualification/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
    this.api.onPost("v1/list/entity/qualification").reply(config => {
        this.db.createQualification(config.data);
        return promiseResolve(config, this.db.getQualifications());
    });
    this.api.onDelete(new RegExp(`v1/list/entity/qualification/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        this.db.removeQualification(id);
        return promiseResolve(config, this.db.getQualifications());
    });
}
//# sourceMappingURL=QualificationApiMock.js.map