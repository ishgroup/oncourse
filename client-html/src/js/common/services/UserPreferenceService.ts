import { DefaultHttpService } from "./HttpService";
import {
  UserPreferenceApi,
  Category,
  UserPreference,
  PreferenceEnum,
  DashboardLinks
} from "@api/model";

class UserPreferenceService {
  readonly userPreferenceApi = new UserPreferenceApi(new DefaultHttpService());

  public getCategories(): Promise<DashboardLinks> {
    return this.userPreferenceApi.getDashboardLinks();
  }

  public setFavoriteCategories(categories: Category[]): Promise<any> {
    return this.userPreferenceApi.setFavorites(categories);
  }

  public getUserPreferencesByKeys(keys: PreferenceEnum[]): Promise<{ [key: string]: string }> {
    return this.userPreferenceApi.get(keys.toString());
  }

  public setUserPreferenceByKey(userPreference: UserPreference): Promise<any> {
    return this.userPreferenceApi.set(userPreference);
  }
}

export default new UserPreferenceService();
