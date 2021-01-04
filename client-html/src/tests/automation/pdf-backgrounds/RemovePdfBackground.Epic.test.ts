import { DefaultEpic } from "../../common/Default.Epic";
import {
  getAutomationPdfBackgroundsList,
  removeAutomationPdfBackground
} from "../../../js/containers/automation/containers/pdf-backgrounds/actions";
import { EpicRemovePdfBackground } from "../../../js/containers/automation/containers/pdf-backgrounds/epics/EpicRemovePdfBackground";

describe("Remove pdf background epic tests", () => {
  it("EpicRemovePdfBackground should returns correct values", () => DefaultEpic({
    action: removeAutomationPdfBackground(1),
    epic: EpicRemovePdfBackground,
    processData: () => [
      getAutomationPdfBackgroundsList(true)
    ]
  }));
});
