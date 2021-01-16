import { DefaultEpic } from "../../common/Default.Epic";
import { VERIFY_USI_FULFILLED, verifyUSI } from "../../../js/containers/entities/contacts/actions";
import { EpicVerifyUSI } from "../../../js/containers/entities/contacts/epics/EpicVerifyUSI";

describe("Verify USI epic tests", () => {
  it("EpicVerifyUSI should returns correct values", () => DefaultEpic({
    action: verifyUSI("test", "test", "2001-01-01", "4123456782"),
    epic: EpicVerifyUSI,
    processData: mockedApi => {
      const usiVerificationResult = mockedApi.db.getVerifyUSI();

      return [
        {
          type: VERIFY_USI_FULFILLED,
          payload: { usiVerificationResult }
        }
      ];
    }
  }));
});
