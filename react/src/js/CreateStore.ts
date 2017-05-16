import {Store} from "react-redux";
import {createEpicMiddleware} from "redux-observable";
import {createLogger} from "redux-logger";
import {applyMiddleware, createStore, GenericStoreEnhancer} from "redux";
import {IshState} from "./services/IshState";
import {RootEpic} from "./RootEpics";
import {combinedReducers} from "./reducers/reducers";
import {EnvironmentConstants} from "./config/EnvironmentConstants";
import {Actions} from "./web/actions/Actions";

export function CreateStore(): Store<IshState> {
  const store = createStore(
    combinedReducers,
    getMiddleware()
  );


  function getMiddleware(): GenericStoreEnhancer {
    const logger = createLogger({
      collapsed: true
    });

    /**
     * Split middlewares which we using in development and in production.
     */
    if (process.env.NODE_ENV === EnvironmentConstants.development) {
      return applyMiddleware(createEpicMiddleware(RootEpic), logger);
    } else {
      return applyMiddleware(createEpicMiddleware(RootEpic));
    }
  }

  // Trigger syncing state with LocalStorage
  // store.dispatch({
  //   type: Actions.SYNC_CART,
  //   payload: []
  // });

  return store;
}
