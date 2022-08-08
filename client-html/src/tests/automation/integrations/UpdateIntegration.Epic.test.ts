import { IntegrationProp } from "@api/model";
import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  updateIntegration,
  getIntegrations
} from "../../../js/containers/automation/actions";
import {
  parseIntegrations
} from "../../../js/containers/automation/containers/integrations/utils";
import { EpicUpdateIntegration } from "../../../js/containers/automation/containers/integrations/epics/EpicUpdateIntegration";
import { FETCH_SUCCESS } from "../../../js/common/actions";

const integration = {
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
};

describe("Update integration epic tests", () => {
  it("EpicUpdateIntegration should returns correct values", () => DefaultEpic({
    action: updateIntegration("23", integration, "MoodleForm"),
    epic: EpicUpdateIntegration,
    processData: () => [
        initialize("MoodleForm", parseIntegrations([integration])[0]),
        getIntegrations(),
        { type: FETCH_SUCCESS, payload: { message: "Integration was successfully updated" } }
      ]
  }));
});