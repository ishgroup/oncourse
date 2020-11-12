import { openInternalLink } from "../../../../common/utils/links";

export const openQualificationLink = (qualificationId: number) => {
  openInternalLink("/qualification/" + qualificationId);
};
