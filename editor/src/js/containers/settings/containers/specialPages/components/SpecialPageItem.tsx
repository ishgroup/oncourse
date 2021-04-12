import React from "react";
import classnames from "classnames";
import {TextField} from "@material-ui/core";
import {
  SpecialPageItem as SpecialPageItemModel,
  URLMatchRule,
} from "../../../../../model";
import {withStyles} from "@material-ui/core/styles";
import clsx from "clsx";

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
        <h6 className="mb-1">{item.specialPage}</h6>

        <div className={clsx(classes.rule, "d-flex", "align-items-end")}>
          <label className="mr-1">From ({item.matchType.toLowerCase()})</label>
          <TextField
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

        {item.error && (
          <div>
            <label className="error">{item.error}</label>
          </div>
        )}
      </div>
    );
  }
}

export default (withStyles(styles)(SpecialPageItem));
