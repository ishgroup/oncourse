import {ClassAge} from "../../../../../model/settings/ClassAge";

export class WebsiteSettingsState {
  enableSocialMedia: boolean;
  addThisId: string;
  enableForCourse: boolean;
  enableForWebpage: boolean;
  classAge: ClassAge;
  fetching: boolean = false;
}
