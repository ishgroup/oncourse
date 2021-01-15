import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicCreateCertificate } from "../../../js/containers/entities/certificates/epics/EpicCreateCertificate";
import {
  CREATE_CERTIFICATE_ITEM_FULFILLED,
  createCertificate
} from "../../../js/containers/entities/certificates/actions";

describe("Create certificate epic tests", () => {
  it("EpicCreateCertificate should returns correct values", () => DefaultEpic({
    action: mockedApi => createCertificate(mockedApi.db.createNewCertificate()),
    epic: EpicCreateCertificate,
    processData: () => [
      {
        type: CREATE_CERTIFICATE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Certificate Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Certificate" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
