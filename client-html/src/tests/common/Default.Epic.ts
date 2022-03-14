import { ActionsObservable, Epic, StateObservable } from "redux-observable";
import { filter, toArray } from "rxjs/operators";
import { Subject } from "rxjs";
import { mockedAPI } from "../TestEntry";
import { FETCH_FINISH, FETCH_START } from "../../js/common/actions";
import TestStore from "../../js/constants/Store";

interface Props {
  action: any;
  epic: Epic<any, any>,
  processData: (mockedApi: any) => any;
  beforeProcess?: () => void;
  store?: Object;
}

export const DefaultEpic = ({
  action, epic, processData, beforeProcess = () => {}, store = {}
}: Props) => {
  // Initialize / override default state
  const state = new StateObservable(new Subject(), { ...TestStore.getState(), ...store });

  // Redux action to trigger epic
  const action$ = ActionsObservable.of(typeof action === "function" ? action(mockedAPI) : action );

  // Initializing epic instance
  const epic$ = epic(action$, state, {});

  if (beforeProcess) beforeProcess();

  // Testing epic to be resolved with expected array of actions
  return expect(
    epic$
      .pipe(
        // Filtering common actions
        filter((a: any) => ![FETCH_START, FETCH_FINISH].includes(a.type)),
        toArray()
      )
      .toPromise()
  ).resolves.toEqual(processData(mockedAPI));
};
