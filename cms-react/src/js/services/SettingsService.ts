import {SettingsApi} from "../http/SettingsApi";

class SettingsService {
  readonly settingsApi = new SettingsApi();

  public getCheckoutSettings(): Promise<any> {
    return this.settingsApi.getCheckoutSettings();
  }

  public setCheckoutSettings(settings: any): Promise<any> {
    return this.settingsApi.setCheckoutSettings(settings);
  }

  public getSkillsOnCourseSettings(): Promise<any> {
    return this.settingsApi.getSkillsOnCourseSettings();
  }

  public setSkillsOnCourseSettings(settings: any): Promise<any> {
    return this.settingsApi.setSkillsOnCourseSettings(settings);
  }

  public getRedirectSettings(): Promise<any> {
    return this.settingsApi.getRedirectSettings();
  }

  public setRedirectSettings(settings: any): Promise<any> {
    return this.settingsApi.setRedirectSettings(settings);
  }

  public getWebsiteSettings(): Promise<any> {
    return this.settingsApi.getWebsiteSettings();
  }

  public setWebsiteSettings(settings: any): Promise<any> {
    return this.settingsApi.setWebsiteSettings(settings);
  }

}

export default new SettingsService;
