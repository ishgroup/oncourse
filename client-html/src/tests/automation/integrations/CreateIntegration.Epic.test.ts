import { IntegrationProp } from "@api/model";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { createIntegration, getIntegrations } from "../../../js/containers/automation/actions";
import { EpicCreateIntegration } from "../../../js/containers/automation/containers/integrations/epics/EpicCreateIntegration";

describe("Create integration epic tests", () => {
  it("EpicCreateIntegration should returns correct values", () => DefaultEpic({
    action: createIntegration({
      type: 1,
      id: "",
      name: "Test Moodle Integration",
      props: [
        { key: "baseUrl", value: "http://localhost:8888/moodle29/" } as IntegrationProp,
        { key: "username", value: "admin" } as IntegrationProp,
        { key: "password", value: "Consulrisk_12" } as IntegrationProp,
        { key: "serviceName", value: "newint" } as IntegrationProp,
        { key: "courseTag", value: "Moodle" } as IntegrationProp
      ]
    }),
    epic: EpicCreateIntegration,
    processData: () => [
      getIntegrations(),
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Integration was successfully created" }
      }
    ]
  }));
});
