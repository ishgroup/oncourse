import {HttpService} from "../common/services/HttpService";
import {CommonError} from "../model/common/CommonError";
import {SkillsOnCourseSettings} from "../model/settings/SkillsOnCourseSettings";
import {WebsiteSettings} from "../model/settings/WebsiteSettings";

export class SettingsApi {
  constructor(private http: HttpService) {
  }

  settingsSkillsOnCourseGetGet(): Promise<SkillsOnCourseSettings> {
    return this.http.GET(`/settings.skillsOnCourse.get`);
  }
  settingsSkillsOnCourseSetPost(skillsOnCourseSettingsRequest: SkillsOnCourseSettings): Promise<SkillsOnCourseSettings> {
    return this.http.POST(`/settings.skillsOnCourse.set`, skillsOnCourseSettingsRequest);
  }
  settingsWebsiteGetGet(): Promise<WebsiteSettings> {
    return this.http.GET(`/settings.website.get`);
  }
  settingsWebsiteSetPost(websiteSettingsRequest: WebsiteSettings): Promise<WebsiteSettings> {
    return this.http.POST(`/settings.website.set`, websiteSettingsRequest);
  }
}
