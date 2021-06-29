import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {createLogger} from "redux-logger";
import {applyMiddleware, combineReducers, createStore} from "redux";
import {Field, reducer as formReducer, reduxForm} from "redux-form";
import { Values } from 'redux-form-website-template';
import { InjectedFormProps } from 'redux-form/lib/reduxForm';

const store = createStore(
  combineReducers({form: formReducer}), applyMiddleware(createLogger())
);

class RadioGroupForm extends React.Component<InjectedFormProps, any> {
  render() {
    const {handleSubmit, pristine, reset, submitting} = this.props;
    return (
      <form onSubmit={handleSubmit}>
        <div>
          <label>Sex</label>
          <div>
            <label>
              <Field name="sex" component="input" type="radio" value="male" />
              {' '}
              Male
            </label>
            <label>
              <Field name="sex" component="input" type="radio" value="female" />
              {' '}
              Female
            </label>
          </div>
        </div>
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
  form: "RadioGroupForm"
})(RadioGroupForm);


function showResults(values) {
  window.alert(`You submitted:\n\n${JSON.stringify(values, null, 2)}`);
}

const render = () => ReactDOM.render(
  <Provider store={store}>
    <div id="checkout" className="payments">
      <Form onSubmit={showResults}/>
      <Values form="RadioGroupForm" />
    </div>
  </Provider>,
  document.getElementById("root")
);

render();
