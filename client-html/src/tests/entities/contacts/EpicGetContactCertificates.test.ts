import { DefaultEpic } from "../../common/Default.Epic";
import {
  contactCertificatesMap,
  EpicGetContactCertificates
} from "../../../js/containers/entities/contacts/epics/EpicGetContactCertificates";
import {
  GET_CONTACT_CERTIFICATES_FULFILLED,
  getContactCertificates
} from "../../../js/containers/entities/contacts/actions";

describe("Get contact certificates epic tests", () => {
  it("EpicGetContactCertificates should returns correct values", () => DefaultEpic({
    action: getContactCertificates(1),
    epic: EpicGetContactCertificates,
    processData: mockedApi => {
      const response = mockedApi.db.getPlainCertificates();
      const certificates = response.rows.map(contactCertificatesMap);

      return [
        {
          type: GET_CONTACT_CERTIFICATES_FULFILLED,
          payload: { certificates }
        }
      ];
    }
  }));
});
