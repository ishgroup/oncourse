/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Diff, FundingSource, SearchQuery, Sorting, Tag } from "@api/model";
import Drawer from "@material-ui/core/Drawer";
import Grid from "@material-ui/core/Grid";
import IconButton from "@material-ui/core/IconButton";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import withStyles from "@material-ui/core/styles/withStyles";
import Typography from "@material-ui/core/Typography";
import { Help } from "@material-ui/icons";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { change, Field, reduxForm } from "redux-form";
import { PreferencesState } from "../../../../../containers/preferences/reducers/state";
import { getEntityTags } from "../../../../../containers/tags/actions";
import { State } from "../../../../../reducers/state";
import Button from "../../../buttons/Button";
import DataTypeRenderer from "../../../form/DataTypeRenderer";
import FormField from "../../../form/form-fields/FormField";
import { validateTagsList } from "../../../form/simpleTagListComponent/validateTagsList";
import { bulkChangeRecords } from "../../actions";
import bottomDrawerStyles from "../bottomDrawerStyles";
import SelectionSwitcher from "../share/SelectionSwitcher";
import { BulkEditField, getBulkEditFields } from "./utils";
import { EntityName } from "../../../../../model/entities/common";

interface BulkEditProps {
  rootEntity: EntityName;
  showBulkEditDrawer: boolean;
  toggleBulkEditDrawer: () => void;
  selection: string[];
  count: number;
  submitting?: boolean;
  searchQuery?: SearchQuery;
  sort?: Sorting[];
  classes?: any;
  dispatch?: Dispatch;
  getBulkEditList?: (entityName: string) => void;
  handleSubmit?: any;
  onSave?: any;
  sidebarWidth: number;
  values?: any;
  invalid?: boolean;
  validating?: boolean;
  rows?: any;
  columns?: any;
  doBulkEdit?: (entityBulkEdit, diff: Diff) => void;
  manualLink?: string;
  hasAql?: boolean;
  contracts?: FundingSource[];
  dataCollectionRules: PreferencesState["dataCollectionRules"];
  entityTags?: {[key: string]: Tag[]};
  getEntityTags?: (entity: string) => void;
  showConfirm?: any;
}

