import * as React from "react";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {FieldLabel} from "./FieldLabel";
import {HintText} from "./HintText";
import classnames from "classnames";
import {MouseHover, WrappedMouseHoverProps} from "./MouseHover";
import {replaceWithNl} from "../../common/utils/HtmlUtils";

export class CheckboxField extends React.Component<any, any> {
  render() {
    return (
      <MouseHover component={checkboxFieldComponent} componentProps={this.props}/>
    );
  }
}

export const checkboxFieldComponent = (props: CheckboxFieldProps) => {
  const {input, label, classes, required, hint, placeholder} = props;

  return (
    <div >
      {label &&
        <FieldLabel
          name={input.name}
          label={label}
          required={required}
          className={classnames({["with-hint"]: Boolean(hint)})}
        >
            <HintText {...props}/>
            {placeholder && <span
              className={"label-placeholder"}
              style={{whiteSpace: "pre-line"}}
            >{replaceWithNl(placeholder)}</span>}
        </FieldLabel>
      }

      <input {...input} checked={input.value} className={classes} type="checkbox"/>

    </div>
  );
};

export interface CheckboxFieldProps extends Partial<WrappedFieldProps>, CommonFieldProps, WrappedMouseHoverProps {
}
