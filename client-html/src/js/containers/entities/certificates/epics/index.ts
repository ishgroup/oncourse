import { combineEpics } from "redux-observable";
import { EpicGetCertificate } from "./EpicGetCertificate";
import { EpicUpdateCertificateItem } from "./EpicUpdateCertificateItem";
import { EpicGetCertificateOutcomes } from "./EpicGetCertificateOutcomes";
import { EpicRevokeCertificate } from "./EpicRevokeCertificate";
import { EpicValidateUSI } from "./EpicValidateUSI";
import { EpicCheckRevokeStatus } from "./EpicCheckRevokeStatus";

export const EpicCertificate = combineEpics(
  EpicGetCertificate,
  EpicUpdateCertificateItem,
  EpicGetCertificateOutcomes,
  EpicValidateUSI,
  EpicRevokeCertificate,
  EpicCheckRevokeStatus
);
