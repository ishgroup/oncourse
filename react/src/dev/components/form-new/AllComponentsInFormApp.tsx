import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {createLogger} from "redux-logger";
import {applyMiddleware, combineReducers, createStore} from "redux";
import {Field, FormErrors, FormProps, reducer as formReducer, reduxForm} from "redux-form";
import {Values} from "redux-form-website-template";

import "react-select/dist/react-select.css";
import "../../../scss/_ReactSelect.scss";
import {SearchApiMock} from "../../mocks/SearchApiMock";

import TextField from "../../../js/components/form-new/TextField";
import Checkbox from "../../../js/components/form-new/Checkbox";
import SelectField from "../../../js/components/form-new/SelectField";
import RadioGroup from "../../../js/components/form-new/RadioGroup";
import TextArea from "../../../js/components/form-new/TextArea";


const store = createStore(
  combineReducers({form: formReducer}), applyMiddleware(createLogger())
);

const options = [
  {key: 'one', value: 'One'},
  {key: 'two', value: 'Two'},
  {key: 'three', value: 'Three'},
  {key: 'five', value: 'Five'},

];
//https://gist.github.com/leocristofani/98312e61807db8f32e720c9f97a186e5
//https://github.com/JedWatson/react-select/issues/1129


const stub: SearchApiMock = new SearchApiMock(null);

/**
 *  application for all components to test how they work inside redux-form
 */
class AllComponentsFrom extends React.Component<any, any> {
  render() {
    const {handleSubmit, pristine, reset, submitting} = this.props;
    return (
      <form onSubmit={handleSubmit}>
        <fieldset>
          <Field component={TextField} name="address" type={"number"} label="Street" required={true}/>
        </fieldset>
        <fieldset>
          <Field component={Checkbox} name="email" label="E-mail" required={true}/>
        </fieldset>
        <fieldset>
          <Field component={SelectField} name="suburb" label="Suburb" required={true} loadOptions={stub.getCountries}/>
        </fieldset>
        <fieldset>
          <Field component={RadioGroup} name="Gender" label="Gender" required={true} items={[{key: "1", value: "Male"}, {key: "2", value: "Female"}]}/>
        </fieldset>
        <fieldset>
          <Field component={TextArea} name="needs" label="Special Needs" required={true}/>
        </fieldset>
        <button type="submit" className="btn btn-default" disabled={pristine || submitting}>Submit</button>
      </form>
    );
  }
}

const validate = (values: FormData, props: FormProps<FormData, any, any> & any): FormErrors<FormData> => {
  const errors = Object.assign({}, values);
  return errors;
};

const Form = reduxForm({
  form: "AllComponentsFrom",
  validate: validate
})(AllComponentsFrom);


function showResults(values) {
  window.alert(`You submitted:\n\n${JSON.stringify(values, null, 2)}`);
}

const render = () => ReactDOM.render(
  <Provider store={store}>
    <div id="checkout" className="payments">
      <Form onSubmit={showResults}/>
      <Values form="AllComponentsFrom"/>
    </div>
  </Provider>,
  document.getElementById("root")
);

render();