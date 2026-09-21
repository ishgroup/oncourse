import { DefaultEpic } from "../../common/Default.Epic";
import { closeSendMessage, FETCH_SUCCESS, SEND_MESSAGE_FAILED, START_PROCESS, UPDATE_PROCESS } from "../../../js/common/actions";
import { sendMessage } from "../../../js/containers/entities/messages/actions";
import { EpicSendMessage } from "../../../js/containers/entities/messages/epics/EpicSendMessage";

describe("Send message epic tests", () => {
  it("EpicSendMessage should returns correct values", () => DefaultEpic({
    action: sendMessage({
      sendToStudents: true,
      sendToTutors: true,
      sendToOtherContacts: true,
      sendToSuppressStudents: false,
      sendToSuppressTutors: false,
      sendToSuppressOtherContacts: false,
      entity: "Enrolment",
      sendToWithdrawnStudents: false,
      sendToActiveStudents: true,
      sendToSuppressWithdrawnStudents: false,
      sendToSuppressActiveStudents: false,
      templateId: 1,
      fromAddress: "training@ish.com.au",
      searchQuery: {
        search: "id in (1)",
        pageSize: 20,
        offset: 0,
        filter: "",
        tagGroups: []
      },
      variables: {
        subjectTxt: "test",
        body: "test 123"
      },
      messageType: "Sms",
      recipientsCount: 1,
      selectAll: true
    },
    ["2", "3"]),
    epic: EpicSendMessage,
    processData: () => [
      {
        type: UPDATE_PROCESS,
        payload: { processId: "testing 123" }
      },
      {
        type: START_PROCESS,
        payload: {
          processId: "testing 123",
          actions: [
            closeSendMessage(),
            {
              type: FETCH_SUCCESS,
              payload: { message: "All messages sent" }
            }
          ],
          actionsOnFail: [
            { type: SEND_MESSAGE_FAILED }
          ]
        }
      }
    ]
  }));
});
