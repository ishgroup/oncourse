/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Certificate, CertificateValidationRequest } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_CERTIFICATE_ITEM = _toRequestType("get/certificate");
export const GET_CERTIFICATE_ITEM_FULFILLED = FULFILLED(GET_CERTIFICATE_ITEM);

export const GET_CERTIFICATE_OUTCOMES = _toRequestType("get/certificate/outcome");
export const GET_CERTIFICATE_OUTCOMES_FULFILLED = FULFILLED(GET_CERTIFICATE_OUTCOMES);

export const DELETE_CERTIFICATE_ITEM = _toRequestType("delete/certificate");
export const DELETE_CERTIFICATE_ITEM_FULFILLED = FULFILLED(DELETE_CERTIFICATE_ITEM);

export const UPDATE_CERTIFICATE_ITEM = _toRequestType("put/certificate");
export const UPDATE_CERTIFICATE_ITEM_FULFILLED = FULFILLED(UPDATE_CERTIFICATE_ITEM);

export const CREATE_CERTIFICATE_ITEM = _toRequestType("post/certificate");
export const CREATE_CERTIFICATE_ITEM_FULFILLED = FULFILLED(CREATE_CERTIFICATE_ITEM);

export const VALIDATE_CERTIFICATES = _toRequestType("post/validateCertificates");
export const SET_CERTIFICATES_VALIDATION_STATUS = "set/certificate/print/validation";

export const REVOKE_CERTIFICATE_ITEM = _toRequestType("post/certificate/revoke");

export const GET_CERTIFICATES_REVOKE_STATUS = _toRequestType("get/certificate/revokeStatus");
export const SET_CERTIFICATES_REVOKE_STATUS = "set/certificate/revokeStatus";

export const SET_CERTIFICATE_OUTCOMES_SEARCH = "set/certificate/outcome/search";

export const CLEAR_CERTIFICATE_OUTCOMES = "clear/certificate/outcome";

export const validateCertificates = (validationRequest: CertificateValidationRequest) => ({
  type: VALIDATE_CERTIFICATES,
  payload: validationRequest
});

export const setCertificatesValidationStatus = (status: string) => ({
  type: SET_CERTIFICATES_VALIDATION_STATUS,
  payload: status
});

export const getCertificatesRevokeStatus = (ids: number[]) => ({
  type: GET_CERTIFICATES_REVOKE_STATUS,
  payload: ids
});

export const setCertificatesRevokeStatus = (hasRevoked: boolean) => ({
  type: SET_CERTIFICATES_REVOKE_STATUS,
  payload: { hasRevoked }
});

export const revokeCertificate = (ids: number[], reason: string) => ({
  type: REVOKE_CERTIFICATE_ITEM,
  payload: { ids, reason }
});

export const getCertificate = (id: string) => ({
  type: GET_CERTIFICATE_ITEM,
  payload: id
});

export const getCertificateOutcomes = (studentId: number) => ({
  type: GET_CERTIFICATE_OUTCOMES,
  payload: studentId
});

export const clearCertificateOutcomes = (loading?: boolean) => ({
  type: CLEAR_CERTIFICATE_OUTCOMES,
  payload: { items: [], search: "", loading }
});

export const setCertificateOutcomesSearch = (search: string) => ({
  type: SET_CERTIFICATE_OUTCOMES_SEARCH,
  payload: { search }
});

export const removeCertificate = (id: string) => ({
  type: DELETE_CERTIFICATE_ITEM,
  payload: id
});

export const updateCertificate = (id: string, certificate: Certificate) => ({
  type: UPDATE_CERTIFICATE_ITEM,
  payload: { id, certificate }
});

export const createCertificate = (certificate: Certificate) => ({
  type: CREATE_CERTIFICATE_ITEM,
  payload: { certificate }
});
