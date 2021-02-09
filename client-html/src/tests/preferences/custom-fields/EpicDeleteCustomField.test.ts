import { DefaultEpic } from "../../common/Default.Epic";
import {
  DELETE_CUSTOM_FIELD_FULFILLED,
  deleteCustomField
} from "../../../js/containers/preferences/actions";
import { EpicDeleteCustomField } from "../../../js/containers/preferences/containers/custom-fields/epics/EpicDeleteCustomField";
import { FETCH_SUCCESS } from "../../../js/common/actions";

describe("Delete custom field epic tests", () => {
  it("EpicDeleteCustomField should returns correct values", () => DefaultEpic({
    action: deleteCustomField("886543"),
    epic: EpicDeleteCustomField,
    processData: mockedApi => {
      const customFields = mockedApi.db.customFields.filter(it => Number(it.id) !== Number("886543"));

      return [
        {
          type: DELETE_CUSTOM_FIELD_FULFILLED,
          payload: { customFields }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Custom Field was successfully deleted" }
        }
      ];
    }
  }));
});
