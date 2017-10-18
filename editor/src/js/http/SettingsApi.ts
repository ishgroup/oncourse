import {DefaultHttpService} from "../common/services/HttpService";
import {API} from "../constants/Config";

export class SettingsApi {
  private http = new DefaultHttpService();

  getCheckoutSettings(): Promise<any> {
    return this.http.GET(API.GET_CHECKOUT_SETTINGS);
  }

  setCheckoutSettings(payload): Promise<any> {
    return this.http.POST(API.SET_CHECKOUT_SETTINGS, payload);
  }

  getSkillsOnCourseSettings(): Promise<any> {
    return this.http.GET(API.GET_SKILLS_ON_COURSE_SETTINGS);
  }

  setSkillsOnCourseSettings(payload): Promise<any> {
    return this.http.POST(API.SET_SKILLS_ON_COURSE_SETTINGS, payload);
  }

  getRedirectSettings(): Promise<any> {
    return this.http.GET(API.GET_REDIRECT_SETTINGS);
  }

  setRedirectSettings(payload): Promise<any> {
    return this.http.POST(API.SET_REDIRECT_SETTINGS, payload);
  }

  getWebsiteSettings(): Promise<any> {
    return this.http.GET(API.GET_WEBSITE_SETTINGS);
  }

  setWebsiteSettings(payload): Promise<any> {
    return this.http.POST(API.SET_WEBSITE_SETTINGS, payload);
  }

}
