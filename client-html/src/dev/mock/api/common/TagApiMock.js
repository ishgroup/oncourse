import { promiseResolve } from "../../MockAdapter";
export function TagApiMock(mock) {
    /**
     * List items
     **/
    this.api.onGet("/v1/tag").reply(config => {
        const tags = this.db.getTags();
        return promiseResolve(config, tags);
    });
}
//# sourceMappingURL=TagApiMock.js.map