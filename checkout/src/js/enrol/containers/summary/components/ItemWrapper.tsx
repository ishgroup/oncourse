import * as React from "react";
import {isNil} from "lodash";
import classnames from "classnames";

export interface Props {
  name: string;
  title: any;
  selected: boolean;
  error: string;
  warning: string;
  item: any;
  contact: any;
  onChange: (item, contact) => void;
  fullWidth?: boolean;
}

export class ItemWrapper extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {name, title, selected, error, warning, onChange, item, contact, children, fullWidth} = this.props;

    return (
      <div className={classnames("enrolmentInfo", {"col-xs-16 col-md-17": !fullWidth, "col-md-24": fullWidth})}>
        <label>
          <input className="enrolmentSelect"
                 type="checkbox"
                 name={name}
                 onChange={ onChange.bind(this, item, contact) }
                 checked={selected} disabled={!isNil(error)}/>
          { title }
        </label>
        {warning && (<span dangerouslySetInnerHTML={{__html: warning}}/>)}
        {error && <span className="disabled" dangerouslySetInnerHTML={{__html: error}}/>}
        <br/>
        {children}
      </div>
    );
  }
}
