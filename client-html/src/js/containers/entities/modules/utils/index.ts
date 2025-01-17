import { openInternalLink } from "ish-ui";

export const openModuleLink = (moduleId: number) => {
  openInternalLink("/module/" + moduleId);
};
