import { setSendMessageProcess } from '../../../js/common/actions';
import {
  interruptSendMessage,
  pollSendMessageProcess,
  sendMessage
} from '../../../js/containers/entities/messages/actions';
import { EpicSendMessage } from '../../../js/containers/entities/messages/epics/EpicSendMessage';
import { DefaultEpic } from '../../common/Default.Epic';

describe("Send message epic tests", () => {
  it("EpicSendMessage should start polling the send process", () => DefaultEpic({
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
    store: () => ({ sendMessage: { open: true, sending: true, processId: null } }),
    processData: () => [
      setSendMessageProcess("testing 123"),
      pollSendMessageProcess("testing 123")
    ]
  }));

  it("EpicSendMessage should interrupt the process if the dialog was closed before the request returned", () => DefaultEpic({
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
    store: () => ({ sendMessage: { open: false, sending: false, processId: null } }),
    processData: () => [
      interruptSendMessage("testing 123")
    ]
  }));
});
