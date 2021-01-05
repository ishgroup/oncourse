import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  deleteIntegrationItem,
  getIntegrations
} from "../../../js/containers/automation/actions";
import { EpicDeleteIntegration } from "../../../js/containers/automation/containers/integrations/epics/EpicDeleteIntegration";

describe("Remove integration epic tests", () => {
  it("EpicDeleteIntegration should returns correct values", () => DefaultEpic({
    action: deleteIntegrationItem("23"),
    epic: EpicDeleteIntegration,
    processData: () => [
      getIntegrations(),
      {
        type: FETCH_SUCCESS,
        payload: { message: "Integration was successfully deleted" }
      }
    ]
  }));
});
