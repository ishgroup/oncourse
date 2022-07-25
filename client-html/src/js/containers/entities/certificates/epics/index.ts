import { combineEpics } from "redux-observable";
import { EpicUpdateCertificateItem } from "./EpicUpdateCertificateItem";
import { EpicGetCertificateOutcomes } from "./EpicGetCertificateOutcomes";
import { EpicRevokeCertificate } from "./EpicRevokeCertificate";
import { EpicValidateUSI } from "./EpicValidateUSI";
import { EpicCheckRevokeStatus } from "./EpicCheckRevokeStatus";

export const EpicCertificate = combineEpics(
  EpicUpdateCertificateItem,
  EpicGetCertificateOutcomes,
  EpicValidateUSI,
  EpicRevokeCertificate,
  EpicCheckRevokeStatus
);
