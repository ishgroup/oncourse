import {
  applyMiddleware, compose, createStore, Store, StoreEnhancer
} from 'redux';
import { createLogger } from 'redux-logger';
import { createEpicMiddleware } from 'redux-observable';
import { billingReducers } from './reducers';
import { EpicRoot } from './epics';

export const epicMiddleware = createEpicMiddleware();

const getMiddleware = (): StoreEnhancer<any> => {
  const logger = createLogger({
    collapsed: true,
  });

  if (process.env.NODE_ENV === 'development') {
    return applyMiddleware(epicMiddleware, logger);
  }
  return applyMiddleware(epicMiddleware);
};

const CreateStore = (): Store<any> => {
  const store: Store<any> = createStore(
    billingReducers,
    compose(getMiddleware()),
  ) as Store<any>;

  epicMiddleware.run(EpicRoot);

  return store;
};

export const store = CreateStore();
