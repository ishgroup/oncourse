import {HttpService} from "../common/services/HttpService";
import {CommonError} from "../model/common/CommonError";
import {RedirectSettings} from "../model/settings/RedirectSettings";
import {SkillsOnCourseSettings} from "../model/settings/SkillsOnCourseSettings";
import {WebsiteSettings} from "../model/settings/WebsiteSettings";

export class SettingsApi {
  constructor(private http: HttpService) {
  }

  getRedirectSettings(): Promise<RedirectSettings> {
    return this.http.GET(`/getRedirectSettings`);
  }
  getSkillsOnCourseSettings(): Promise<SkillsOnCourseSettings> {
    return this.http.GET(`/getSkillsOnCourseSettings`);
  }
  getWebsiteSettings(): Promise<WebsiteSettings> {
    return this.http.GET(`/getWebsiteSettings`);
  }
  setRedirectSettings(redirectSettingsRequest: RedirectSettings): Promise<RedirectSettings> {
    return this.http.POST(`/setRedirectSettings`, redirectSettingsRequest);
  }
  setSkillsOnCourseSettings(skillsOnCourseSettingsRequest: SkillsOnCourseSettings): Promise<SkillsOnCourseSettings> {
    return this.http.POST(`/setSkillsOnCourseSettings`, skillsOnCourseSettingsRequest);
  }
  setWebsiteSettings(websiteSettingsRequest: WebsiteSettings): Promise<WebsiteSettings> {
    return this.http.POST(`/setWebsiteSettings`, websiteSettingsRequest);
  }
}
