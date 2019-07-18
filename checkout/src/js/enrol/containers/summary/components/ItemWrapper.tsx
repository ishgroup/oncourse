import * as React from "react";
import {isNil} from "lodash";
import classnames from "classnames";


const divStyle = {
  width: '2em',
  paddingRight: '0px',
  paddingLeft: '0px'
};

const inputStyle = {
  width: '2em',
  paddingLeft: '5px',
  paddingRight: '5px'
};

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
  quantity?:number;
  onQuantityChange?: (val) => void;
  onQuantityBlur?: (val) => void;
}

export class ItemWrapper extends React.Component<Props, any> {
  public render(): JSX.Element {
    const {name, title, selected, error, warning, onChange, item, contact, children, fullWidth,
      quantity, onQuantityChange, onQuantityBlur } = this.props;

    return (
      <div className={classnames("enrolmentInfo", {"col-xs-16 col-md-17": !fullWidth, "col-md-24": fullWidth})}>

        {selected && quantity  && quantity > 0 ?
          <div className="row">
              <div className="col-xs-1">
                <input className="enrolmentSelect"
                       type="checkbox"
                       name={name}
                       onChange={ onChange.bind(this, item, contact) }
                       checked={selected} disabled={!isNil(error)}/>
              </div>

              <div className="col-xs-2" style={divStyle}>
                <input
                    type="text"
                    className="text-right"
                    name="quantityValue"
                    value={quantity}
                    onChange={e => onQuantityChange(e.target.value)}
                    onBlur={e => onQuantityBlur(e)}
                    style={inputStyle}
                />
              </div>
              <div className="col-xs-10">
                <label>
                { title }
                </label>
              </div>

          </div>
          :
            <label>
              <input className="enrolmentSelect"
                     type="checkbox"
                     name={name}
                     onChange={ onChange.bind(this, item, contact) }
                     checked={selected} disabled={!isNil(error)}/>
              { title }
            </label>
        }
        {warning && (<span dangerouslySetInnerHTML={{__html: warning}}/>)}
        {error && <span className="disabled" dangerouslySetInnerHTML={{__html: error}}/>}
        <br/>
        {children}

      </div>
    );
  }
}
