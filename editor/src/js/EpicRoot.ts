import {combineEpics} from "redux-observable";
import {EpicAuth} from "./containers/auth/epics";
import {EpicMenu} from "./containers/content/containers/menus/epics";
import {EpicPages} from "./containers/content/containers/pages/epics";
import {EpicBlocks} from "./containers/content/containers/blocks/epics";
import {EpicThemes} from "./containers/design/containers/themes/epics";
import {EpicHistory} from "./containers/history/epics";
import {EpicSkillsOnCourseSettings} from "./containers/settings/containers/skillsOnCourse/epics";
import {EpicRedirectSettings} from "./containers/settings/containers/redirect/epics";
import {EpicWebsiteSettings} from "./containers/settings/containers/website/epics";
import {EpicCheckoutSettings} from "./containers/settings/containers/checkout/epics";
import {EpicSpecialPages} from "./containers/settings/containers/specialPages/epics";
import { EpicGetFileContents, EpicGetDirectoryContents, EpicPutFileContents } from "./common/webdav/epics";

export const EpicRoot = combineEpics(
  EpicAuth,
  EpicMenu,
  EpicPages,
  EpicBlocks,
  EpicThemes,
  EpicHistory,
  EpicSkillsOnCourseSettings,
  EpicRedirectSettings,
  EpicWebsiteSettings,
  EpicCheckoutSettings,
  EpicSpecialPages,
  EpicGetFileContents,
  EpicPutFileContents,
  EpicGetDirectoryContents,
);
