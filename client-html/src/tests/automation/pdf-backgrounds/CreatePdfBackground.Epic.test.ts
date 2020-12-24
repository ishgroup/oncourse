import { DefaultEpic } from "../../common/Default.Epic";
import { FETCH_SUCCESS } from "../../../js/common/actions";
import {
  CREATE_AUTOMATION_PDF_BACKGROUND,
  getAutomationPdfBackgroundsList
} from "../../../js/containers/automation/containers/pdf-backgrounds/actions";
import { EpicCreatePdfBackground } from "../../../js/containers/automation/containers/pdf-backgrounds/epics/EpicCreatePdfBackground";

describe("Create pdf background epic tests", () => {
  it("EpicCreatePdfBackground should returns correct values", () =>
    DefaultEpic({
      action: {
        type: CREATE_AUTOMATION_PDF_BACKGROUND,
        payload: {
          fileName: "name 21",
          overlay: {
            name: "name 21",
            lastModified: new Date().getTime(),
            lastModifiedDate: new Date(),
            size: 5176,
            type: "image/png",
            webkitRelativePath: ""
          }
        }
      },
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
