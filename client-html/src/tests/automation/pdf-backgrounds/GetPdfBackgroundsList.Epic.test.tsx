import React from "react";
import CropPortraitIcon from "@mui/icons-material/CropPortrait";
import CropLandscapeIcon from "@mui/icons-material/CropLandscape";
import { mockedAPI } from "../../TestEntry";
import { DefaultEpic } from "../../common/Default.Epic";
import {
  GET_AUTOMATION_PDF_BACKGROUNDS_LIST_FULFILLED,
  getAutomationPdfBackgroundsList
} from "../../../js/containers/automation/containers/pdf-backgrounds/actions";
import { EpicGetPdfBackgroundsList } from "../../../js/containers/automation/containers/pdf-backgrounds/epics/EpicGetPdfBackgroundsList";
import { CatalogItemType } from "../../../js/model/common/Catalog";

describe("Get pdf backgrounds list epic tests", () => {
  it("EpicGetPdfBackgroundsList should returns correct values", () => DefaultEpic({
    action: getAutomationPdfBackgroundsList(),
    epic: EpicGetPdfBackgroundsList,
    processData: () => {
      const pdfBackgroundsResponse = mockedAPI.db.getReportOverlays();

      const pdfBackgrounds: CatalogItemType[] = pdfBackgroundsResponse.rows.map(r => ({
        id: Number(r.id),
        title: r.values[0],
        installed: true,
        enabled: true,
        hideShortDescription: true,
        titleAdornment: r.values[1] === "true" ? <CropPortraitIcon className="lightGrayIconButton" /> : <CropLandscapeIcon className="lightGrayIconButton" />
      }));

      pdfBackgrounds.sort((a, b) => (a.title.toLowerCase() > b.title.toLowerCase() ? 1 : -1));

      return [
        {
          type: GET_AUTOMATION_PDF_BACKGROUNDS_LIST_FULFILLED,
          payload: { pdfBackgrounds }
        }
      ];
    }
  }));
});
