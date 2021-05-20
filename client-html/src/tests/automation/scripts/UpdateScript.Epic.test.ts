import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  GET_SCRIPTS_LIST,
  getScriptItem,
  saveScriptItem,
  UPDATE_SCRIPT_ENTITY_REQUEST_FULFILLED
} from "../../../js/containers/automation/containers/scripts/actions";
import { EpicSaveScriptItem } from "../../../js/containers/automation/containers/scripts/epics/EpicSaveScriptItem";

describe("Update script epic tests", () => {
  it("EpicSaveScriptItem should returns correct values", () => DefaultEpic({
    action: saveScriptItem(1, {
      id: 1,
      name: "script 1",
      enabled: false,
      keyCode: null,
      entity: null,
      description: "test script",
      trigger: {
        type: "Schedule",
        entityName: null,
        cron: {
          scheduleType: "Custom",
          custom: "0 0 3 ? * *"
        }
      },
      content:
        "  def enrolment = args.entity\n"
        + "\n"
        + "   email {\n"
        + '      template "Enrolment Confirmation"\n'
        + "      bindings enrolment: enrolment\n"
        + '      to "blake@acwa.asn.au"\n'
        + '      if (enrolment.courseClass.course.hasTag("ACWA Events", true)==true){\n'
        + '        from ("acwa@acwa.asn.au", "ACWA Events")\n'
        + "      }\n"
        + '      else if (enrolment.courseClass.course.hasTag("Disability Justice Project", true)==true){\n'
        + '        from ("training@disabilityjustice.edu.au", "Disability Justice Project")\n'
        + "      }\n"
        + "    }\n"
        + "    \n"
        + "    args.context.commitChanges()",
      lastRun: [
        "2018-06-04T05:20:48.000Z",
        "2018-06-04T05:14:40.000Z",
        "2018-04-17T07:06:01.000Z",
        "2018-04-17T07:03:48.000Z",
        "2018-04-17T07:00:19.000Z",
        "2018-04-17T06:57:43.000Z",
        "2018-03-16T10:31:37.000Z"
      ],
      createdOn: "2018-01-16T08:51:46.000Z",
      modifiedOn: "2019-10-10T10:00:15.000Z",
      variables: [],
      options: []
    }, "PATCH", "Code"),
    epic: EpicSaveScriptItem,
    processData: () => [
      {
        type: UPDATE_SCRIPT_ENTITY_REQUEST_FULFILLED
      },
      {
        type: GET_SCRIPTS_LIST
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Script was updated" }
      },
      getScriptItem(1)
    ]
  }));
});
