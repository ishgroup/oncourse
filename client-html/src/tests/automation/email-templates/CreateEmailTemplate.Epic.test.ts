import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  CREATE_EMAIL_TEMPLATE,
  CREATE_EMAIL_TEMPLATE_FULFILLED,
  GET_EMAIL_TEMPLATES_LIST
} from "../../../js/containers/automation/containers/email-templates/actions";
import { EpicCreateEmailTemplate } from "../../../js/containers/automation/containers/email-templates/epics/EpicCreateEmailTemplate";

describe("Create email template epic tests", () => {
  it("EpicCreateEmailTemplate should returns correct values", () =>
    DefaultEpic({
      action: {
        type: CREATE_EMAIL_TEMPLATE,
        payload: {
          emailTemplate: {
            id: 21,
            name: "name 21",
            keyCode: "keyCode 21",
            enabled: true
          }
        }
      },
      epic: EpicCreateEmailTemplate,
      processData: () => [
        {
          type: CREATE_EMAIL_TEMPLATE_FULFILLED
        },
        {
          type: GET_EMAIL_TEMPLATES_LIST,
          payload: { keyCodeToSelect: "keyCode 21" }
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Message template created" }
        }
      ]
    }));
});
