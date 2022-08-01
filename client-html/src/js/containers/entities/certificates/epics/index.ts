import { combineEpics } from "redux-observable";
import { EpicGetCertificateOutcomes } from "./EpicGetCertificateOutcomes";
import { EpicRevokeCertificate } from "./EpicRevokeCertificate";
import { EpicValidateUSI } from "./EpicValidateUSI";
import { EpicCheckRevokeStatus } from "./EpicCheckRevokeStatus";

export const EpicCertificate = combineEpics(
  EpicGetCertificateOutcomes,
  EpicValidateUSI,
  EpicRevokeCertificate,
  EpicCheckRevokeStatus
);
