import {Store} from "react-redux";
import {createEpicMiddleware} from "redux-observable";
import {createLogger} from "redux-logger";
import {createStore, applyMiddleware, GenericStoreEnhancer} from "redux";
import {IshState} from "./services/IshState";
import {rootEpic} from "./rootEpic";
import {combinedReducers} from "./reducers/reducers";
import {EnvironmentConstants} from "./config/EnvironmentConstants";
import {IshActions} from "./constants/IshActions";

export function configureStore(): Store<IshState> {
  const store = createStore(
    combinedReducers,
    getMiddlewares()
  );

  function getMiddlewares(): GenericStoreEnhancer {
    const logger = createLogger({
      collapsed: true
    });

    /**
     * Split middlewares which we using in development and in production.
     */
    if (process.env.NODE_ENV === EnvironmentConstants.development) {
      return applyMiddleware(createEpicMiddleware(rootEpic), logger);
    } else {
      return applyMiddleware(createEpicMiddleware(rootEpic));
    }
  }

  // Trigger syncing state with LocalStorage
  store.dispatch({
    type: IshActions.SYNC_CART,
    payload: []
  });


  window["store"] = store;

  return store;
}
