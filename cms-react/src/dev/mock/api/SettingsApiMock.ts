import {promiseReject, promiseResolve} from "../MockAdapter";
import {API} from "../../../js/constants/Config";

export function settingsApiMock() {

  // Skills On Course settings
  this.api.onGet(API.GET_SKILLS_ON_COURSE_SETTINGS).reply(config => promiseResolve(
    config,
    this.db.settings.skillsOnCourse,
  ));

  this.api.onPost(API.SET_SKILLS_ON_COURSE_SETTINGS).reply(config => {
    const settings = JSON.parse(config.data);
    this.db.saveSettings(settings, 'skillsOnCourse');

    return promiseResolve(
      config,
      settings,
    );
  });

  // Website settings
  this.api.onGet(API.GET_WEBSITE_SETTINGS).reply(config => promiseResolve(
    config,
    this.db.settings.website,
  ));

  this.api.onPost(API.SET_WEBSITE_SETTINGS).reply(config => {
    const settings = JSON.parse(config.data);
    this.db.saveSettings(settings, 'website');

    return promiseResolve(
      config,
      settings,
    );
  });

  // Checkout settings
  this.api.onGet(API.GET_CHECKOUT_SETTINGS).reply(config => promiseResolve(
    config,
    this.db.settings.checkout,
  ));

  this.api.onPost(API.SET_CHECKOUT_SETTINGS).reply(config => {
    const settings = JSON.parse(config.data);
    this.db.saveSettings(settings, 'checkout');

    return promiseResolve(
      config,
      settings,
    );
  });
}
