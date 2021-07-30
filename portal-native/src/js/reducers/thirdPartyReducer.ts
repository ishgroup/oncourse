import { IAction } from '../model/IshAction';
import { ThirdPartyState } from '../model/ThirdParty';
import { SET_CLIENT_IDS } from '../actions/ThirdPartyActions';

const initial: ThirdPartyState = {};

export default (state = initial, action: IAction): ThirdPartyState => {
  switch (action.type) {
    case SET_CLIENT_IDS:
      return {
        ...state,
        ...action.payload
      };

    default:
      return state;
  }
};
