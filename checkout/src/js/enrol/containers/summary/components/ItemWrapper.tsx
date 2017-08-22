import * as React from "react";
import {isNil} from "lodash";

export interface Props {
  name: string;
  title: string;
  selected: boolean;
  error: string;
  warning: string;
  item: any;
  contact: any;
  onChange: (item, contact) => void;
}

export class ItemWrapper extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {name, title, selected, error, warning, onChange, item, contact, children} = this.props;

    return (
      <div className="col-xs-16 col-md-17 enrolmentInfo">
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
