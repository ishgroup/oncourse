import { promiseResolve } from "../../MockAdapter";
export function MembershipProductApiMock(mock) {
    this.api.onGet(new RegExp(`v1/list/entity/membershipProduct/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        return promiseResolve(config, this.db.getMembershipProduct(id));
    });
    this.api.onPut(new RegExp(`v1/list/entity/membershipProduct/\\d+`)).reply(config => {
        return promiseResolve(config, JSON.parse(config.data));
    });
    this.api.onPost("v1/list/entity/membershipProduct").reply(config => {
        this.db.createMembershipProduct(config.data);
        return promiseResolve(config, this.db.getMembershipProducts());
    });
    this.api.onDelete(new RegExp(`v1/list/entity/membershipProduct/\\d+`)).reply(config => {
        const params = config.url.split("/");
        const id = params[params.length - 1];
        this.db.removeMembershipProduct(id);
        return promiseResolve(config, this.db.getMembershipProducts());
    });
}
//# sourceMappingURL=MembershipProductApiMock.js.map