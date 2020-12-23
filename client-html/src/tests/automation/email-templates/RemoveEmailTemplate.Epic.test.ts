import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_EMAIL_TEMPLATES_LIST,
  REMOVE_EMAIL_TEMPLATE,
  REMOVE_EMAIL_TEMPLATE_FULFILLED
} from "../../../js/containers/automation/containers/email-templates/actions";
import { EpicRemoveEmailTemplate } from "../../../js/containers/automation/containers/email-templates/epics/EpicRemoveEmailTemplate";

describe("Remove email template epic tests", () => {
  it("EpicRemoveEmailTemplate should returns correct values", () =>
    DefaultEpic({
      action: {
        type: REMOVE_EMAIL_TEMPLATE,
        payload: 1
      },
      epic: EpicRemoveEmailTemplate,
      processData: () => [
        {
          type: REMOVE_EMAIL_TEMPLATE_FULFILLED
        },
        {
          type: GET_EMAIL_TEMPLATES_LIST
        },
        {
          type: FETCH_SUCCESS,
          payload: { message: "Message template deleted" }
        }
      ]
    }));
});
