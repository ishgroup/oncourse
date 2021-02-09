import { DefaultEpic } from "../../common/Default.Epic";
import {
  UPDATE_CUSTOM_FIELDS_FULFILLED,
  updateCustomFields
} from "../../../js/containers/preferences/actions";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { EpicUpdateCustomFields } from "../../../js/containers/preferences/containers/custom-fields/epics/EpicUpdateCustomFields";

describe("Update custom field epic tests", () => {
  it("EpicUpdateCustomFields should returns correct values", () => DefaultEpic({
    action: mockedApi => updateCustomFields(mockedApi.db.customFields),
    epic: EpicUpdateCustomFields,
    processData: mockedApi => {
      const customFields = mockedApi.db.customFields;

      return [
        {
          type: UPDATE_CUSTOM_FIELDS_FULFILLED,
          payload: { customFields }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Custom Fields were successfully updated" }
        }
      ];
    }
  }));
});
