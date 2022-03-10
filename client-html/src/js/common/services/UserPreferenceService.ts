import {
  UserPreferenceApi,
  UserPreference,
  PreferenceEnum
} from "@api/model";
import { DefaultHttpService } from "./HttpService";

class UserPreferenceService {
  readonly userPreferenceApi = new UserPreferenceApi(new DefaultHttpService());

  public getUserPreferencesByKeys(keys: PreferenceEnum[]): Promise<{ [key: string]: string }> {
    return this.userPreferenceApi.get(keys.toString());
  }

  public setUserPreferenceByKey(userPreference: UserPreference): Promise<any> {
    return this.userPreferenceApi.set(userPreference);
  }
}

export default new UserPreferenceService();
