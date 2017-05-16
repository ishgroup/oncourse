import {combineEpics} from "redux-observable";
import {Observable} from "rxjs";
import {Injector} from "./injector";
import {IshState} from "./services/IshState";
import {IshActions} from "./constants/IshActions";
import {Store} from "redux";
import {CheckoutEpic} from "./enrol/epics/CheckoutEpic";
import {mapError, mapPayload} from "./epics/epicsUtils";
import {reduxFormEpics} from "./epics/reduxFormEpics";
import {AutocompleteRequestPayload, AutocompleteResponsePayload} from "./actions/actions";
import {IshAction} from "./actions/IshAction";
import {WebEpic} from "./web/epics/WebEpic";

export const RootEpic = combineEpics(
  WebEpic,
  CheckoutEpic,
  reduxFormEpics,
  createAutocompleteEpic(IshActions.AUTOCOMPLETE)
);

const {
  autocompleteApi,
} = Injector.of();



function createAutocompleteEpic(actionType) {
  return (action$, store: Store<IshState>) => action$
    .ofType(actionType)
    .mergeMap((action: IshAction<AutocompleteRequestPayload>) => {
      return Observable
        .fromPromise(autocompleteApi.autocomplete(action.payload.key, action.payload.text))
        .takeUntil(action$.ofType(
          actionType,
          IshActions.AUTOCOMPLETE_CLEAR
        )) // Prevent showing old results (old promise which returned after new one)
        .map(payload => ({
          key: action.payload.key,
          candidates: payload
        }) as AutocompleteResponsePayload)
        .map(mapPayload(actionType))
        .catch(mapError(actionType));
    });
}

