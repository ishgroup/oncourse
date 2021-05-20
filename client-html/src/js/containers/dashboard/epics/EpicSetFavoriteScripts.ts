import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import UserPreferenceService from "../../../common/services/UserPreferenceService";
import { FAVORITE_SCRIPTS_KEY } from "../../../constants/Config";
import { SET_FAVORITE_SCRIPTS, SET_FAVORITE_SCRIPTS_FULFILLED } from "../actions";

const request: EpicUtils.Request = {
  type: SET_FAVORITE_SCRIPTS,
  getData: (scripts: number[]) => UserPreferenceService.setUserPreferenceByKey(
    { key: FAVORITE_SCRIPTS_KEY, value: scripts.join(',') }
  ),
  processData: () => [
    { type: SET_FAVORITE_SCRIPTS_FULFILLED }
  ]
};

export const EpicSetFavoriteScripts: Epic<any, any> = EpicUtils.Create(request);
