import {ClassAge, State} from "../../../../../model";

export class WebsiteSettingsState {
  enableSocialMedia: boolean;
  addThisId: string;
  enableForCourse: boolean;
  enableForWebpage: boolean;
  classAge: ClassAge = {};
  suburbAutocompleteState?: State;
  refreshSettings?: boolean = false;
}
