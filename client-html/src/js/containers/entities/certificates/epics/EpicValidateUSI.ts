/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { SET_CERTIFICATES_VALIDATION_STATUS, VALIDATE_CERTIFICATES } from "../actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import CertificateService from "../services/CertificateService";
import { SET_PRINT_VALIDATING_STATUS } from "../../../../common/components/list-view/components/share/actions";
import { CertificateValidationRequest } from "@api/model";

const request: EpicUtils.Request = {
  type: VALIDATE_CERTIFICATES,
  getData: (validationRequest: CertificateValidationRequest) => CertificateService.validateForPrint(validationRequest),
  processData: (validationStatus: string) => {
    return [
      {
        type: SET_CERTIFICATES_VALIDATION_STATUS,
        payload: { validationStatus }
      },
      {
        type: SET_PRINT_VALIDATING_STATUS,
        payload: { validating: false }
      }
    ];
  },

  processError: response => {
    return [
      ...FetchErrorHandler(response),
      {
        type: SET_PRINT_VALIDATING_STATUS,
        payload: { validating: false }
      }
    ];
  }
};

export const EpicValidateUSI: Epic<any, any> = EpicUtils.Create(request);
