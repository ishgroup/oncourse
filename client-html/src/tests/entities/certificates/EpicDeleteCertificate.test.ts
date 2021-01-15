import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicDeleteCertificate } from "../../../js/containers/entities/certificates/epics/EpicDeleteCertificate";
import {
  DELETE_CERTIFICATE_ITEM_FULFILLED,
  removeCertificate
} from "../../../js/containers/entities/certificates/actions";

describe("Delete certificate epic tests", () => {
  it("EpicDeleteCertificate should returns correct values", () => DefaultEpic({
    action: removeCertificate("1"),
    epic: EpicDeleteCertificate,
    processData: () => [
      {
        type: DELETE_CERTIFICATE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Certificate record deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Certificate", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ]
  }));
});
