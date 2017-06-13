import * as React from "react";
import {isNil} from "lodash";

export interface Props {
  name: string
  title: string
  selected: boolean
  error: string
  warning: string
  onChange: (boolean) => void
}

export class ItemWrapper extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {name, title, selected, error, warning, onChange, children} = this.props;

    return (
      <div className="col-xs-16 col-md-17 enrolmentInfo">
        <label>
          <input className="enrolmentSelect"
                 type="checkbox"
                 name={name}
                 onChange={ onChange }
                 checked={selected } disabled={!isNil(error)}/>
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