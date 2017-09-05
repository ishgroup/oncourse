import * as React from "react";
import classnames from "classnames";
import {metaFrom} from "./FieldsUtils";
import ReactTooltip from "react-tooltip";

interface Props {
  label: string;
  required: boolean;
  children: any;
  touched: boolean;
  error: string;
  warning: string;
}

class Wrapper extends React.Component<any, any> {
  render() {
    const {label, required, children} = this.props;

    let input = this.props.input;
    if (!input) {
      input = {
        name: this.props.name,
      };
    }

    const {error, warning} = metaFrom(this.props);
    const divClass = classnames("form-group", error && "has-error", warning && "has-warning");
    const requiredSign = required ? '<em title="This field is required">*</em>' : '';

    return (
      <div className={divClass}>
        {label &&
        <label className="control-label" htmlFor={input.name}>
          <span dangerouslySetInnerHTML={{__html: label + requiredSign}}/>
        </label> }
        <span className="input-field" data-for={input.name} data-tip>
          {children}
        </span>
        {this.renderMessage(input.name)}
      </div>
    );
  }

  renderMessage(id: string) {
    const {touched, error, warning, active} = metaFrom(this.props);
    const message = error ? error : warning;
    const type = error ? "error" : (warning ? "warning" : null);

    return (message && touched &&
      <ReactTooltip id={id} place="right" type={type} effect="solid">
        <span className="help-block" dangerouslySetInnerHTML={{__html: message}}/>
      </ReactTooltip>
    );
  }

}

export default Wrapper;
