import * as React from "react";
import * as ReactDOM from "react-dom";
import {Provider} from "react-redux";
import {createLogger} from "redux-logger";
import {applyMiddleware, combineReducers, createStore} from "redux";
import {Field, reducer as formReducer, reduxForm} from "redux-form";
import {Values} from "redux-form-website-template";
import {TextField} from "../../../js/components/form/TextField";
import {CheckboxField} from "../../../js/components/form/CheckboxField";
import {ComboboxField} from "../../../js/components/form/ComboboxField";
import {TextAreaField} from "../../../js/components/form/TextAreaField";


const store = createStore(
  combineReducers({form: formReducer}), applyMiddleware(createLogger()),
);

const options = [
  {key: 'one', value: 'One'},
  {key: 'two', value: 'Two'},
  {key: 'three', value: 'Three'},
  {key: 'five', value: 'Five'},

];
// https://gist.github.com/leocristofani/98312e61807db8f32e720c9f97a186e5
// https://github.com/JedWatson/react-select/issues/1129

/**
 *  application for all components to test how they work inside redux-form
 */
class AllComponentsFrom extends React.Component<any, any> {

  render() {
    const {handleSubmit, pristine, submitting} = this.props;
    return (
      <form onSubmit={handleSubmit}>
        <fieldset>
          <Field component={TextField} name="address" type={"number"} label="Street" required={true}/>
        </fieldset>
        <fieldset>
          <Field component={CheckboxField} name="email" label="E-mail" required={true}/>
        </fieldset>
        <fieldset>
          <ComboboxField name="suburb" label="Suburb" required={true} items={options}/>
        </fieldset>
        <fieldset>
          <Field component={TextAreaField} name="needs" label="Special Needs" required={true}/>
        </fieldset>
        <button type="submit" className="btn btn-default" disabled={pristine || submitting}>Submit</button>
      </form>
    );
  }
}

const validate = values => {
  const errors = Object.assign({}, values);
  return errors;
};

const Form = reduxForm({
  validate,
  form: "AllComponentsFrom",
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
  document.getElementById("root"),
);

render();
