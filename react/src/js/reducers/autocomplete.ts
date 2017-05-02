import {IshAction} from "../actions/IshAction";
import {FULFILLED, REJECTED, IshActions} from "../constants/IshActions";
import {AutocompleteResponsePayload} from "../actions/actions";
import {AutocompleteState} from "../services/IshState";

export function autocompleteReducer(
  state: AutocompleteState = {},
  action: IshAction<AutocompleteResponsePayload>
): AutocompleteState {
  switch (action.type) {
    case IshActions.AUTOCOMPLETE_CLEAR:
      return emptyCandidatesForKey(state, action.payload.key);
    case FULFILLED(IshActions.AUTOCOMPLETE):
      return {
        ...state,
        ...{[action.payload.key]: action.payload.candidates}
      };
    case REJECTED(IshActions.AUTOCOMPLETE):
      return emptyCandidatesForKey(state, action.payload.key);
    default:
      return state;
  }
}

function emptyCandidatesForKey(state: AutocompleteState = {}, key: string): AutocompleteState {
  return {
    ...state,
    ...{[key]: []}
  };
}
