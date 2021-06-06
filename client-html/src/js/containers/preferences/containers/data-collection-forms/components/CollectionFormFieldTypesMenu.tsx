import * as React from "react";
import {
 withStyles, Typography, Menu, MenuItem
} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { DataCollectionType, FieldType } from "@api/model";

import createStyles from "@material-ui/core/styles/createStyles";
import Fab from "@material-ui/core/Fab";
import { getDataCollectionFormFieldTypes } from "../../../actions";
import { State } from "../../../../../reducers/state";

const styles = () => createStyles({
  menu: {
    marginLeft: "52px"
  },
  customFieldLabel: {
    fontSize: "0.9em"
  }
});

interface Props {
  fieldTypes?: FieldType[];
  items: any;
  getTypes?: (id: string) => void;
  addField: (type: string) => void;
  addHeading: () => void;
  formType: string;
  className: string;
  classes?: any;
}

class CollectionFormFieldTypesMenu extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      anchorEl: null
    };
  }

  componentDidMount() {
    this.props.getTypes(this.props.formType);
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.fieldTypes) {
      this.setState({
        filteredTypes: this.filterFieldTypes(nextProps.fieldTypes, nextProps.items)
      });
    }
    if (nextProps.formType !== this.props.formType) {
      this.props.getTypes(nextProps.formType);
    }
  }

  handleAddFieldClick = e => {
    this.setState({ anchorEl: e.currentTarget });
  };

  handleAddFieldClose = () => {
    this.setState({ anchorEl: null });
  };

  filterFieldTypes = (fieldTypes, items) => {
    const filtered = JSON.parse(JSON.stringify(fieldTypes));
    items.forEach(item => {
      const itemAddedIndex = item.baseType === "field" && filtered.findIndex(field => field.uniqueKey === item.type.uniqueKey);
      if (itemAddedIndex !== false && itemAddedIndex !== -1) {
        filtered.splice(itemAddedIndex, 1);
      }
    });

    filtered.sort((a, b) => (a.label[0].toLowerCase() > b.label[0].toLowerCase() ? 1 : -1));

    return filtered;
  }

  render() {
    const { anchorEl, filteredTypes } = this.state;
    const { addField, addHeading, classes } = this.props;

    return (
      <>
        <Menu
          id="field-types-menu"
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={this.handleAddFieldClose}
          className={classes.menu}
        >
          <MenuItem onClick={() => addHeading()}>
            <Typography className="heading">Heading</Typography>
          </MenuItem>
          {filteredTypes
            && filteredTypes.map(val => (
              <MenuItem key={val.uniqueKey} onClick={() => addField(val)}>
                {val.formattedLabel.includes("[") ? (
                  <>
                    {val.formattedLabel.split("[")[0]}
                    {" "}
                    <span className={`iconColor flex-fill text-end ${classes.customFieldLabel}`}>
                      [
                      {val.formattedLabel.split("[")[1]}
                    </span>
                  </>
                ) : (
                  val.formattedLabel
                )}
              </MenuItem>
            ))}
        </Menu>
        <Fab
          type="button"
          size="small"
          color="primary"
          classes={{
            sizeSmall: "appBarFab"
          }}
          aria-owns={anchorEl ? "field-types-menu" : null}
          aria-haspopup="true"
          onClick={this.handleAddFieldClick}
        >
          <AddIcon />
        </Fab>
      </>
    );
  }
}

const decorateFeildTypes = types => (
    types
    && types.map(item => ({
      uniqueKey: item.uniqueKey,
      label: item.label,
      formattedLabel:
        item.uniqueKey.split(".")[0] === "customField"
          ? `${item.label} [${item.uniqueKey.split(".")[1].capitalize() === 'Article' ? 'Product' : item.uniqueKey.split(".")[1].capitalize()}]`
          : item.label
    }))
  );

const mapStateToProps = (state: State) => ({
  fieldTypes: decorateFeildTypes(state.preferences.dataCollectionFormFieldTypes)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getTypes: (type: DataCollectionType) => dispatch(getDataCollectionFormFieldTypes(type))
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(withStyles(styles)(CollectionFormFieldTypesMenu));
