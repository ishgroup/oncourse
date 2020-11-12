import { openInternalLink } from "../../../../common/utils/links";

export const openModuleLink = (moduleId: number) => {
  openInternalLink("/module/" + moduleId);
};
