import * as React from "react";
import * as ReactDOM from "react-dom";

import "react-select/dist/react-select.css";
import "../../../scss/_ReactSelect.scss";

import {Provider} from "react-redux";
import {ADD_CONTACT} from "../../../js/enrol/containers/contact-add/actions/Actions";
import ContactEditForm, {NAME} from "../../../js/enrol/containers/contact-edit/ContactEditForm";
import {OpenContactDetailsRequest} from "../../../js/enrol/containers/contact-edit/actions/Actions";
import {CreateStore} from "../../../js/CreateStore";
import {normalize} from "normalizr";
import {ContactSchema} from "../../../js/NormalizeSchema";
import {Values} from "redux-form-website-template";
import {Progress, Tab} from "../../../js/enrol/components/Progress";
import {Messages} from "../../../js/enrol/containers/Functions";
import {MockConfig} from "../../mocks/mocks/MockConfig";

const config = new MockConfig();

config.init((config: MockConfig) => {

  //add contact
  config.store.dispatch({
    type: ADD_CONTACT,
    payload: normalize({
      id: 1001,
      firstName: 'Andrey',
      lastName: 'Koyro',
      email: 'pervoliner@gmail.com'
    }, ContactSchema)
  });

  config.store.dispatch({
    type: OpenContactDetailsRequest
  });

  render(config);

});


const render = (config) => ReactDOM.render(
  <Provider store={config.store}>
    <div id="checkout" className="payments">
      <Progress onChange={(t) => {
      }} model={{active: Tab.Details, disabled: [Tab.Summary, Tab.Payment]}}/>
      <Messages/>
      <ContactEditForm/>
      <Values form={NAME}/>
    </div>
  </Provider>,
  document.getElementById("root")
);