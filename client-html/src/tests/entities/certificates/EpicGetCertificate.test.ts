import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { SET_LIST_EDIT_RECORD } from "../../../js/common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../js/common/components/list-view/constants";
import { EpicGetCertificate } from "../../../js/containers/entities/certificates/epics/EpicGetCertificate";
import { GET_CERTIFICATE_ITEM_FULFILLED, getCertificate } from "../../../js/containers/entities/certificates/actions";

describe("Get certificate epic tests", () => {
  it("EpicGetCertificate should returns correct values", () => DefaultEpic({
    action: getCertificate("1"),
    epic: EpicGetCertificate,
    processData: mockedApi => {
      const certificate = mockedApi.db.getCertificate(1);
      return [
        {
          type: GET_CERTIFICATE_ITEM_FULFILLED,
          payload: { certificate }
        },
        {
          type: SET_LIST_EDIT_RECORD,
          payload: { editRecord: certificate, name: certificate.studentName }
        },
        initialize(LIST_EDIT_VIEW_FORM_NAME, certificate)
      ];
    }
  }));
});
