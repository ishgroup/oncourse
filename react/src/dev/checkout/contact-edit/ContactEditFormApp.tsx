import * as React from "react";
import * as ReactDOM from "react-dom";

import "react-select/dist/react-select.css";

import {connect, Provider} from "react-redux";
import {ContactAdd} from "enrol/containers/contact-add/actions/Actions";
import ContactEditForm, {NAME} from "enrol/containers/contact-edit/ContactEditForm";
import {FieldsLoadRequest} from "enrol/containers/contact-edit/actions/Actions";
import {CreateStore} from "CreateStore";
import {Progress, Tab} from "enrol/components/Progress";
import {normalize} from "normalizr";
import {contactsSchema} from "schema";
import {FieldSet} from "model/field/FieldSet";
import { Values } from 'redux-form-website-template';
import {MessagesRedux} from "enrol/containers/Functions";

// const reducer = (state: any = {}): any => {
//   return state;
// };
//
// const store = createStore(combineReducers({state: reducer, form: formReducer}));

const store = CreateStore();

//add contact
store.dispatch({
  type: ContactAdd,
  payload: normalize({
    id: 1001,
    firstName: 'Andrey',
    lastName: 'Koyro',
    email: 'pervoliner@gmail.com',
    fieldSet: FieldSet.enrolment
  }, contactsSchema)
});

store.dispatch({
  type: FieldsLoadRequest
});


const render = () => ReactDOM.render(
  <Provider store={store}>
    <div id="checkout" className="payments">
      <Progress onChange={(t) => {console.log(t)}}
        model = {{
          active: Tab.Details,
          disabled: [Tab.Summary, Tab.Payment]
        }}
      />
      <MessagesRedux/>
      <ContactEditForm/>
      <Values form={NAME} />
    </div>
  </Provider>,
  document.getElementById("root")
);

render();