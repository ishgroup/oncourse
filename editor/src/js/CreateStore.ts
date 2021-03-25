import {createEpicMiddleware} from "redux-observable";
import {createLogger} from "redux-logger";
import {applyMiddleware, compose, createStore, Store, StoreEnhancer} from "redux";
import {EpicRoot} from "./EpicRoot";
import { persistReducer } from 'redux-persist'
import {combinedReducers} from "./reducers";
import {EnvironmentConstants} from "./config/EnvironmentConstants";
import localforage from "localforage";
import {State} from "./reducers/state";

const persistConfig = {
  key: 'root',
  storage: localforage,
  blacklist: ["form", "notifications"]
}

const persistedReducer = persistReducer(persistConfig, combinedReducers);

const inDevelopment = process.env.NODE_ENV === EnvironmentConstants.development;

const epicMiddleware = createEpicMiddleware();

const global: any = window;

const composeEnhancers = inDevelopment && global.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
  // @ts-ignore
  ? global.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({ trace: true, traceLimit: 50 })
  : compose;

export const CreateStore = (): Store<State> => {
  const logger = createLogger({
    collapsed: true,
  });

  let store;

  /**
   * Split middlewares which we using in development and in production.
   */
  if (inDevelopment) {
    store = createStore(persistedReducer, composeEnhancers(applyMiddleware(epicMiddleware, logger)));
  } else {
    store = createStore(persistedReducer, composeEnhancers(applyMiddleware(epicMiddleware)));
  }

  epicMiddleware.run(EpicRoot);

  return store;
};
