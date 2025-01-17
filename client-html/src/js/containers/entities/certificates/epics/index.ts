import { combineEpics } from "redux-observable";
import { EpicCheckRevokeStatus } from "./EpicCheckRevokeStatus";
import { EpicGetCertificateOutcomes } from "./EpicGetCertificateOutcomes";
import { EpicRevokeCertificate } from "./EpicRevokeCertificate";
import { EpicValidateUSI } from "./EpicValidateUSI";

export const EpicCertificate = combineEpics(
  EpicGetCertificateOutcomes,
  EpicValidateUSI,
  EpicRevokeCertificate,
  EpicCheckRevokeStatus
);
