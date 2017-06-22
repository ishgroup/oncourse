import {Store} from "react-redux";
import {createEpicMiddleware} from "redux-observable";
import {createLogger} from "redux-logger";
import {applyMiddleware, compose, createStore, GenericStoreEnhancer} from "redux";
import {IshState} from "./services/IshState";
import {EpicRoot} from "./EpicRoot";
import {combinedReducers} from "./reducers/reducers";
import {EnvironmentConstants} from "./config/EnvironmentConstants";
import {autoRehydrate, getStoredState, OnComplete, persistStore} from "redux-persist";
import localForage from "localforage";


const getMiddleware = (): GenericStoreEnhancer => {
  const logger = createLogger({
    collapsed: true,
  });

  /**
   * Split middlewares which we using in development and in production.
   */
  if (process.env.NODE_ENV === EnvironmentConstants.development) {
    return applyMiddleware(createEpicMiddleware(EpicRoot), logger);
  } else {
    return applyMiddleware(createEpicMiddleware(EpicRoot));
  }
};

export const CreateStore = (): Store<IshState> => {
  const store: Store<IshState> = createStore(
    combinedReducers,
    compose(getMiddleware(), autoRehydrate()),
  ) as Store<IshState>;
  return store;
};

export const RestoreState = (store: Store<IshState>, onComplete: OnComplete<any>): void => {
  persistStore(store, {storage: localForage, blacklist: ["form", "phase", "page"]}, onComplete);
};
