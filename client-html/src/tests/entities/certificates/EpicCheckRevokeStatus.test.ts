import { DefaultEpic } from "../../common/Default.Epic";
import { EpicCheckRevokeStatus } from "../../../js/containers/entities/certificates/epics/EpicCheckRevokeStatus";
import {
  getCertificatesRevokeStatus,
  SET_CERTIFICATES_REVOKE_STATUS
} from "../../../js/containers/entities/certificates/actions";

describe("Check revoke status epic tests", () => {
  it("EpicCheckRevokeStatus should returns correct values", () => DefaultEpic({
    action: getCertificatesRevokeStatus([1]),
    epic: EpicCheckRevokeStatus,
    processData: mockedApi => {
      const certificates = mockedApi.db.getPlainCertificates({ columns: "revokedOn" });
      const hasRevoked = certificates.rows.some(r => r.values[0]);

      return [
        {
          type: SET_CERTIFICATES_REVOKE_STATUS,
          payload: { hasRevoked }
        }
      ];
    }
  }));
});
