import * as React from "react";
import Rodal from "rodal";
import "rodal/lib/rodal.css";
import {Props as FeeProps} from "./FeesRangeComponent";
import {toFormFieldProps} from "../../../components/form/FieldFactory";
import {Field, reduxForm} from "redux-form";
import {RadioGroupField} from "../../../components/form/RadioGroupField";
import { InjectedFormProps } from 'redux-form/lib/reduxForm';


interface State {
  visible: boolean;
}

export class Dialog extends React.Component<FeeProps, State> {

  constructor(props) {
    super(props);
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
      this.hide();
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
    );
  }

  questions() {
    const onSubmit = (values): void => {
      this.props.onSubmit(values);
      this.hide();
    };
    return (
      <Form {...this.props} onSubmit={onSubmit}/>
    );
  }
}


export class Fields extends React.Component<InjectedFormProps & { model?: any }, any> {
  render() {
    const {model, handleSubmit} = this.props;
    return (
      <div className="modal-content">
        <div className="modal-header">
          <div dangerouslySetInnerHTML={{__html: model.header}}/>
        </div>
        <div className="clearfix"></div>
        <div className="modal-body">
          <div dangerouslySetInnerHTML={{__html: model.message}}/>
          <form onSubmit={handleSubmit}>
            {model.fields.map((f,i) => {
              switch (f.dataType) {
                default:
                  return <Field {...toFormFieldProps(f,i)} component={RadioGroupField}
                                items={[{key: "true", value: "Yes"}, {key: "false", value: "No"}]}/>;
              }
            })}

            <button type="submit" className="btn btn-primary btn-lg btn-block">
              <i className="fa fa-check-circle"></i> Estimate my fee
            </button>

          </form>
        </div>
      </div>
    );
  }
}

export const Form = reduxForm({
  form: "EstimateFee",
})(Fields);

