/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createEpicMiddleware } from 'redux-observable';
import { createLogger } from 'redux-logger';
import {
  applyMiddleware, compose, createStore, Store,
} from 'redux';
import { State } from '../model/State';
import { IAction } from '../model/IshAction';
import { combinedReducers } from './index';
import { EpicRoot } from '../epics';

export const epicMiddleware = createEpicMiddleware();

/* tslint:disable */
const composeEnhancers = __DEV__ && global.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
  ? global.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({ trace: true, traceLimit: 50 })
  : compose;
/* tslint:enable */

const CreateStore = (): Store<State, IAction<any>> => {
  const logger = createLogger({
    collapsed: true,
  });

  let store;

  /**
   * Split middlewares which we using in development and in production.
   */
  if (__DEV__) {
    store = createStore(
      combinedReducers,
      composeEnhancers(applyMiddleware(epicMiddleware, logger)),
    );
  } else {
    store = createStore(combinedReducers, composeEnhancers(applyMiddleware(epicMiddleware)));
  }

  epicMiddleware.run(EpicRoot);

  return store;
};

export default CreateStore();
