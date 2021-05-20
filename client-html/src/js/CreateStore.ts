/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createEpicMiddleware } from "redux-observable";
import { createLogger } from "redux-logger";
import { applyMiddleware, compose, createStore, Store } from "redux";
import { EpicRoot } from "./EpicRoot";
import { combinedReducers } from "./reducers";
import { EnvironmentConstants } from "./constants/EnvironmentConstants";
import { State } from "./reducers/state";
import { IAction } from "./common/actions/IshAction";

const inDevelopment = process.env.NODE_ENV === EnvironmentConstants.development;

export const epicMiddleware = createEpicMiddleware();

/* tslint:disable */
// @ts-ignore
const composeEnhancers = inDevelopment && window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
  // @ts-ignore
  ? window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({ trace: true, traceLimit: 50 })
  : compose;
/* tslint:enable */

export const CreateStore = (): Store<State, IAction<any>> => {
  const logger = createLogger({
    collapsed: true
  });

  let store;

  /**
   * Split middlewares which we using in development and in production.
   */
  if (inDevelopment) {
    store = createStore(combinedReducers, composeEnhancers(applyMiddleware(epicMiddleware, logger)));
  } else {
    store = createStore(combinedReducers, composeEnhancers(applyMiddleware(epicMiddleware)));
  }

  epicMiddleware.run(EpicRoot);

  return store;
};

// export const RestoreState = (store: Store<any>, onComplete: OnComplete<any>): void => {
//   persistStore(store, { storage: localforage, blacklist: [] }, onComplete);
// };
