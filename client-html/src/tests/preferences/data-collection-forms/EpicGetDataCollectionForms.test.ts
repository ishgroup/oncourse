import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_DATA_COLLECTION_FORMS_FULFILLED,
  getDataCollectionForms
} from "../../../js/containers/preferences/actions";
import { EpicGetDataCollectionForms } from "../../../js/containers/preferences/containers/data-collection-forms/epics/EpicGetDataCollectionForms";

describe("Get data collection forms epic tests", () => {
  it("EpicGetDataCollectionForms should returns correct values", () => DefaultEpic({
    action: getDataCollectionForms(),
    epic: EpicGetDataCollectionForms,
    processData: mockedApi => {
      const dataCollectionForms = mockedApi.db.dataCollectionForms;

      return [
        {
          type: GET_DATA_COLLECTION_FORMS_FULFILLED,
          payload: { dataCollectionForms }
        }
      ];
    }
  }));
});
