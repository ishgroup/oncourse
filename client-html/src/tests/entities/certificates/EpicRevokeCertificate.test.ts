import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_CERTIFICATE_ITEM,
  revokeCertificate
} from "../../../js/containers/entities/certificates/actions";
import { EpicRevokeCertificate } from "../../../js/containers/entities/certificates/epics/EpicRevokeCertificate";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { GET_RECORDS_REQUEST } from "../../../js/common/components/list-view/actions";

describe("Revoke certificate epic tests", () => {
  it("EpicRevokeCertificate should returns correct values", () => DefaultEpic({
    action: revokeCertificate([1], "test"),
    epic: EpicRevokeCertificate,
    processData: () => {
      const ids = [1];
      return [
        {
          type: FETCH_SUCCESS,
          payload: { message: "Certificate revoked successfully" }
        },
        {
          type: GET_RECORDS_REQUEST,
          payload: { entity: "Certificate", listUpdate: true, savedID: ids[0] }
        },
        {
          type: GET_CERTIFICATE_ITEM,
          payload: ids[0]
        }
      ];
    }
  }));
});
