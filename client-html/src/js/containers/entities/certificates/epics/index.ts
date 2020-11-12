import { combineEpics } from "redux-observable";
import { EpicGetCertificate } from "./EpicGetCertificate";
import { EpicUpdateCertificateItem } from "./EpicUpdateCertificateItem";
import { EpicCreateCertificate } from "./EpicCreateCertificate";
import { EpicDeleteCertificate } from "./EpicDeleteCertificate";
import { EpicGetCertificateOutcomes } from "./EpicGetCertificateOutcomes";
import { EpicRevokeCertificate } from "./EpicRevokeCertificate";
import { EpicValidateUSI } from "./EpicValidateUSI";
import { EpicCheckRevokeStatus } from "./EpicCheckRevokeStatus";

export const EpicCertificate = combineEpics(
  EpicGetCertificate,
  EpicUpdateCertificateItem,
  EpicCreateCertificate,
  EpicDeleteCertificate,
  EpicGetCertificateOutcomes,
  EpicValidateUSI,
  EpicRevokeCertificate,
  EpicCheckRevokeStatus
);
