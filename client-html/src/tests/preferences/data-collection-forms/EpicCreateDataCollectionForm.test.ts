import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  CREATE_DATA_COLLECTION_FORM_FULFILLED,
  createDataCollectionForm
} from "../../../js/containers/preferences/actions";
import { EpicCreateDataCollectionForm } from "../../../js/containers/preferences/containers/data-collection-forms/epics/EpicCreateDataCollectionForm";

describe("Create data collection form epic tests", () => {
  it("EpicCreateDataCollectionForm should returns correct values", () => DefaultEpic({
    action: mockedApi => createDataCollectionForm(mockedApi.db.dataCollectionForms[0]),
    epic: EpicCreateDataCollectionForm,
    processData: mockedApi => {
      const dataCollectionForms = mockedApi.db.dataCollectionForms;

      return [
        {
          type: CREATE_DATA_COLLECTION_FORM_FULFILLED,
          payload: { dataCollectionForms }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "New form was successfully created" }
        }
      ];
    }
  }));
});
