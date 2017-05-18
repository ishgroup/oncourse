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
    const {label, required, children} = this.props;
    const {error, warning} = metaFrom(this.props);

    const divClass = classnames("form-group", error && "has-error", warning && "has-warning");
    return (
      <div className={divClass}>
        <label className="control-label" htmlFor={name}>
          <span dangerouslySetInnerHTML={{__html: label}}/>
          <span>
            {required && <em title="This field is required">*</em>}
          </span>
        </label>
        {children}
        {this.renderMessage()}
      </div>
    )
  }

  renderMessage() {
    const {touched, error, warning, active} = metaFrom(this.props);
    if (active) {
      return ((error && <span className="help-block" dangerouslySetInnerHTML={{__html: error}}/>) ||
      (warning && <span className="help-block" dangerouslySetInnerHTML={{__html: warning}}/>))
    } else {
      return null;
    }
  }
}

export default Wrapper;
