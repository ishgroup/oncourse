import {Injector} from "../../injector";
import {PreferenceApi} from "../../http/PreferenceApi";
import {Preferences} from "../../model";

export class CommonService {
  constructor(private preferenceApi: PreferenceApi) {
  }

  public getPreferences = (): Promise<Preferences> => {
    return this.preferenceApi.getPreferences();
  }
}

const {
  preferenceApi,
} = Injector.of();

export default new CommonService(preferenceApi);