import React from 'react';
import classnames from 'classnames';
import {RedirectItem as RedirectItemModel} from "../../../../../model";
import CustomButton from "../../../../../common/components/CustomButton";
import {Grid, TextField} from "@material-ui/core";

interface RedirectItemState extends RedirectItemModel {
  submitted?: boolean;
}

interface Props {
  item: RedirectItemState;
  index: number;
  onChange: (e, index, key) => any;
  onRemove: (index) => any;
}

class RedirectItem extends React.Component<Props, any> {
  shouldComponentUpdate(newProps: Props) {
    return this.props.item !== newProps.item;
  }

  render() {
    const {item, index, onChange, onRemove} = this.props;

    return (
      <Grid key={index}>
        <label className="mr-1">From</label>
        <TextField
          className={classnames({invalid: (item.submitted && item.to && !item.from) || item.error})}
          type="text"
          name={`from-${index}`}
          id={`from-${index}`}
          value={item.from}
          onChange={e => onChange(e, index, 'from')}
        />

        <label className="ml-1 mr-1">To</label>
        <TextField
          className={classnames({invalid: (item.submitted && !item.to && item.from) || item.error})}
          type="text"
          name={`to-${index}`}
          id={`to-${index}`}
          value={item.to}
          onChange={e => onChange(e, index, 'to')}
        />

        <CustomButton
          styleType="delete"
          onClick={() => onRemove(index)}
          styles="ml-1"
        >
          Remove
        </CustomButton>

        {item.error &&
          <div className="form-inline">
            <label className="error">{item.error}</label>
          </div>
        }
      </Grid>
    );
  }
}

export default RedirectItem;

