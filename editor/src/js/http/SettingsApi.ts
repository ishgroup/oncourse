import {HttpService} from "../common/services/HttpService";
import {CommonError} from "../model/common/CommonError";
import {RedirectSettings} from "../model/redirect/RedirectSettings";
import {SkillsOnCourseSettings} from "../model/settings/SkillsOnCourseSettings";
import {WebsiteSettings} from "../model/settings/WebsiteSettings";

export class SettingsApi {
  constructor(private http: HttpService) {
  }

  getRedirectSettings(): Promise<RedirectSettings> {
    return this.http.GET(`/redirect.list`);
  }
  getSkillsOnCourseSettings(): Promise<SkillsOnCourseSettings> {
    return this.http.GET(`/settings.skillsOnCourse.get`);
  }
  getWebsiteSettings(): Promise<WebsiteSettings> {
    return this.http.GET(`/settings.website.get`);
  }
  setRedirectSettings(redirectSettingsRequest: RedirectSettings): Promise<RedirectSettings> {
    return this.http.POST(`/redirect.update`, redirectSettingsRequest);
  }
  setSkillsOnCourseSettings(skillsOnCourseSettingsRequest: SkillsOnCourseSettings): Promise<SkillsOnCourseSettings> {
    return this.http.POST(`/settings.skillsOnCourse.set`, skillsOnCourseSettingsRequest);
  }
  setWebsiteSettings(websiteSettingsRequest: WebsiteSettings): Promise<WebsiteSettings> {
    return this.http.POST(`/settings.website.set`, websiteSettingsRequest);
  }
}
