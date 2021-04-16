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
  data: {
    items: RedirectItemState[];
    index: number;
    onChange: (e, index, key) => any;
    onRemove: (index) => any;
  };
  index: number;
  style: any;
}

const areEqual = (prev: Props, cur: Props) => {
  let equal = true;

  const prevItem = prev.data.items[prev.index];
  const curItem = cur.data.items[cur.index];

  for (const key in prevItem) {
    if (prevItem[key] !== curItem[key]) {
      equal = false;
      break;
    }
  }
  return equal;
};

const RedirectItem = React.memo<Props>((
  {
    style,
    data,
    index,
  }) => {

  const {items, onChange, onRemove} = data;

  const item = items[index];

  return (
    <div style={style}>
      <Grid container className="centeredFlex">
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
            noWrap
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
            noWrap
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
    </div>
  );
},areEqual);

export default RedirectItem;

