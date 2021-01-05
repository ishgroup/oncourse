import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_INTEGRATIONS_FULFILLED,
  getIntegrations
} from "../../../js/containers/automation/actions";
import { EpicGetIntegrations } from "../../../js/containers/automation/containers/integrations/epics/EpicGetIntegrations";
import { parseIntegrations } from "../../../js/containers/automation/containers/integrations/utils";

describe("Get integrations epic tests", () => {
  it("EpicGetIntegrations should returns correct values", () => DefaultEpic({
    action: getIntegrations(),
    epic: EpicGetIntegrations,
    processData: mockedAPI => {
      const integrations = parseIntegrations(mockedAPI.db.getIntegrations());

      return [
        {
          type: GET_INTEGRATIONS_FULFILLED,
          payload: { integrations }
        }
      ];
    }
  }));
});
