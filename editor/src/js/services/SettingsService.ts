import {
  SettingsApi,
  RedirectApi,
  SpecialPageApi,
  Redirects,
  SkillsOnCourseSettings,
  WebsiteSettings,
  CheckoutSettings,
  SpecialPages,
} from "../model";
import {DefaultHttpService} from "../common/services/HttpService";

class SettingsService {
  readonly settingsApi = new SettingsApi(new DefaultHttpService());
  readonly redirectApi = new RedirectApi(new DefaultHttpService());
  readonly specialPageApi = new SpecialPageApi(new DefaultHttpService());

  public getSkillsOnCourseSettings(): Promise<SkillsOnCourseSettings> {
    return this.settingsApi.getSkillsOnCourseSettings();
  }

  public setSkillsOnCourseSettings(
    settings: SkillsOnCourseSettings,
  ): Promise<SkillsOnCourseSettings> {
    return this.settingsApi.updateSkillsOnCourseSettings(settings);
  }

  public getRedirectSettings(): Promise<Redirects> {
    return this.redirectApi.getRedirects();
  }

  public setRedirectSettings(settings: Redirects): Promise<any> {
    return this.redirectApi.updateRedirects(settings);
  }

  public getSpecialPages(): Promise<SpecialPages> {
    return this.specialPageApi.getSpecialPages();
  }

  public updateSpecialPages(
    specialPages: SpecialPages,
  ): Promise<SpecialPages> {
    return this.specialPageApi.updateSpecialPages(specialPages);
  }

  public getWebsiteSettings(): Promise<WebsiteSettings> {
    return this.settingsApi.getWebsiteSettings();
  }

  public setWebsiteSettings(
    settings: WebsiteSettings,
  ): Promise<WebsiteSettings> {
    return this.settingsApi.updateWebsiteSettings(settings);
  }

  public getCheckoutSettings(): Promise<CheckoutSettings> {
    return this.settingsApi.getCheckoutSettings();
  }

  public setCheckoutSettings(
    settings: CheckoutSettings,
  ): Promise<CheckoutSettings> {
    return this.settingsApi.updateCheckoutSettings(settings);
  }
}

export default new SettingsService();
