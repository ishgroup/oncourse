import { PreferenceEnum } from "@api/model";
import { mockedAPI } from "../TestEntry";
import {
  GET_USER_PREFERENCES_FULFILLED,
  getUserPreferences
} from "../../js/common/actions";
import { EpicGetUserPreferences } from "../../js/common/epics/EpicGetUserPreferences";
import { DefaultEpic } from "./Default.Epic";

export const GetUserPreferences = (keys: PreferenceEnum[]) => DefaultEpic({
  action: getUserPreferences(keys),
  epic: EpicGetUserPreferences,
  processData: () => {
    const preferences = mockedAPI.db.getUserPreferences(keys);

    return [
      {
        type: GET_USER_PREFERENCES_FULFILLED,
        payload: { preferences }
      }
    ];
  }
});
