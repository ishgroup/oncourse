import React from 'react';
import {Grid} from "@material-ui/core";
import {RedirectItem as RedirectItemModel} from "../../../../../model";
import CustomButton from "../../../../../common/components/CustomButton";
import {stubFunction} from "../../../../../common/utils/Components";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";

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
      <Grid container key={index} className="centeredFlex">
        <Grid item xs={5}>
          <EditInPlaceField
            label="From"
            name={`from-${index}`}
            id={`from-${index}`}
            meta={{
              invalid: (item.submitted && item.to && !item.from) || item.error,
              error: item.error,
            }}
            input={{
              onChange: e => onChange(e, index, 'from'),
              onBlur: stubFunction,
              onFocus: stubFunction,
              value: item.from,
            }}
            fullWidth
        />
        </Grid>
        <Grid item xs={5}>
          <EditInPlaceField
            label="To"
            name={`to-${index}`}
            id={`to-${index}`}
            meta={{
              invalid: (item.submitted && !item.to && item.from) || item.error,
              error: item.error,
            }}
            input={{
              onChange: e => onChange(e, index, 'to'),
              onBlur: stubFunction,
              onFocus: stubFunction,
              value: item.to,
            }}
            fullWidth
          />
        </Grid>
        <Grid item>
          <CustomButton
            styleType="delete"
            onClick={() => onRemove(index)}
            styles="ml-1"
          >
            Remove
          </CustomButton>
        </Grid>
      </Grid>
    );
  }
}

export default RedirectItem;

