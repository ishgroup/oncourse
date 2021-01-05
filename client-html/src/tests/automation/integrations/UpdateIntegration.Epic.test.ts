import { IntegrationProp } from "@api/model";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  UPDATE_INTEGRATION_ITEM_FULFILLED,
  updateIntegration
} from "../../../js/containers/automation/actions";
import { EpicUpdateIntegration } from "../../../js/containers/automation/containers/integrations/epics/EpicUpdateIntegration";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { parseIntegrations } from "../../../js/containers/automation/containers/integrations/utils";

describe("Update integration epic tests", () => {
  it("EpicUpdateIntegration should returns correct values", () => DefaultEpic({
    action: updateIntegration("23", {
      type: 1,
      id: "123",
      name: "Moodle Integration",
      props: [
        { key: "baseUrl", value: "http://localhost:8888/moodle29/" } as IntegrationProp,
        { key: "username", value: "admin" } as IntegrationProp,
        { key: "password", value: "Consulrisk_12" } as IntegrationProp,
        { key: "serviceName", value: "newint" } as IntegrationProp,
        { key: "courseTag", value: "Moodle" } as IntegrationProp
      ],
      verificationCode: null,
      created: new Date().toISOString(),
      modified: new Date().toISOString()
    }),
    epic: EpicUpdateIntegration,
    processData: mockedAPI => {
      const integrations = parseIntegrations(mockedAPI.db.getIntegrations());
      return [
        { type: UPDATE_INTEGRATION_ITEM_FULFILLED, payload: { integrations } },
        { type: FETCH_SUCCESS, payload: { message: "Integration was successfully updated" } }
      ];
    }
  }));
});
