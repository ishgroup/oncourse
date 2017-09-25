import {combineEpics} from "redux-observable";
import {EpicAuth} from "./containers/auth/epics";
import {EpicMenu} from "./containers/menus/epics";
import {EpicPages} from "./containers/pages/epics/index";
import {EpicBlocks} from "./containers/blocks/epics/index";

export const EpicRoot = combineEpics(
  EpicAuth,
  EpicMenu,
  EpicPages,
  EpicBlocks,
);
