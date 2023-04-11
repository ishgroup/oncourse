import { initialize } from "redux-form";
import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  createAutomationPdfBackground,
  getAutomationPdfBackgroundsList
} from "../../../js/containers/automation/containers/pdf-backgrounds/actions";
import {
  PDF_BACKGROUND_FORM_NAME
} from "../../../js/containers/automation/containers/pdf-backgrounds/PdfBackgrounds";
import { EpicCreatePdfBackground } from "../../../js/containers/automation/containers/pdf-backgrounds/epics/EpicCreatePdfBackground";

const overlay = {
  name: "name 21",
  lastModified: new Date().getTime(),
  size: 5176,
  type: "image/png"
} as File;

describe("Create pdf background epic tests", () => {
  it("EpicCreatePdfBackground should returns correct values", () => DefaultEpic({
    action: createAutomationPdfBackground("name 21", overlay),
    epic: EpicCreatePdfBackground,
    processData: () => [
      initialize(PDF_BACKGROUND_FORM_NAME, overlay),
      getAutomationPdfBackgroundsList(false, "name 21"),
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Background was added" }
      }
    ]
  }));
});
