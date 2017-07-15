import * as React from "react";
import Rodal from "rodal";
import "rodal/lib/rodal.css";
import {Props as FeeProps, Values} from "./FeesRangeComponent";
import {toFormFieldProps} from "../../../components/form/FieldFactory";
import {Field, FormProps, reduxForm} from "redux-form";
import {RadioGroupField} from "../../../components/form/RadioGroupField";


interface State {
  visible: boolean
}

export class Dialog extends React.Component<FeeProps, State> {

  constructor() {
    super();
    this.state = {
      visible: false
    };
  }


  hide(): void {
    this.setState({
      visible: false
    });
    if (this.props.onClose) {
      this.props.onClose();
    }
  }


  render() {
    const onClose = () => {
      this.hide()
    };
    return (
      <Rodal visible={this.state.visible}
             onClose={onClose}
             animation={"zoom"}
             height={1000}
             customStyles={{maxHeight: 400, minHeight: 300}}
      >
        {this.questions()}
      </Rodal>
    )
  }

  questions() {
    const onSubmit = (values: Values): void => {
      this.props.onSubmit(values);
      this.hide()
    };
    return (
      <Form {...this.props} onSubmit={onSubmit}/>
    )
  }
}

export interface Props extends FormProps<FormData, any, any>, FeeProps {

}

export class Fields extends React.Component<any, any> {
  render() {
    const {model, handleSubmit, onClose} = this.props;
    const p: any = {
      key: "testKey",
      name: "testName",
      label: "testLabel",
      type: "text",
      required: "true",
      placeholder: "placeholder",
    };
    return (
      <div className="modal-content">
        <div className="modal-header">
          <div dangerouslySetInnerHTML={{__html: model.header}}/>
        </div>
        <div className="clearfix"></div>
        <div className="modal-body">
          <div dangerouslySetInnerHTML={{__html: model.message}}/>
          <form onSubmit={handleSubmit}>

            {model.fields.map(f => {
              switch (f.dataType) {
                default:
                  return <Field {...toFormFieldProps(f)} component={RadioGroupField}
                                items={[{key: "true", value: "Yes"}, {key: "false", value: "No"}]}/>
              }
            })}

            <button type="submit" className="btn btn-primary btn-lg btn-block">
              <i className="fa fa-check-circle"></i> Estimate my fee
            </button>

          </form>
        </div>
      </div>
    )
  }
}

export const Form = reduxForm({
  form: "EstimateFee",
})(Fields);

