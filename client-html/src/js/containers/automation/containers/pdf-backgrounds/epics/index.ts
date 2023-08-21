import { combineEpics } from "redux-observable";
import { EpicCreatePdfBackground } from "./EpicCreatePdfBackground";
import { EpicGetPdfBackground } from "./EpicGetPdfBackground";
import { EpicGetPdfBackgroundCopy } from "./EpicGetPdfBackgroundCopy";
import { EpicGetPdfBackgroundsList } from "./EpicGetPdfBackgroundsList";
import { EpicRemovePdfBackground } from "./EpicRemovePdfBackground";
import { EpicUpdatePdfBackground } from "./EpicUpdatePdfBackground";

export const EpicPdfBackgrounds = combineEpics(
  EpicUpdatePdfBackground,
  EpicGetPdfBackgroundsList,
  EpicCreatePdfBackground,
  EpicGetPdfBackground,
  EpicRemovePdfBackground,
  EpicGetPdfBackgroundCopy
);
