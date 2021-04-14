import React from "react";
import {
  SpecialPageItem as SpecialPageItemModel,
} from "../../../../../model";
import {withStyles} from "@material-ui/core/styles";
import {stubFunction} from "../../../../../common/utils/Components";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";

const styles = theme => ({
  rule: {
    maxWidth: "25vw",
    width: "25vw",
  }
})

interface SpecialPageItemState extends SpecialPageItemModel {
  submitted?: boolean;
}

interface Props {
  classes: any;
  item: SpecialPageItemState;
  index: number;
  onChange: (e, index, key) => any;
}

class SpecialPageItem extends React.PureComponent<Props, any> {
  render() {
    const {classes, item, index, onChange} = this.props;

    return (
      <div key={index}>
        <h6 className="heading mb-1">{item.specialPage}</h6>
        <div>
          <EditInPlaceField
            label={`From (${item.matchType.toLowerCase()})`}
            name={`from-${index}`}
            id={`from-${index}`}
            meta={{
              invalid: item.error,
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
        </div>
      </div>
    );
  }
}

export default (withStyles(styles)(SpecialPageItem));
