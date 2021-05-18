import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {SET_SKILLS_SETTINGS_REQUEST, SET_SKILLS_SETTINGS_FULFILLED} from "../actions";
import SettingsService from "../../../../../services/SettingsService";
import {SHOW_MESSAGE} from "../../../../../common/components/message/actions";

const request: EpicUtils.Request<any, any> = {
  type: SET_SKILLS_SETTINGS_REQUEST,
  getData: (payload, state) => SettingsService.setSkillsOnCourseSettings(payload),
  processData: (skillsOnCourseSettings: any, state: any) => {
    return [
      {
        type: SHOW_MESSAGE,
        payload: {message: "Save success", success: true},
      },
      {
        type: SET_SKILLS_SETTINGS_FULFILLED,
        payload: skillsOnCourseSettings,
      },
    ];
  },
};

export const EpicSetSkillsOnCourseSettings: Epic<any, any> = EpicUtils.Create(request);
