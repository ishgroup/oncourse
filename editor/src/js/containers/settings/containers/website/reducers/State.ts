import {ClassAge} from "../../../../../model";

export class WebsiteSettingsState {
  enableSocialMedia: boolean;
  addThisId: string;
  enableForCourse: boolean;
  enableForWebpage: boolean;
  classAge: ClassAge = {};
  refreshSettings?: boolean = false;
}
