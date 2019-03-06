import React from "react";
import classnames from "classnames";
import {
  Container,
  Row,
  Col,
  Button,
  Form,
  FormGroup,
  Label,
  Input,
  FormText,
} from "reactstrap";
import {
  SpecialPageItem as SpecialPageItemModel,
  URLMatchRule,
} from "../../../../../model";

interface SpecialPageItemState extends SpecialPageItemModel {
  submitted?: boolean;
}

interface Props {
  item: SpecialPageItemState;
  index: number;
  onChange: (e, index, key) => any;
}

export class SpecialPageItem extends React.PureComponent<Props, any> {
  render() {
    const {item, index, onChange} = this.props;

    return (
      <div key={index}>
        <div className="form-inline">
          <h6 className="mb-0 ml-2">{item.specialPage}</h6>

          <div className="rule ml-2">
            <Label>From ({item.matchType.toLowerCase()})</Label>
            <Input
              className={classnames({
                invalid: item.error,
              })}
              type="text"
              name={`from-${index}`}
              id={`from-${index}`}
              value={item.from}
              onChange={e => onChange(e, index, "from")}
            />
          </div>
        </div>

        {item.error && (
          <div className="form-inline">
            <label className="error">{item.error}</label>
          </div>
        )}
      </div>
    );
  }
}
