import {HttpService} from "../common/services/HttpService";
import {CommonError} from "../model/common/CommonError";
import {Preferences} from "../model/common/Preferences";

export class PreferenceApi {
  constructor(private http: HttpService) {
  }

  getPreferences(): Promise<Preferences> {
    return this.http.GET(`/getPreferences`);
  }
}
