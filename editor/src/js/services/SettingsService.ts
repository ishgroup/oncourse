import {SettingsApi} from "../http/SettingsApi";
import {RedirectSettings, SkillsOnCourseSettings, WebsiteSettings} from "../model";
import {DefaultHttpService} from "../common/services/HttpService";
import {RedirectApi} from "../http/RedirectApi";

class SettingsService {
  readonly settingsApi = new SettingsApi(new DefaultHttpService());
  readonly redirectApi = new RedirectApi(new DefaultHttpService());


  public getSkillsOnCourseSettings(): Promise<SkillsOnCourseSettings> {
    return this.settingsApi.settingsSkillsOnCourseGetGet();
  }

  public setSkillsOnCourseSettings(settings: SkillsOnCourseSettings): Promise<SkillsOnCourseSettings> {
    return this.settingsApi.settingsSkillsOnCourseSetPost(settings);
  }

  public getRedirectSettings(): Promise<RedirectSettings> {
    return this.redirectApi.redirectListGet();
  }

  public setRedirectSettings(settings: RedirectSettings): Promise<any> {
    return this.redirectApi.redirectUpdatePost(settings);
  }

  public getWebsiteSettings(): Promise<WebsiteSettings> {
    return this.settingsApi.settingsWebsiteGetGet();
  }

  public setWebsiteSettings(settings: WebsiteSettings): Promise<WebsiteSettings> {
    return this.settingsApi.settingsWebsiteSetPost(settings);
  }

}

export default new SettingsService;
