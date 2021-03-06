import { combineEpics } from "redux-observable";
import { EpicGetPdfBackgroundsList } from "./EpicGetPdfBackgroundsList";
import { EpicCreatePdfBackground } from "./EpicCreatePdfBackground";
import { EpicGetPdfBackground } from "./EpicGetPdfBackground";
import { EpicRemovePdfBackground } from "./EpicRemovePdfBackground";
import { EpicUpdatePdfBackground } from "./EpicUpdatePdfBackground";

export const EpicPdfBackgrounds = combineEpics(
  EpicUpdatePdfBackground,
  EpicGetPdfBackgroundsList,
  EpicCreatePdfBackground,
  EpicGetPdfBackground,
  EpicRemovePdfBackground
);
