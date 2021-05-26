import React, {useEffect, useState} from 'react';
import {withStyles} from "@material-ui/core/styles";
import {IconButton, Paper, TextField, Typography} from "@material-ui/core";
import {connect} from "react-redux";
import {Dispatch} from "redux";
import clsx from "clsx";
import {AddCircle} from "@material-ui/icons";
import {FixedSizeList as List} from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import {getRedirectSettings, setRedirectSettings} from "./actions";
import RedirectComp from "./components/RedirectItem";
import {RedirectSettingsState} from "./reducers/State";
import {State} from "../../../../reducers/state";
import CustomButton from "../../../../common/components/CustomButton";
import {RedirectItem as RedirectItemModel} from "../../../../../../build/generated-sources";

const styles: any = theme => ({
  redirectWrapper: {
    maxHeight: "calc(100vh - 30px)",
    boxSizing: "border-box",
    overflow: "hidden",
  },
  saveButton: {
    marginLeft: theme.spacing(2),
  },
});

interface RedirectItemState extends RedirectItemModel {
  index?: number;
  submitted?: boolean;
}

interface Props {
  classes: any;
  onInit: () => any;
  onSave: (settings) => any;
  redirect: RedirectSettingsState;
  fetching: boolean;
}

const Redirect: React.FC<Props> = (
  {
    redirect,
    onInit,
    onSave,
    classes,
    fetching,
  }) => {
  const [rules, setRules] = useState<RedirectItemState[]>(redirect.rules.map(r => ({...r, submitted: false})) || []);
  const [filter, setFilter] = useState("");

  useEffect(() => {
    onInit();
  }, []);

  useEffect(() => {
    setIndexes(redirect.rules)
  }, [redirect.refreshSettings]);

  const checkUniqueRule = (rule, newValue) => {
    const value = rules.find((elem) => elem.index !== rule.index && elem.from === newValue);

    return value ? "The from field must be unique" : null;
  };

  const onChange = (e, index, key) => {
    const updated = rules.map((r, rIndex) => {
      if (rIndex === index) {
        return ({
          ...r,
          [key]: e.target.value,
          submitted: false,
          error: checkUniqueRule(r, e.target.value),
        })
      } else {
        return ({ ...r, submitted: false, })
      }
    });

    setRules(updated);
  };

  const onAddNew = () => {
    setIndexes([{from: '', to: ''}, ...rules]);
  };

  const onSaveHandler = () => {
    const rulesForUpdate = rules
      .filter(rule => rule.from || rule.to)
      .map(rule => ({from: rule.from, to: rule.to, submitted: true}));

    if (rulesForUpdate.filter(rule => (rule.from && !rule.to) || (!rule.from && rule.to)).length) return;

    setRules(rulesForUpdate);
    onSave({rules: rulesForUpdate.map(({from, to}) => ({from, to}))});
  };

  const setIndexes = (rules) => {
    const rulesWithIndex = rules.map((elem, index) => ({...elem, index}));
    setRules(rulesWithIndex);
  }

  const onRemove = index => {
    const updated = [...rules];
    updated.splice(index, 1);
    setIndexes(updated);
  };

  const onChangeFilter = e => {
    setFilter(e.target.value);
  };

  const filteredRules = rules.filter(r => r.from.indexOf(filter) !== -1 || r.to.indexOf(filter) !== -1 || !r.from || !r.to);

  return (
    <Paper className={clsx((fetching && "fetching"), "p-3", classes.redirectWrapper)}>
      <p className="mb-1">
        Add 301 redirects to your website by entering the local path on the left (starting with '/')
        and the destination on the right (either starting with '/' for another local page or starting with
        http/https for redirecting to another website).
      </p>

      {rules && rules.length > 0 &&
        <TextField
          type="text"
          name="filter"
          placeholder="Filter"
          id="filter"
          value={filter}
          onChange={onChangeFilter}
        />
      }

      <div className="mt-2 mb-3">
        <div className={"centeredFlex"}>
          <Typography className="heading">Add new</Typography>
          <IconButton onClick={onAddNew}>
            <AddCircle className="addButtonColor" width={20} />
          </IconButton>

          <CustomButton
            styleType="submit"
            onClick={onSaveHandler}
            styles={classes.saveButton}
          >
            Save
          </CustomButton>
        </div>
      </div>

      <div className={"mt-3"}>
        <div style={{height: (window.innerHeight - 230) < (filteredRules.length * 65) ? window.innerHeight - 230 : filteredRules.length * 65}}>
          <AutoSizer>
            {({height, width}) => (
              <List
                className="List"
                height={height}
                itemCount={filteredRules.length}
                itemData={{onChange, onRemove, items: filteredRules}}
                itemSize={65}
                width={width}
              >
                {RedirectComp}
              </List>
            )}
          </AutoSizer>
        </div>
      </div>
    </Paper>
  );
};

const mapStateToProps = (state: State) => ({
  redirect: state.settings.redirectSettings,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: () => dispatch(getRedirectSettings()),
    onSave: settings => dispatch(setRedirectSettings(settings)),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(Redirect));
