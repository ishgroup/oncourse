import {SettingsApi} from "../http/SettingsApi";
import {SkillsOnCourseSettings} from "../model/settings/SkillsOnCourseSettings";
import {RedirectSettings} from "../model/settings/RedirectSettings";
import {WebsiteSettings} from "../model/settings/WebsiteSettings";
import {DefaultHttpService} from "../common/services/HttpService";

class SettingsService {
  readonly settingsApi = new SettingsApi(new DefaultHttpService());
  

  public getSkillsOnCourseSettings(): Promise<SkillsOnCourseSettings> {
    return this.settingsApi.getSkillsOnCourseSettings();
  }

  public setSkillsOnCourseSettings(settings: SkillsOnCourseSettings): Promise<SkillsOnCourseSettings> {
    return this.settingsApi.setSkillsOnCourseSettings(settings);
  }

  public getRedirectSettings(): Promise<RedirectSettings> {
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
