import { DefaultEpic } from "../../common/Default.Epic";
import {
  getAutomationPdfBackground,
  getAutomationPdfBackgroundsList,
  updateAutomationPdfBackground
} from "../../../js/containers/automation/containers/pdf-backgrounds/actions";
import { EpicUpdatePdfBackground } from "../../../js/containers/automation/containers/pdf-backgrounds/epics/EpicUpdatePdfBackground";

describe("Update pdf background epic tests", () => {
  it("EpicUpdatePdfBackground should returns correct values", () => DefaultEpic({
    action: updateAutomationPdfBackground("name 1", 1, {
      name: "name 1",
      lastModified: new Date().getTime(),
      size: 5176,
      type: "image/png"
    } as File),
    epic: EpicUpdatePdfBackground,
    processData: () => [
      getAutomationPdfBackgroundsList(),
      getAutomationPdfBackground(1)
    ]
  }));
});
