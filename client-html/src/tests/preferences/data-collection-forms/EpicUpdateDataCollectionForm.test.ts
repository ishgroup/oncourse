import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_DATA_COLLECTION_RULES_REQUEST,
  UPDATE_DATA_COLLECTION_FORM_FULFILLED,
  updateDataCollectionForm
} from "../../../js/containers/preferences/actions";
import { EpicUpdateDataCollectionForm } from "../../../js/containers/preferences/containers/data-collection-forms/epics/EpicUpdateDataCollectionForm";
import { FETCH_SUCCESS } from "../../../js/common/actions";

describe("Update data collection form epic tests", () => {
  it("EpicUpdateDataCollectionForm should returns correct values", () => DefaultEpic({
    action: mockedApi => updateDataCollectionForm("test.form.1", mockedApi.db.dataCollectionForms[0]),
    epic: EpicUpdateDataCollectionForm,
    processData: mockedApi => {
      const dataCollectionForms = mockedApi.db.dataCollectionForms;

      return [
        {
          type: UPDATE_DATA_COLLECTION_FORM_FULFILLED,
          payload: { dataCollectionForms }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Form was successfully updated" }
        },
        {
          type: GET_DATA_COLLECTION_RULES_REQUEST
        }
      ];
    }
  }));
});
