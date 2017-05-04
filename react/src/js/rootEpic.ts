import {combineEpics} from "redux-observable";
import {Observable} from "rxjs";
import {Injector} from "./injector";
import {IshState} from "./services/IshState";
import {IshActions} from "./constants/IshActions";
import {Store} from "redux";
import {enrolEpics} from "./enrol/epics/enrolEpics";
import {mapError, mapPayload} from "./epics/epicsUtils";
import {reduxFormEpics} from "./epics/reduxFormEpics";
import {AutocompleteRequestPayload, AutocompleteResponsePayload} from "./actions/actions";
import {IshAction} from "./actions/IshAction";
import {WebEpics} from "./web/epics/webEpics";

export const rootEpic = combineEpics(
  WebEpics,
  enrolEpics,
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

