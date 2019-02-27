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
  SpecialPage,
  URLMatchRule,
} from "../../../../../model";
import {Checkbox} from "../../../../../common/components/Checkbox";
import {URLMatchRuleKeys} from "../reducers/State";

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
            <Label>From</Label>
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

          <Label>Match Type</Label>
          <Input
            type="select"
            name={`matchType-${index}`}
            id={`matchType-${index}`}
            value={item.matchType}
            onChange={e => onChange(e, index, "matchType")}
          >
            {Object.keys(URLMatchRule).map((i, n) => (
              <option key={n} value={URLMatchRule[i]}>
                {URLMatchRule[i]}
              </option>
            ))}
          </Input>

          {/*<FormGroup className="ml-3">*/}
            {/*<Checkbox*/}
              {/*label="Exact"*/}
              {/*name={`matchType-${index}`}*/}
              {/*checked={item.matchType === URLMatchRuleKeys[0]}*/}
              {/*onChange={e => onMatchTypeChange(e, index, "matchType")}*/}
            {/*/>*/}
          {/*</FormGroup>*/}
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
