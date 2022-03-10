import { ActionsObservable, Epic } from "redux-observable";
import { filter, toArray } from "rxjs/operators";
import { mockedAPI, store } from "../TestEntry";
import { FETCH_FINISH, FETCH_START } from "../../js/common/actions";

interface Props {
  action: any;
  epic: Epic<any, any>,
  processData: (mockedApi: any) => any;
  beforeProcess?: () => void;
}

export const DefaultEpic = ({
  action, epic, processData, beforeProcess = () => {}
}: Props) => {
  // Redux action to trigger epic
  const action$ = ActionsObservable.of(typeof action === "function" ? action(mockedAPI) : action );

  // Initializing epic instance
  const epic$ = epic(action$, store, {});

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
