import * as React from "react";
import classnames from "classnames";
import {metaFrom} from "./FieldsUtils";

interface Props {
  label: string,
  required: boolean
  children: any
  touched: boolean
  error: string
  warning: string
}

class Wrapper extends React.Component<any, any> {
  render() {
    const {label, input, required, children} = this.props;
    const {error, warning} = metaFrom(this.props);
    const divClass = classnames("form-group field-error-tooltip", error && "has-error", warning && "has-warning");
    return (
      <div className={divClass}>
        {label &&
        <label className="control-label" htmlFor={input.name}>
          <span dangerouslySetInnerHTML={{__html: label}}/>
          <span>
              {required && <em title="This field is required">*</em>}
            </span>
        </label> }
        {children}
        {this.renderMessage()}
      </div>
    )
  }

  renderMessage() {
    const {touched, error, warning, active} = metaFrom(this.props);
    const message = error ? error : warning;
    return (message && <span className="field-error-tooltip-text help-block" dangerouslySetInnerHTML={{__html: message}}/>);
  }

}

export default Wrapper;
