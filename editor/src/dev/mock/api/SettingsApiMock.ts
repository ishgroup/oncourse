import {promiseReject, promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function settingsApiMock() {

  // Skills On Course settings
  this.api.onGet(API.SKILLS_ON_COURSE).reply(config => promiseResolve(
    config,
    this.db.settings.skillsOnCourse,
  ));

  this.api.onPut(API.SKILLS_ON_COURSE).reply(config => {
    const settings = JSON.parse(config.data);
    this.db.saveSettings(settings, 'skillsOnCourse');

    return promiseResolve(
      config,
      settings,
    );
  });

  // Website settings
  this.api.onGet(API.WEBSITE).reply(config => promiseResolve(
    config,
    this.db.settings.website,
  ));

  this.api.onPut(API.WEBSITE).reply(config => {
    const settings = JSON.parse(config.data);
    this.db.saveSettings(settings, 'website');

    return promiseResolve(
      config,
      settings,
    );
  });

  // Redirect settings
  this.api.onGet(API.REDIRECT).reply(config => promiseResolve(
    config,
    this.db.settings.redirect,
  ));

  this.api.onPut(API.REDIRECT).reply(config => {
    const settings = JSON.parse(config.data);
    this.db.saveSettings(settings, 'redirect');

    // if (1) { // test reject
    //   const rules = settings.rules.map(rule => ({...rule, error: "This url already redirected"}));
    //   settings.rules = rules;
    //
    //   return promiseReject(
    //     config,
    //     settings,
    //   );
    // }

    return promiseResolve(
      config,
      settings,
    );
  });

  // Checkout settings
  this.api.onGet(API.CHECKOUT).reply(config => promiseResolve(
    config,
    this.db.settings.checkout,
  ));

  this.api.onPut(API.CHECKOUT).reply(config => {
    const settings = JSON.parse(config.data);
    this.db.saveSettings(settings, 'checkout');

    return promiseResolve(
      config,
      settings,
    );
  });

}
