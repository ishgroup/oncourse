import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import UserPreferenceService from "../../../common/services/UserPreferenceService";
import { FAVORITE_SCRIPTS_KEY } from "../../../constants/Config";
import { GET_FAVORITE_SCRIPTS, GET_FAVORITE_SCRIPTS_FULFILLED } from "../actions";

const request: EpicUtils.Request = {
  type: GET_FAVORITE_SCRIPTS,
  getData: () => UserPreferenceService.getUserPreferencesByKeys(
    [FAVORITE_SCRIPTS_KEY]
  ),
  processData: v => {
    const key = v[FAVORITE_SCRIPTS_KEY];
    const favoriteScripts = key ? (key.split(",")).map(k => Number(k)) : [];
    return [
      {
        type: GET_FAVORITE_SCRIPTS_FULFILLED,
        payload: { favoriteScripts }
      }
    ];
  }
};

export const EpicGetFavoriteScripts: Epic<any, any> = EpicUtils.Create(request);
