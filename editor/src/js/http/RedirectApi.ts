import {HttpService} from "../common/services/HttpService";
import {CommonError} from "../model/common/CommonError";
import {RedirectSettings} from "../model/redirect/RedirectSettings";

export class RedirectApi {
  constructor(private http: HttpService) {
  }

  redirectListGet(): Promise<RedirectSettings> {
    return this.http.GET(`/redirect.list`);
  }
  redirectUpdatePost(redirectSettingsRequest: RedirectSettings): Promise<RedirectSettings> {
    return this.http.POST(`/redirect.update`, redirectSettingsRequest);
  }
}
