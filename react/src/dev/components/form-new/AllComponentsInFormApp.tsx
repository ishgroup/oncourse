import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {createLogger} from "redux-logger";
import {applyMiddleware, combineReducers, createStore} from "redux";
import {Field, reducer as formReducer, reduxForm} from "redux-form";
import {Values} from "redux-form-website-template";

import "../../../scss/index.scss";
import {SearchApiMock} from "../../mocks/SearchApiMock";

import {TextField} from "../../../js/components/form-new/TextField";
import Checkbox from "../../../js/components/form-new/Checkbox";
import SelectField from "../../../js/components/form-new/SelectField";
import RadioGroup from "../../../js/components/form-new/RadioGroup";
import TextArea from "../../../js/components/form-new/TextArea";
import {MockConfig} from "../../mocks/mocks/MockConfig";


const store = createStore(
  combineReducers({form: formReducer}), applyMiddleware(createLogger()),
);

const options = [
  {key: 'one', value: 'One'},
  {key: 'two', value: 'Two'},
  {key: 'three', value: 'Three'},
  {key: 'five', value: 'Five'},

];

const config: MockConfig = new MockConfig();
const stub: SearchApiMock = new SearchApiMock(config);

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
          <Field component={SelectField} name="suburb" label="Suburb" required={true}
                 loadOptions={text => stub.getCountries(text)}
          />
        </fieldset>
        <fieldset>
          <Field component={RadioGroup} name="Gender" label="Gender" required={true}
                 items={[{key: "1", value: "Male"}, {key: "2", value: "Female"}]}/>
        </fieldset>
        <fieldset>
          <Field component={TextArea} name="needs" label="Special Needs" required={true}/>
        </fieldset>
        <button type="submit" className="btn btn-default" disabled={pristine || submitting}>Submit</button>
      </form>
    );
  }
}

const validate = (values: FormData) => {
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
