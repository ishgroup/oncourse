import { openInternalLink } from "ish-ui";

export const openQualificationLink = (qualificationId: number) => {
  openInternalLink("/qualification/" + qualificationId);
};
