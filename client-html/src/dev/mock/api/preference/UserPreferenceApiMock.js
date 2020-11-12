import { promiseResolve } from "../../MockAdapter";
export function UserPreferenceApiMock() {
    /**
     * Preferences
     **/
    this.api.onGet("/v1/user/preference").reply(config => {
        const keys = config.params.keys.split(",");
        return promiseResolve(config, this.db.getUserPreferences(keys));
    });
    this.api.onGet("/v1/preference/currency").reply(config => {
        return promiseResolve(config, this.db.currency);
    });
}
//# sourceMappingURL=UserPreferenceApiMock.js.map