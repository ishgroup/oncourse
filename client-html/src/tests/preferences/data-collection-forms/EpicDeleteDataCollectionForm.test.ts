import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  DELETE_DATA_COLLECTION_FORM_FULFILLED,
  deleteDataCollectionForm
} from "../../../js/containers/preferences/actions";
import { EpicDeleteDataCollectionForm } from "../../../js/containers/preferences/containers/data-collection-forms/epics/EpicDeleteDataCollectionForm";

const id = "test.form.1";

describe("Delete data collection form epic tests", () => {
  it("EpicDeleteDataCollectionForm should returns correct values", () => DefaultEpic({
    action: deleteDataCollectionForm(id),
    epic: EpicDeleteDataCollectionForm,
    processData: mockedApi => {
      const dataCollectionForms = mockedApi.db.dataCollectionForms.filter(form => String(form.id) !== String(id));

      return [
        {
          type: DELETE_DATA_COLLECTION_FORM_FULFILLED,
          payload: { dataCollectionForms }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Form was successfully deleted" }
        }
      ];
    }
  }));
});
