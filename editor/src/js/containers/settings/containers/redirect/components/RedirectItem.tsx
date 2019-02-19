import React from 'react';
import classnames from 'classnames';
import {Container, Row, Col, Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';
import {Condition, RedirectItem as RedirectItemModel, SpecialPage, URLMatchRule} from "../../../../../model";

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
        <div className="form-inline">

          <div>
            <div className="rule mb-2">
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
            </div>

            <div className="form-inline">
              <Label>Special Page</Label>
              <Input
                type="select"
                name={`specialPage-${index}`}
                id={`specialPage-${index}`}
                value={item.specialPage}
                onChange={e => onChange(e, index, 'specialPage')}
              >
                {Object.keys(SpecialPage).map( (i,n) => <option key={n} value={SpecialPage[i]}>{SpecialPage[i]}</option>)}
              </Input>

              <Label>Match Type</Label>
              <Input
                type="select"
                name={`matchType-${index}`}
                id={`matchType-${index}`}
                value={item.matchType}
                onChange={e => onChange(e, index, 'matchType')}
              >
                {Object.keys(URLMatchRule).map( (i,n) => <option key={n} value={URLMatchRule[i]}>{URLMatchRule[i]}</option>)}
              </Input>
            </div>
          </div>


          <div className="rule">
            <Button
              color="danger"
              className="btn outline"
              onClick={() => onRemove(index)}
            >
              <span className="icon icon-delete"/>
              Remove
            </Button>
          </div>

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

