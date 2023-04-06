import { CertificateOutcome } from "@api/model";
import { IAction } from "../../../../common/actions/IshAction";
import { PlainEntityState } from "../../../../model/common/Plain";
import {
  CLEAR_CERTIFICATE_OUTCOMES,
  GET_CERTIFICATE_OUTCOMES,
  GET_CERTIFICATE_OUTCOMES_FULFILLED,
  GET_CERTIFICATE_OUTCOMES_REJECTED,
  GET_CERTIFICATES_REVOKE_STATUS,
  SET_CERTIFICATE_OUTCOMES_SEARCH,
  SET_CERTIFICATES_REVOKE_STATUS,
  SET_CERTIFICATES_VALIDATION_STATUS,
  VALIDATE_CERTIFICATES
} from "../actions";

export interface CertificatesState {
  outcomes: PlainEntityState<CertificateOutcome>;
  validationStatus: string;
  checkingRevokeStatus: boolean;
  hasRevoked: boolean;
}

const initial: CertificatesState = {
  outcomes: {
    items: [],
    search: "",
    loading: false,
    error: false
  },
  validationStatus: null,
  checkingRevokeStatus: false,
  hasRevoked: false,
};

export const certificatesReducer = (state: CertificatesState = initial, action: IAction<any>): CertificatesState => {
  switch (action.type) {
    case GET_CERTIFICATE_OUTCOMES: {
      return {
        ...state,
        outcomes: {
          ...state.outcomes,
          error: false,
          loading: true
        }
      };
    }

    case GET_CERTIFICATE_OUTCOMES_REJECTED: {
      return {
        ...state,
        outcomes: {
          ...state.outcomes,
          error: true,
          loading: false
        }
      };
    }

    case SET_CERTIFICATE_OUTCOMES_SEARCH:
    case CLEAR_CERTIFICATE_OUTCOMES:
    case GET_CERTIFICATE_OUTCOMES_FULFILLED: {
      return {
        ...state,
        outcomes: {
          ...state.outcomes,
          ...action.payload,
          error: false
        }
      };
    }
    case VALIDATE_CERTIFICATES: {
      return {
        ...state,
        validationStatus: null
      };
    }

    case GET_CERTIFICATES_REVOKE_STATUS:
      return {
        ...state,
        ...action.payload,
        checkingRevokeStatus: true
      };

    case SET_CERTIFICATES_REVOKE_STATUS:
      return {
        ...state,
        ...action.payload,
        checkingRevokeStatus: false
      };

    case SET_CERTIFICATES_VALIDATION_STATUS: {
      return {
        ...state,
        ...action.payload
      };
    }

    default:
      return state;
  }
};
