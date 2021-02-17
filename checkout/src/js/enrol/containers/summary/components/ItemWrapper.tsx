import * as React from "react";
import { isNil } from "lodash";
import classnames from "classnames";
import { CSSProperties } from "react";

const styles: CSSProperties = {
  divStyle: {
    width: '2em',
    paddingRight: '0px',
    paddingLeft: '0px'
  },
  inputStyle: {
    width: '2em',
    paddingLeft: '5px',
    paddingRight: '5px',
    margin: "0 8px 0 0"
  },
  enrolmentSelect: {
    position: "relative",
    marginRight: "8px"
  },
  quantity: {
    display: 'flex',
    alignItems: "center",
    margin: "0 -19px"
  }
}


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
  quantity?: number;
  onQuantityChange?: (val) => void;
  onQuantityBlur?: (val) => void;
  readonly?: boolean;
}

export class ItemWrapper extends React.Component<Props, any> {
  public render(): JSX.Element {
    const { name, title, selected, error, warning, onChange, item, contact, children, fullWidth, readonly,
      quantity, onQuantityChange, onQuantityBlur } = this.props;

    return (
      <div className={classnames("enrolmentInfo", { "col-xs-16 col-md-16": !fullWidth, "col-md-24": fullWidth })}>

        {selected && quantity && quantity > 0 ?
          <div style={styles.quantity}>
            {!readonly && <div style={styles.enrolmentSelect}>
              <input
                type="checkbox"
                name={name}
                onChange={onChange.bind(this, item, contact)}
                checked={selected}
                disabled={!isNil(error) || item.allowRemove === false} />
            </div>}

            {!readonly && <input
              type="text"
              name="quantityValue"
              value={quantity}
              onChange={e => onQuantityChange(e.target.value)}
              onBlur={e => onQuantityBlur(e)}
              style={styles.inputStyle}
            />}

            <label>
              {title}
            </label>

          </div>
          :
          <label style={{ position: "relative" }}>
            {!readonly &&
            <input
               className="enrolmentSelect"
               type="checkbox"
               name={name}
               onChange={onChange.bind(this, item, contact)}
               checked={selected}
               disabled={!isNil(error) || item.allowRemove === false}
               style={{
                 position: "absolute",
                 left: "-19px",
                 margin: 0,
                 fontSize: "0.8rem"
               }}
            />}
            {title}
          </label>
        }
        {warning && (<span dangerouslySetInnerHTML={{ __html: warning }} />)}
        {error && <div className="disabled" dangerouslySetInnerHTML={{ __html: error }} />}
        <br />
        {children}
      </div>
    );
  }
}
