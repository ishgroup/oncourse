import { ThirdPartyState } from '../model/ThirdParty';
import { toRequestType } from '../utils/ActionUtils';

export const GET_CLIENT_IDS = toRequestType('get/clientIds');
export const SET_CLIENT_IDS = 'set/clientIds';

export const getClientIds = () => ({
  type: GET_CLIENT_IDS
});

export const setClientIds = (ids: ThirdPartyState) => ({
  type: SET_CLIENT_IDS,
  payload: ids
});
