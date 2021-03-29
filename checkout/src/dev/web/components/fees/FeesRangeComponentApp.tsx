import * as React from "react";
import * as ReactDOM from "react-dom";
import {FeesRangeComponent, Props, Values} from "../../../../js/web/components/fees/FeesRangeComponent";
import {mockCourseClassWithFeesRange, mockField} from "../../../mocks/mocks/MockFunctions";
import {DataType} from "../../../../js/model";
import {connect, Provider} from "react-redux";
import {applyMiddleware, combineReducers, createStore, Dispatch, Store} from "redux";
import {reducer as formReducer} from "redux-form";
import "../../../../scss/index.scss";
import {createLogger} from "redux-logger";
import {IAction} from "../../../../js/actions/IshAction";

const reducer = combineReducers({
  form: formReducer,
  estimatedDiscountIndex: (state: number = null, action: IAction<number>): number => {
    switch (action.type) {
      case "SET":
        return action.payload;
    }
    return state;
  },
});

export const store: Store<any> = createStore(reducer, applyMiddleware(createLogger()));


const props: Props = {
  onSubmit: () => {
  },
  model: {
    header: "<h3 class=\"modal-title pull-left text-nowrap\" id=\"ModalLabel\">Am I eligible?</h3>",
    message: "<p tabindex=\"0\">Answer these questions to find out if you are eligible to access Smart and Skilled government subsidised training.</p>",
    price: mockCourseClassWithFeesRange(),
    fields: [
      mockField("I am 15 years or older.", "over15", DataType.BOOLEAN),
      mockField("I have left school.", "leftSchool", DataType.BOOLEAN),
      mockField("I live or work in NSW.", "liveInNsw", DataType.BOOLEAN),
      mockField("I am an Australian citizen or Australian permanent resident or humanitarian visa holder or New Zealand citizen.",
                "citizen", DataType.BOOLEAN),
    ],
  },
};

const render = () => ReactDOM.render(
  <Provider store={store}>
    <div>
      <Comp/>
    </div>
  </Provider>,
  document.getElementById("react-fees-range-component"),
);

const Comp = connect<any, any, any, any>(state => {
  return {
    model: props.model,
    estimatedDiscountIndex: state.estimatedDiscountIndex,
  };
},                   (dispatch: Dispatch<any>): any => {
  return {
    onSubmit: () => dispatch({type: "SET", payload: 1}),
  };
})(FeesRangeComponent);

render();