const BulkEditForm: React.FC<BulkEditProps> = props => {
  const {
    showBulkEditDrawer,
    toggleBulkEditDrawer,
    sidebarWidth,
    classes,
    selection,
    count,
    submitting,
    validating,
    rootEntity,
    getEntityTags,
    entityTags,
    invalid,
    handleSubmit,
    searchQuery,
    doBulkEdit,
    manualLink,
    dispatch
  } = props;

  const [selectAll, setSelectAll] = useState(false);
  const [bulkEditFields, setBulkEditFields] = useState(null);
  const [selectedKeyCode, setSelectedKeyCode] = useState(null);
  const [usedKeys, setUsedKeys] = useState({});

  useEffect(() => {
    if (rootEntity) {
      getEntityTags(rootEntity);
    }

    const fields = getBulkEditFields(rootEntity);
    setBulkEditFields(fields);

    if (fields && fields.length) {
      setSelectedKeyCode(fields[0].keyCode);
    }
  }, [rootEntity]);

  const onClose = useCallback(() => {
    setSelectAll(false);
    toggleBulkEditDrawer();
  }, []);

  const onSave = values => {
    const ids = selectAll ? null : selection.map(s => Number(s));
    const searchObj = selectAll ? searchQuery : {};
    const diff = {
      [selectedKeyCode]: values[selectedKeyCode]
    };

    if (selectedKeyCode === "bulkTag" || selectedKeyCode === "bulkUntag") {
      diff[selectedKeyCode] = diff[selectedKeyCode].map(tag => tag.id).join(",");
    }

    doBulkEdit(rootEntity, {
      ids,
      diff,
      search: searchObj.search,
      filter: searchObj.filter,
      tagGroups: searchObj.tagGroups
    });
  };

  const getBulkEditFieldData = useCallback(():BulkEditField => {
    if (!bulkEditFields || !selectedKeyCode) return null;
    return bulkEditFields.find(field => field.keyCode === selectedKeyCode);
  }, [bulkEditFields, selectedKeyCode]);

  const tags = useMemo(
    () => {
      const tags = entityTags && rootEntity && entityTags[rootEntity];
      return tags || [];
    },
    [entityTags, rootEntity]
  );

  const validateTagList = useCallback((value, allValues, props) => validateTagsList(tags, value, allValues, props), [tags]);

  const BulkEditFieldRendered = useMemo(() => {
    if (!selectedKeyCode) {
      return null;
    }

    const field = getBulkEditFieldData();
    let fieldProps = {};

    if (field.hasOwnProperty("defaultValue") && !usedKeys[field.keyCode]) {
      setUsedKeys({ ...usedKeys, [field.keyCode]: true });
      dispatch(change("BulkEditForm", field.keyCode, field.defaultValue));
    }

    // eslint-disable-next-line default-case
    switch (field.type) {
      case "Select": {
        fieldProps = {
          items: field.propsItemKey ? props[field.propsItemKey] || [] : field.items,
          selectValueMark: field.selectValueMark,
          selectLabelMark: field.selectLabelMark,
          fieldClasses: {
            text: classes.text,
            label: classes.customLabel,
            placeholder: classes.placeholder
          }
        };
        break;
      }
      case "Switch":
      case "Checkbox": {
        fieldProps = {
          classes: { label: classes.label },
          uncheckedClass: classes.label,
          color: "primary"
        };
        break;
      }
      case "Tag": {
        fieldProps = {
          fieldClasses: {
            text: classes.text,
            label: classes.customLabel,
            placeholder: classes.placeholder,
            listbox: classes.listbox
          }
        };
        break;
      }
      default: {
        fieldProps = {
          ...(field.type === "Money" ? { stringValue: true } : {}),
          fieldClasses: {
            text: classes.text,
            label: classes.customLabel,
            placeholder: classes.placeholder
          }
        };
      }
    }

    if (field.componentProps) {
      fieldProps = {
        ...fieldProps,
        ...field.componentProps
      };
    }

    return field.type === "Tag"
      ? (
        <>
          <Typography variant="body2" color="inherit" className="pb-1" classes={{ root: classes.listItemsText }}>
            {`The following tags will be ${field.keyCode === "bulkTag" ? "added to" : "removed from"} the records...`}
          </Typography>
          <FormField
            type="tags"
            name={field.keyCode}
            tags={tags}
            validate={validateTagList}
            {...fieldProps}
          />
        </>
      )
      : (
        <Field
          label={field.name}
          name={field.keyCode}
          type={field.type}
          component={DataTypeRenderer}
          fullWidth
          validate={field.validate}
          {...fieldProps}
        />
      );
  }, [entityTags, rootEntity, usedKeys, validateTagList, bulkEditFields, selectedKeyCode]);

  return (
    <Drawer
      anchor="bottom"
      open={showBulkEditDrawer}
      onClose={onClose}
      classes={{ paper: classes.exportContainer }}
      PaperProps={{
        style: {
          left: window.innerWidth >= 1024 ? sidebarWidth : 0
        }
      }}
    >
      <Grid container className={classes.content}>
        <Grid container className={classes.header} wrap="nowrap" alignItems="center">
          <Grid item xs={2}>
            <Typography variant="body2" className={classes.headerText}>
              Bulk edit
            </Typography>
          </Grid>
          <Grid item xs className="centeredFlex">
            <SelectionSwitcher
              selectedRecords={selection.length}
              allRecords={count}
              selectAll={selectAll}
              setSelectAll={setSelectAll}
              disabled={count === null || submitting || validating}
            />

            <div className="flex-fill" />

            <IconButton className={classes.headerText} href={manualLink} target="_blank">
              <Help />
            </IconButton>
          </Grid>
        </Grid>
        <Grid container className={classes.body} wrap="nowrap" spacing={3}>
          <Grid item zeroMinWidth className={classes.menuColumn}>
            <List disablePadding className={classes.list}>
              {bulkEditFields
                && bulkEditFields.map(field => {
                  const { label, keyCode } = field;

                  return (
                    <ListItem
                      button
                      classes={{
                        root: classes.listItems,
                        selected: classes.listItemsSelected
                      }}
                      key={keyCode}
                      selected={selectedKeyCode === keyCode}
                      onClick={() => setSelectedKeyCode(keyCode)}
                      disableRipple
                    >
                      <Typography variant="body2" color="inherit" classes={{ root: classes.listItemsText }}>
                        {label}
                      </Typography>

                      {selectedKeyCode === keyCode && <div className={classes.menuCorner} />}
                    </ListItem>
                  );
                })}
            </List>
          </Grid>
          <Grid item xs className={classes.menuColumn}>
            <form autoComplete="off" onSubmit={handleSubmit(onSave)} className={classes.form}>
              <Grid container className={classes.formContent}>
                <Grid item xs={12}>
                  {BulkEditFieldRendered}
                </Grid>
              </Grid>

              <Grid item xs={12} className={classes.closeShareButtons}>
                <Button className={classes.closeButton} onClick={onClose} variant="text">
                  Cancel
                </Button>
                <Button
                  disabled={invalid || validating || !selectedKeyCode}
                  rootClasses={classes.shareButton}
                  type="submit"
                  loading={submitting || validating}
                >
                  Make changes
                </Button>
              </Grid>
            </form>
          </Grid>
        </Grid>
      </Grid>
    </Drawer>
  );
};

const mapStateToProps = (state: State) => ({
  submitting: state.fetch.pending,
  searchQuery: state.list.searchQuery,
  contracts: state.export.contracts,
  dataCollectionRules: state.preferences.dataCollectionRules,
  entityTags: state.tags.entityTags
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  doBulkEdit: (entity, diff: Diff) => dispatch(bulkChangeRecords(entity, diff)),
  getEntityTags: (entity: string) => dispatch(getEntityTags(entity))
});

export default reduxForm({
  form: "BulkEditForm"
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(bottomDrawerStyles)(BulkEditForm))) as any;
