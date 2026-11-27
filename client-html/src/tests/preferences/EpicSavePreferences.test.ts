import { DefaultEpic } from "../common/Default.Epic";
import {
  SAVE_PREFERENCES_FULFILLED,
  savePreferences
} from "../../js/containers/preferences/actions";
import { Categories } from "../../js/model/preferences";
import * as ModelAssistant from "../../js/model/preferences/Assistant";
import * as ModelLicences from "../../js/model/preferences/Licences";
import { EpicSavePreferences } from "../../js/containers/preferences/epics/EpicSavePreferences";
import { FETCH_SUCCESS, getUserPreferences } from "../../js/common/actions";
import { ASSISTANT_AVAILABLE_KEY } from "../../js/constants/Config";

describe("Save preferences epic tests", () => {
  it("EpicSavePreferences should returns correct values", () => DefaultEpic({
    action: savePreferences(Categories.licences, { "license.access_control": true, "license.budget": true }),
    epic: EpicSavePreferences,
    processData: mockedApi => {
      const preferences = mockedApi.db.getPreferences(Object.keys(ModelLicences).map(item => ModelLicences[item].uniqueKey));
      return [
        {
          type: SAVE_PREFERENCES_FULFILLED,
          payload: { preferences, category: Categories.licences }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Form was successfully saved" }
        }
      ];
    }
  }));

  it("EpicSavePreferences should refresh the assistant availability after saving assistant settings", () => DefaultEpic({
    action: savePreferences(Categories.assistant, { [ModelAssistant.AssistantEnabled.uniqueKey]: "false" }),
    epic: EpicSavePreferences,
    processData: mockedApi => {
      const preferences = mockedApi.db.getPreferences(Object.keys(ModelAssistant).map(item => ModelAssistant[item].uniqueKey));
      return [
        {
          type: SAVE_PREFERENCES_FULFILLED,
          payload: { preferences, category: Categories.assistant }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Form was successfully saved" }
        },
        getUserPreferences([ASSISTANT_AVAILABLE_KEY])
      ];
    }
  }));
});
