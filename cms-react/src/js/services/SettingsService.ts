import {SettingsApi} from "../http/SettingsApi";
import {CheckoutSettings} from "../model/settings/CheckoutSettings";
import {SkillsOnCourseSettings} from "../model/settings/SkillsOnCourseSettings";
import {RedirectSettings} from "../model/settings/RedirectSettings";
import {WebsiteSettings} from "../model/settings/WebsiteSettings";

class SettingsService {
  readonly settingsApi = new SettingsApi();

  public getCheckoutSettings(): Promise<any> {
    return this.settingsApi.getCheckoutSettings();
  }

  public setCheckoutSettings(settings: CheckoutSettings): Promise<CheckoutSettings> {
    return this.settingsApi.setCheckoutSettings(settings);
  }

  public getSkillsOnCourseSettings(): Promise<CheckoutSettings> {
    return this.settingsApi.getSkillsOnCourseSettings();
  }

  public setSkillsOnCourseSettings(settings: SkillsOnCourseSettings): Promise<SkillsOnCourseSettings> {
    return this.settingsApi.setSkillsOnCourseSettings(settings);
  }

  public getRedirectSettings(): Promise<SkillsOnCourseSettings> {
    return this.settingsApi.getRedirectSettings();
  }

  public setRedirectSettings(settings: RedirectSettings): Promise<any> {
    return this.settingsApi.setRedirectSettings(settings);
  }

  public getWebsiteSettings(): Promise<WebsiteSettings> {
    return this.settingsApi.getWebsiteSettings();
  }

  public setWebsiteSettings(settings: WebsiteSettings): Promise<WebsiteSettings> {
    return this.settingsApi.setWebsiteSettings(settings);
  }

}

export default new SettingsService;
