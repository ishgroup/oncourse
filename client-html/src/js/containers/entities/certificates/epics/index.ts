import { combineEpics } from "redux-observable";
import { EpicGetCertificate } from "./EpicGetCertificate";
import { EpicUpdateCertificateItem } from "./EpicUpdateCertificateItem";
import { EpicDeleteCertificate } from "./EpicDeleteCertificate";
import { EpicGetCertificateOutcomes } from "./EpicGetCertificateOutcomes";
import { EpicRevokeCertificate } from "./EpicRevokeCertificate";
import { EpicValidateUSI } from "./EpicValidateUSI";
import { EpicCheckRevokeStatus } from "./EpicCheckRevokeStatus";

export const EpicCertificate = combineEpics(
  EpicGetCertificate,
  EpicUpdateCertificateItem,
  EpicDeleteCertificate,
  EpicGetCertificateOutcomes,
  EpicValidateUSI,
  EpicRevokeCertificate,
  EpicCheckRevokeStatus
);
