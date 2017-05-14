import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {CreateStore} from "../js/CreateStore";
import Checkout from "../js/enrol/containers/checkout/Checkout";
import {Actions} from "../js/web/actions/Actions";

const store = CreateStore();

const loadCourseClasses = () => {
  store.dispatch({
    type: Actions.REQUEST_COURSE_CLASS,
    payload:[5037296],
  });
};

const addCourseClass = () => {
  store.dispatch({
    type: Actions.ADD_CLASS_TO_CART,
    payload: {id: "5037296"}
  });
};

const removeCourseClass = () => {
  store.dispatch({
    type: Actions.REMOVE_CLASS_FROM_CART,
    payload: {id: "5037296"}
  });
};


const render = () => ReactDOM.render(
  <Provider store={store}>
    <div id="checkout" className="payments">
      <Checkout/>
      <button onClick={loadCourseClasses}>Load Classes</button>
      <button onClick={addCourseClass}>Add Classes</button>
      <button onClick={removeCourseClass}>Remove Classes</button>
    </div>
  </Provider>,
  document.getElementById("root")
);

render();