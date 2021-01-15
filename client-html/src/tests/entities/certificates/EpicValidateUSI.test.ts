import { DefaultEpic } from "../../common/Default.Epic";
import {
  SET_CERTIFICATES_VALIDATION_STATUS,
  validateCertificates
} from "../../../js/containers/entities/certificates/actions";
import { EpicValidateUSI } from "../../../js/containers/entities/certificates/epics/EpicValidateUSI";
import { SET_PRINT_VALIDATING_STATUS } from "../../../js/common/components/list-view/components/share/actions";

describe("Validate certificate USI epic tests", () => {
  it("EpicValidateUSI should returns correct values", () => DefaultEpic({
    action: mockedApi => validateCertificates(mockedApi.db.validateCertificateUSIRequest()),
    epic: EpicValidateUSI,
    processData: mockedApi => [
      {
        type: SET_CERTIFICATES_VALIDATION_STATUS,
        payload: { validationStatus: mockedApi.db.validateCertificateStatus() }
      },
      {
        type: SET_PRINT_VALIDATING_STATUS,
        payload: { validating: false }
      }
    ]
  }));
});
