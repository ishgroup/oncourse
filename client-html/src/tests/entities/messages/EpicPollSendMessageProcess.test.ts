import { closeSendMessage, FETCH_FAIL, FETCH_SUCCESS, sendMessageFailed } from '../../../js/common/actions';
import ProcessService from '../../../js/common/services/ProcessService';
import { pollSendMessageProcess } from '../../../js/containers/entities/messages/actions';
import { EpicPollSendMessageProcess } from '../../../js/containers/entities/messages/epics/EpicPollSendMessageProcess';
import { DefaultEpic } from '../../common/Default.Epic';

const processId = "send-process";

const currentSendStore = () => ({ sendMessage: { open: true, sending: true, processId } });

describe("Poll send message process epic tests", () => {
  afterEach(() => jest.restoreAllMocks());

  it("should close the dialog when the process is finished", () => {
    jest.spyOn(ProcessService, "getProcessStatus").mockResolvedValue({ status: "Finished" });

    return DefaultEpic({
      action: pollSendMessageProcess(processId),
      epic: EpicPollSendMessageProcess,
      store: currentSendStore,
      processData: () => [
        closeSendMessage(),
        {
          type: FETCH_SUCCESS,
          payload: { message: "All messages sent" }
        }
      ]
    });
  });

  it("should keep polling while the process is in progress", () => {
    jest.spyOn(ProcessService, "getProcessStatus").mockResolvedValue({ status: "In progress" });

    return DefaultEpic({
      action: pollSendMessageProcess(processId),
      epic: EpicPollSendMessageProcess,
      store: currentSendStore,
      processData: () => [pollSendMessageProcess(processId)]
    });
  });

  it("should reset sending state when the process failed", () => {
    jest.spyOn(ProcessService, "getProcessStatus").mockResolvedValue({ status: "Failed", message: "SMTP error" });

    return DefaultEpic({
      action: pollSendMessageProcess(processId),
      epic: EpicPollSendMessageProcess,
      store: currentSendStore,
      processData: () => [
        sendMessageFailed(),
        {
          type: FETCH_FAIL,
          payload: { message: "SMTP error" }
        }
      ]
    });
  });

  it("should reset sending state when the process is not found", () => {
    jest.spyOn(ProcessService, "getProcessStatus").mockResolvedValue({ status: "Not found" });

    return DefaultEpic({
      action: pollSendMessageProcess(processId),
      epic: EpicPollSendMessageProcess,
      store: currentSendStore,
      processData: () => [
        sendMessageFailed(),
        {
          type: FETCH_FAIL,
          payload: { message: "Messages sending failed" }
        }
      ]
    });
  });

  it("should reset sending state when the status request fails", () => {
    jest.spyOn(ProcessService, "getProcessStatus").mockRejectedValue(undefined);

    return DefaultEpic({
      action: pollSendMessageProcess(processId),
      epic: EpicPollSendMessageProcess,
      store: currentSendStore,
      processData: () => [
        sendMessageFailed(),
        {
          type: FETCH_FAIL,
          payload: { message: "Messages sending failed" }
        }
      ]
    });
  });

  it("should stop polling once the dialog is closed", async () => {
    const getStatus = jest.spyOn(ProcessService, "getProcessStatus");

    await DefaultEpic({
      action: pollSendMessageProcess(processId),
      epic: EpicPollSendMessageProcess,
      store: () => ({ sendMessage: { open: false, sending: false, processId: null } }),
      processData: () => []
    });

    expect(getStatus).not.toHaveBeenCalled();
  });

  it("should ignore a process that is not the current send", () => {
    jest.spyOn(ProcessService, "getProcessStatus").mockResolvedValue({ status: "Finished" });

    return DefaultEpic({
      action: pollSendMessageProcess("previous-process"),
      epic: EpicPollSendMessageProcess,
      store: currentSendStore,
      processData: () => []
    });
  });
});
