import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import { closeListNestedEditRecord } from "../../../js/common/components/list-view/actions";
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
    }),
    epic: EpicSendMessage,
    processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "All messages sent" }
      },
      closeListNestedEditRecord(0)
    ]
  }));
});
