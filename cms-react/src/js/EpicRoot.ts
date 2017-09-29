import {combineEpics} from "redux-observable";
import {EpicAuth} from "./containers/auth/epics";
import {EpicMenu} from "./containers/menus/epics";
import {EpicPages} from "./containers/pages/epics";
import {EpicBlocks} from "./containers/blocks/epics";
import {EpicThemes} from "./containers/themes/epics";

export const EpicRoot = combineEpics(
  EpicAuth,
  EpicMenu,
  EpicPages,
  EpicBlocks,
  EpicThemes,
);
