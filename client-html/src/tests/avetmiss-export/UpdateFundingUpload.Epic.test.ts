import { DefaultEpic } from "../common/Default.Epic";
import {
  UPDATE_FUNDING_UPLOAD_FULFILLED,
  updateFundingUpload
} from "../../js/containers/avetmiss-export/actions";
import { EpicUpdateFundingUpload } from "../../js/containers/avetmiss-export/epics/EpicUpdateFundingUpload";

describe("Update funding upload epic tests", () => {
  it("UpdateFundingUpload should returns correct values", () => DefaultEpic({
    action: updateFundingUpload(1, "success"),
    epic: EpicUpdateFundingUpload,
    processData: () => [
      {
        type: UPDATE_FUNDING_UPLOAD_FULFILLED,
        payload: { id: 1, status: "success" }
      }
    ]
  }));
});
