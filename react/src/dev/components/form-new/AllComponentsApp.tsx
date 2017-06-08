import * as React from "react";
import * as ReactDOM from "react-dom";

import {Provider} from "react-redux";
import {createLogger} from "redux-logger";
import {applyMiddleware, combineReducers, createStore} from "redux";
import {reducer as formReducer, reduxForm} from "redux-form";
import {Values} from "redux-form-website-template";

import {SearchApiMock} from "../../mocks/SearchApiMock";
import {TextField} from "../../../js/components/form-new/TextField";
import Checkbox from "../../../js/components/form-new/Checkbox";
import SelectField from "../../../js/components/form-new/SelectField";
import RadioGroup from "../../../js/components/form-new/RadioGroup";
import TextArea from "../../../js/components/form-new/TextArea";

import "react-select/dist/react-select.css";
import "../../../scss/_ReactSelect.scss";
import "../../../scss/_ReactTooltip.scss";

const store = createStore(
  combineReducers({form: formReducer}), applyMiddleware(createLogger())
);

const options = [
  {key: 'one', value: 'One'},
  {key: 'two', value: 'Two'},
  {key: 'three', value: 'Three'},
  {key: 'five', value: 'Five'},

];


const stub: SearchApiMock = new SearchApiMock(null);

const loadOptions = (text) => {
  return stub.getCountries(text).then((data) => Promise.resolve({options: data}));
};


/**
 * All components application, to test styling
 */
class AllComponentsFrom extends React.Component<any, any> {
  render() {
    const {handleSubmit, pristine, reset, submitting} = this.props;
    return (
      <div>
        <form onSubmit={handleSubmit}>

        <fieldset>
          <TextField name="address" type={"number"} label="Street" required={true}
                     meta={{error: "address Error message", touched: true}}/>
          <TextField name="address1" label="Street" required={true}
                     meta={{warning: "address Warning message", touched: true}}/>
          <TextField name="address2" label="Street" required={true} meta={{}}/>
        </fieldset>
        <fieldset>
          <Checkbox name="email" label="E-mail" required={true} meta={{error: "Error message", touched: true}}/>
          <Checkbox name="email1" label="E-mail" required={true} meta={{warning: "Warning message", touched: true}}/>
          <Checkbox name="email2" label="E-mail" required={true} meta={{}}/>
        </fieldset>
        <fieldset>
          <SelectField name="suburb" label="Suburb" required={true} meta={{error: "Error message", touched: true}}
                       input={{}}/>
          <SelectField name="suburb1" label="Suburb" required={true} meta={{warning: "Warning message", touched: true}}
                       input={{}}/>
          <SelectField name="suburb2" label="Suburb" required={true} meta={{}} input={{}}/>
        </fieldset>
        <fieldset>
          <RadioGroup name="Gender" label="Gender" required={true} items={[{key: "1", value: "Male"}, {key: "2", value: "Female"}]} meta={{error: "Error message", touched: true}}/>
          <RadioGroup name="Gender1" label="Gender" required={true} items={[{key: "1", value: "Male"}, {key: "2", value: "Female"}]} meta={{warning: "Warning message", touched: true}}/>
          <RadioGroup name="Gender2" label="Gender" required={true} items={[{key: "1", value: "Male"}, {key: "2", value: "Female"}]} meta={{}}/>
        </fieldset>
        <fieldset>
          <TextArea name="needs" label="Special Needs" required={true} meta={{error: "Error message", touched: true}}/>
          <TextArea name="needs1" label="Special Needs" required={true} meta={{warning: "Warning message", touched: true}}/>
          <TextArea name="needs2" label="Special Needs" required={true} meta={{}}/>
        </fieldset>
      </form>
      </div>
    );
  }
}

const Form = reduxForm({
  form: "AllComponentsFrom"
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