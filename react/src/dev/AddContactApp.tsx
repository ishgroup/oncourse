import * as React from "react";
import * as ReactDOM from "react-dom";

import {connect, Provider} from "react-redux";
import AddContactForm from "../js/enrol/containers/contact/AddContactForm";
import {configureStore} from "../js/configureStore";
import Messages from "../js/enrol/components/Messages";
import {Progress, Tab} from "../js/enrol/components/Progress";

// const reducer = (state: any = {}): any => {
//   return state;
// };
//
// const store = createStore(combineReducers({state: reducer, form: formReducer}));

const store = configureStore();


const MessagesRedux = connect((state) => {
  return {error: state.enrol.error}
}, (dispatch) => {
  return {}
})(Messages);

const render = () => ReactDOM.render(
  <Provider store={store}>
    <div id="checkout" className="payments">
      <Progress onChange={(t) => {console.log(t)}}
        model = {{
          active: Tab.details,
          disabled: [Tab.summary, Tab.payment]
        }}
      />
      <MessagesRedux/>
      <AddContactForm/>
    </div>
  </Provider>,
  document.getElementById("root")
);

render();