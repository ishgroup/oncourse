import { promiseResolve } from "../../MockAdapter";
export function filterApiMock(mock) {
    /**
     * List items
     **/
    this.api.onGet("/v1/filter").reply(config => {
        const { entity } = config.params;
        const filters = this.db.getFilters(entity);
        return promiseResolve(config, filters);
    });
}
//# sourceMappingURL=FilterApiMock.js.map