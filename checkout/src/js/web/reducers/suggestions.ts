import { SuggestionsState } from '../../services/IshState';
import { IshAction } from '../../actions/IshAction';
import { FULFILLED } from '../../common/actions/ActionUtils';
import { Actions } from '../actions/Actions';

export const suggestionsReducer = (state:SuggestionsState = {}, action: IshAction<SuggestionsState>):SuggestionsState => {
  switch (action.type) {
    case FULFILLED(Actions.REQUEST_SUGGESTION):
      return {
        ...state,
        ...action.payload
      };
    default:
      return state;
  }
};
