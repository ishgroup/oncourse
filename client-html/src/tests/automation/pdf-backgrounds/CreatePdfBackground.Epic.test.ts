import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  createAutomationPdfBackground,
  getAutomationPdfBackgroundsList
} from "../../../js/containers/automation/containers/pdf-backgrounds/actions";
import { EpicCreatePdfBackground } from "../../../js/containers/automation/containers/pdf-backgrounds/epics/EpicCreatePdfBackground";

describe("Create pdf background epic tests", () => {
  it("EpicCreatePdfBackground should returns correct values", () => DefaultEpic({
    action: createAutomationPdfBackground("name 21", {
      name: "name 21",
      lastModified: new Date().getTime(),
      size: 5176,
      type: "image/png"
    } as File),
    epic: EpicCreatePdfBackground,
    processData: () => [
      getAutomationPdfBackgroundsList(false, "name 21"),
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Background was added" }
      }
    ]
  }));
});
