import React from 'react';
import classnames from 'classnames';
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {RedirectItem as RedirectItemModel} from "../../../../../model";

interface RedirectItemState extends RedirectItemModel {
  submitted?: boolean;
}

interface Props {
  item: RedirectItemState;
  index: number;
  onChange: (e, index, key) => any;
  onRemove: (index) => any;
}

export class RedirectItem extends React.Component<Props, any> {

  shouldComponentUpdate(newProps: Props) {
    return this.props.item !== newProps.item;
  }

  render() {
    const {item, index, onChange, onRemove} = this.props;

    return (
      <FormGroup key={index}>
        <div className="form-inline rule">

          <Label>From</Label>
          <Input
            className={classnames({invalid: (item.submitted && item.to && !item.from) || item.error})}
            type="text"
            name={`from-${index}`}
            id={`from-${index}`}
            value={item.from}
            onChange={e => onChange(e, index, 'from')}
          />

          <Label>To</Label>
          <Input
            className={classnames({invalid: (item.submitted && !item.to && item.from) || item.error})}
            type="text"
            name={`to-${index}`}
            id={`to-${index}`}
            value={item.to}
            onChange={e => onChange(e, index, 'to')}
          />

          <Button
            color="danger"
            className="outline"
            onClick={() => onRemove(index)}
          >
            <span className="icon icon-delete"/>
            Remove
          </Button>
        </div>

        {item.error &&
          <div className="form-inline">
            <label className="error">{item.error}</label>
          </div>
        }
      </FormGroup>
    );
  }
}

