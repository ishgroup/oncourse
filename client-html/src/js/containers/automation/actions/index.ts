import { Integration } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../common/actions/ActionUtils";

export const GET_INTEGRATIONS_REQUEST = _toRequestType("get/integrations");
export const GET_INTEGRATIONS_FULFILLED = FULFILLED(GET_INTEGRATIONS_REQUEST);

export const CREATE_INTEGRATION_ITEM_REQUEST = _toRequestType("post/integrations");

export const UPDATE_INTEGRATION_ITEM_REQUEST = _toRequestType("put/integrations");
export const UPDATE_INTEGRATION_ITEM_FULFILLED = FULFILLED(UPDATE_INTEGRATION_ITEM_REQUEST);

export const DELETE_INTEGRATION_ITEM_REQUEST = _toRequestType("delete/integrations");

export const GET_MYOB_AUTH_URL_REQUEST = _toRequestType("get/integration/auth/myob");
export const GET_MYOB_AUTH_URL_FULFILLED = FULFILLED(GET_MYOB_AUTH_URL_REQUEST);

export const getIntegrations = () => ({
  type: GET_INTEGRATIONS_REQUEST
});

export const updateIntegration = (id: string, item: Integration) => ({
  type: UPDATE_INTEGRATION_ITEM_REQUEST,
  payload: { id, item }
});

export const createIntegration = (item: Integration) => ({
  type: CREATE_INTEGRATION_ITEM_REQUEST,
  payload: { item }
});

export const deleteIntegrationItem = (id: string) => ({
  type: DELETE_INTEGRATION_ITEM_REQUEST,
  payload: { id }
});

export const getMyobIntegrationAuthUrl = () => ({
  type: GET_MYOB_AUTH_URL_REQUEST
});
