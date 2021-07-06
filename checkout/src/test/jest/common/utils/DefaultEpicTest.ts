import { ActionsObservable, createEpicMiddleware, Epic } from 'redux-observable';
import { mockState } from '../../../../dev/mocks/mocks/MockFunctions';
import { IshAction } from '../../../../js/actions/IshAction';
import { applyMiddleware, compose, createStore, Store, StoreEnhancer } from 'redux';
import { IshState } from '../../../../js/services/IshState';
import { combinedReducers } from '../../../../js/reducers/reducers';
import { EpicRoot } from '../../../../js/EpicRoot';

interface Args {
  action: IshAction<any> | IshAction<any>[],
  epic: Epic<any, any>,
  result: IshAction<any> | IshAction<any>[],
  customStore?: IshState;
}


export const DefaultEpicTest = ({
  action, epic, result, customStore
}: Args) => {
  //Initial store
  const store: Store<IshState> = createStore(
    combinedReducers,
    customStore || mockState(),
    <StoreEnhancer<IshState>>compose(applyMiddleware(createEpicMiddleware(EpicRoot)) as any),
  ) as Store<IshState>;

  // Redux action to trigger epic
  const action$ = ActionsObservable.of(action);

  // Initializing epic instance
  const epic$ = epic(action$, store, {});

  // Testing epic to be resolved with expected array of actions
  epic$.subscribe((action) => {
    expect(action).toEqual(result);
  })
};
