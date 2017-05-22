import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {createLogger} from "redux-logger";
import {applyMiddleware, combineReducers, createStore} from "redux";
import {Field, reducer as formReducer, reduxForm} from "redux-form";
import { Values } from 'redux-form-website-template';

import 'react-select/dist/react-select.css';
import "../../../scss/_ReactSelect.scss";

import {AutocompleteApiStub} from "../../../js/httpStub/AutocompleteApiStub";
import SelectField from "../../../js/components/form-new/SelectField";
import TextField from "../../../js/components/form-new/TextField";

const store = createStore(
  combineReducers({form: formReducer}), applyMiddleware(createLogger())
);

const options = [
  { key: 'one', value: 'One' },
  { key: 'two', value: 'Two' },
  { key: 'three', value: 'Three' },
  { key: 'five', value: 'Five' },

];
//https://gist.github.com/leocristofani/98312e61807db8f32e720c9f97a186e5
//https://github.com/JedWatson/react-select/issues/1129


const stub:AutocompleteApiStub = new AutocompleteApiStub(null);

const loadOptions = (text) => {
  return stub.getCountries(text).then((data) => Promise.resolve({options:data}));
};

class ReactSelectForm extends React.Component<any, any> {
  render() {
    const {handleSubmit, pristine, reset, submitting} = this.props;
    return (
      <form onSubmit={handleSubmit}>
        <fieldset>
          <TextField name="address" label="Street" required={true} meta={{error: "Error message", touched: true}}/>
          <TextField name="address" label="Street" required={true} meta={{warning: "Warning message", touched: true}}/>
          <TextField name="address" label="Street" required={true} meta={{}}/>

          <Field component={TextField} name="address" label="Street" required={true}/>
          <Field component={SelectField} name={"country"} label={"Country"} required={true} loadOptions={stub.getCountries}/>
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
    <div id="select-field" className="select-field-form">
      <Form onSubmit={showResults}/>
      <Values form="ReactSelectForm" />
    </div>
  </Provider>,
  document.getElementById("root")
);

render();