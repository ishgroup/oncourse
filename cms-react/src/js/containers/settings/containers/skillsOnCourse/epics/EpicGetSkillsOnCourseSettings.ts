import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import {GET_SKILLS_SETTINGS_FULFILLED, GET_SKILLS_SETTINGS_REQUEST} from "../actions";
import SettingsService from "../../../../../services/SettingsService";

const request: EpicUtils.Request<any, any> = {
  type: GET_SKILLS_SETTINGS_REQUEST,
  getData: (payload, state) => SettingsService.getSkillsOnCourseSettings(),
  processData: (skillsOnCourseSettings: any, state: any) => {
    return [
      {
        type: GET_SKILLS_SETTINGS_FULFILLED,
        payload: skillsOnCourseSettings,
      },
    ];
  },
};

export const EpicGetSkillsOnCourseSettings: Epic<any, any> = EpicUtils.Create(request);
