import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_CUSTOM_FIELDS_FULFILLED,
  getCustomFields
} from "../../../js/containers/preferences/actions";
import { EpicGetCustomFields } from "../../../js/containers/preferences/containers/custom-fields/epics/EpicGetCustomFields";

describe("Get custom fields epic tests", () => {
  it("EpicGetCustomFields should returns correct values", () => DefaultEpic({
    action: getCustomFields(),
    epic: EpicGetCustomFields,
    processData: mockedApi => {
      const customFields = mockedApi.db.customFields;

      return [
        {
          type: GET_CUSTOM_FIELDS_FULFILLED,
          payload: { customFields }
        },
        initialize("CustomFieldsForm", { types: customFields })
      ];
    }
  }));
});
