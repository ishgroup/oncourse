import * as React from "react";
import withStyles from "@mui/styles/withStyles";
import { createStyles } from "@mui/material";
import Checkbox from "@mui/material/Checkbox";
import IconButton from "@mui/material/IconButton";
import Delete from "@mui/icons-material/Delete";
import BookmarkBorder from "@mui/icons-material/BookmarkBorder";
import Tooltip from "@mui/material/Tooltip";
import TextField from "@mui/material/TextField";
import clsx from "clsx";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { Filter } from "@api/model";
import { createCustomFilter, setListSavingFilter } from "../../../actions";
import { SavingFilterState } from "../../../../../../model/common/ListView";
import { validateSingleMandatoryField, validateTagName } from "../../../../../utils/validation";
import { SIMPLE_SEARCH_QUOTES_REGEX, SIMPLE_SEARCH_REGEX } from "../../../../../../constants/Config";

const styles = theme =>
  createStyles({
    checkbox: {
      height: "1.3em",
      width: "1.3em",
      marginLeft: "-7px"
    },
    root: {
      display: "flex",
      "&:hover $deleteButton": {
        visibility: "visible"
      }
    },
    deleteButton: {
      visibility: "hidden"
    },
    iconButton: {
      fontSize: theme.spacing(3),
      height: "30px",
      width: "30px",
      padding: theme.spacing(0.5)
    },
    textField: {
      width: "95px"
    },
    label: {
      marginRight: 0
    }
  });

const validationArr = [validateSingleMandatoryField, validateTagName];

export class StubFilterItem extends React.PureComponent<any, any> {
  private inputNode: HTMLInputElement;

  state = {
    filterName: "",
    error: true,
    errorText: validateSingleMandatoryField("")
  };

  componentDidMount() {
    setTimeout(() => {
      this.inputNode.focus();
    }, 400);
  }

  setInputNode = node => {
    this.inputNode = node;
  };

  onFilterNameChange = e => {
    const filterName = e.target.value;
    let errorText = null;

    for (const f of validationArr) {
      if (!errorText) {
        errorText = f(filterName);
      } else {
        break;
      }
    }

    this.setState({
      filterName,
      error: Boolean(errorText),
      errorText
    });
  };

  onSaveFilter = () => {
    const { saveFilter, rootEntity, filterEntity, savingFilter } = this.props;
    const { filterName } = this.state;

    let expression = savingFilter.aqlSearch;

    if (expression.match(SIMPLE_SEARCH_QUOTES_REGEX)) {
      expression = `~${expression}`;
    } else if (expression.match(SIMPLE_SEARCH_REGEX)) {
      expression = `~"${expression}"`;
    }

    const filter: Filter = {
      name: filterName,
      entity: filterEntity || rootEntity,
      expression,
      showForCurrentOnly: savingFilter.isPrivate
    };

    saveFilter(filter, filterEntity || rootEntity);
  };

  clearFilter = () => {
    this.props.setSavingFilter(null);
  };

  render() {
    const { classes } = this.props;
    const { filterName, error, errorText } = this.state;

    return (
      <div className={classes.root}>
        <Checkbox disabled className={classes.checkbox} color={"secondary"} />
        <TextField
          onChange={this.onFilterNameChange}
          helperText={errorText}
          error={error}
          inputRef={this.setInputNode}
          value={filterName}
        />

        <Tooltip title="Save Filter">
          <div>
            <IconButton onClick={this.onSaveFilter} className={classes.iconButton} disabled={error}>
              <BookmarkBorder fontSize="inherit" color={error ? "disabled" : "secondary"} />
            </IconButton>
          </div>
        </Tooltip>

        <Tooltip title="Delete Filter">
          <IconButton onClick={this.clearFilter} className={clsx(classes.deleteButton, classes.iconButton)}>
            <Delete fontSize="inherit" color="secondary" />
          </IconButton>
        </Tooltip>
      </div>
    );
  }
}

export const StubFilterItemBase = withStyles(styles)(StubFilterItem);

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    saveFilter: (filter: Filter, rootEntity: string) => dispatch(createCustomFilter(filter, rootEntity)),
    setSavingFilter: (savingFilter?: SavingFilterState) => dispatch(setListSavingFilter(savingFilter))
  };
};

export default connect<any, any, any>(
  null,
  mapDispatchToProps
)(StubFilterItemBase);
