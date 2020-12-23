import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_EMAIL_TEMPLATE,
  GET_EMAIL_TEMPLATES_LIST,
  UPDATE_EMAIL_TEMPLATE,
  UPDATE_EMAIL_TEMPLATE_FULFILLED
} from "../../../js/containers/automation/containers/email-templates/actions";
import { EpicUpdateEmailTemplate } from "../../../js/containers/automation/containers/email-templates/epics/EpicUpdateEmailTemplate";

describe("Update email template epic tests", () => {
  it("EpicUpdateEmailTemplate should returns correct values", () =>
    DefaultEpic({
      action: {
        type: UPDATE_EMAIL_TEMPLATE,
        payload: {
          emailTemplate: {
            id: 1,
            name: "name 1",
            keyCode: "keyCode 1",
            enabled: false
          }
        }
      },
      epic: EpicUpdateEmailTemplate,
      processData: () => [
        {
          type: UPDATE_EMAIL_TEMPLATE_FULFILLED
        },
        {
          type: GET_EMAIL_TEMPLATE,
          payload: 1
        },
        {
          type: GET_EMAIL_TEMPLATES_LIST
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Message template updated" }
        }
      ]
    }));
});
