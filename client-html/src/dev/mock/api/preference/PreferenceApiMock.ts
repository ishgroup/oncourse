import { promiseResolve } from "../../MockAdapter";

export function preferenceApiMock() {
  /**
   * Preferences
   * */
  this.api.onGet("/v1/preference").reply(config => {
    const query = config.params.search;

    const keys = query.split(",");

    return promiseResolve(config, this.db.getPreferences(keys));
  });
  /**
   * Preferences columns
   * */
  this.api.onGet("/v1/preference/column").reply(config => promiseResolve(config, this.db.columnsSettings));

  /**
   * Mock Preferences save success
   * */
  this.api.onPost("/v1/preference").reply(config => {
    const data = JSON.parse(config.data);
    this.db.savePreferences(data);

    return promiseResolve(config, this.db.getPreferences(data.map(item => item[Object.keys(item)[0]])));
  });

  /**
   * Accounts
   * */
  this.api.onGet("v1/preference/account").reply(config => promiseResolve(config, this.db.getAccounts()));
  /**
   * Timezones
   * */

  this.api.onGet("v1/preference/timezone").reply(config => promiseResolve(config, this.db.timezones));

  /**
   * Countries
   * */
  this.api.onGet("/v1/preference/country").reply(config => promiseResolve(config, this.db.countries));

  /**
   * Languages
   * */
  this.api.onGet("/v1/preference/language").reply(config => promiseResolve(config, this.db.getLanguages()));

  this.api.onGet("v1/preference/messagequeued").reply(config => {
    if (config.params.type === "sms") {
      return promiseResolve(config, Math.floor(Math.random() * 100));
    }

    if (config.params.type === "email") {
      return promiseResolve(config, Math.floor(Math.random() * 100));
    }
    return promiseResolve(config, 666);
  });

  this.api.onGet("/v1/preference/lockedDate").reply(config => promiseResolve(config, this.db.preferencesLockedDate()));
}
