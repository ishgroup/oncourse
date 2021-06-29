import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {createLogger} from "redux-logger";
import {applyMiddleware, combineReducers, createStore} from "redux";
import {Field, reducer as formReducer, reduxForm} from "redux-form";
import {Values} from 'redux-form-website-template';

import "../../../scss/_ReactSelect.scss";

import {SearchApiMock} from "../../mocks/SearchApiMock";
import SelectField from "../../../js/components/form-new/SelectField";
import {TextField} from "../../../js/components/form-new/TextField";
import {MockConfig} from "../../mocks/mocks/MockConfig";
import { InjectedFormProps } from 'redux-form/lib/reduxForm';

const store = createStore(
  combineReducers({form: formReducer}), applyMiddleware(createLogger()),
);


const config: MockConfig = new MockConfig();
const stub: SearchApiMock = new SearchApiMock(config);


const loadOptions = (text) => {
  return stub.getSuburbs(text);
};

class ReactSelectForm extends React.Component<InjectedFormProps, any> {
  render() {
    const {handleSubmit, pristine, reset, submitting} = this.props;
    return (
      <form onSubmit={handleSubmit}>
        <fieldset>
          <Field component={TextField} name="address" label="Street" required={true} meta={{error: "Error message", touched: true}}/>
          <Field component={TextField} name="address" label="Street" required={true} meta={{warning: "Warning message", touched: true}}/>
          <Field component={TextField} name="address" label="Street" required={true} meta={{}}/>

          <Field component={TextField} name="address" label="Street" required={true}/>
          <Field
            component={SelectField}
            name="country"
            label="Country"
            required={true}
            loadOptions={text => loadOptions(text)}
            newOptionEnable={true}
          />
        </fieldset>
        <div>
          <button type="submit" disabled={pristine || submitting}>Submit</button>
          <button type="button" disabled={pristine || submitting} onClick={reset}>
            Clear Values
          </button>
        </div>
      </form>
    );
  }
}

const Form = reduxForm({
  form: "ReactSelectForm"
})(ReactSelectForm);


function showResults(values) {
  window.alert(`You submitted:\n\n${JSON.stringify(values, null, 2)}`);
}

const render = () => ReactDOM.render(
  <Provider store={store}>
    <div>
      <Form onSubmit={showResults}/>
      <Values form="ReactSelectForm" />
    </div>
  </Provider>,
  document.getElementById("root"),
);

render();
