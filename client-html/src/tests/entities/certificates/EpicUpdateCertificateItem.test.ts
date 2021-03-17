import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";
import { EpicUpdateCertificateItem } from "../../../js/containers/entities/certificates/epics/EpicUpdateCertificateItem";
import {
  UPDATE_CERTIFICATE_ITEM_FULFILLED,
  updateCertificate
} from "../../../js/containers/entities/certificates/actions";

describe("Update certificate epic tests", () => {
  it("EpicUpdateCertificateItem should returns correct values", () => DefaultEpic({
    action: mockedApi => updateCertificate("1", mockedApi.db.createNewCertificate(1)),
    epic: EpicUpdateCertificateItem,
    processData: () => [
      {
        type: UPDATE_CERTIFICATE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Certificate Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Certificate", listUpdate: true, savedID: "1" }
      }
    ]
  }));
});
