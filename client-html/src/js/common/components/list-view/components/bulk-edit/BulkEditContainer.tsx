/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import {
 Diff, FundingSource, SearchQuery, Sorting, Tag 
} from "@api/model";
import Drawer from "@mui/material/Drawer";
import Grid from "@mui/material/Grid";
import IconButton from "@mui/material/IconButton";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import withStyles from "@mui/styles/withStyles";
import Typography from "@mui/material/Typography";
import { Help } from "@mui/icons-material";
import React, {
 useEffect, useMemo, useState
} from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
 change, Field, reduxForm, reset 
} from "redux-form";
import Button from "@mui/material/Button";
import LoadingButton from "@mui/lab/LoadingButton";
import { PreferencesState } from "../../../../../containers/preferences/reducers/state";
import { getEntityTags } from "../../../../../containers/tags/actions";
import { State } from "../../../../../reducers/state";
import DataTypeRenderer from "../../../form/DataTypeRenderer";
import FormField from "../../../form/formFields/FormField";
import { bulkChangeRecords } from "../../actions";
import bottomDrawerStyles from "../bottomDrawerStyles";
import SelectionSwitcher from "../share/SelectionSwitcher";
import { BulkEditField, getBulkEditFields } from "./utils";
import { EntityName } from "../../../../../model/entities/common";
import { ShowConfirmCaller } from "../../../../../model/common/Confirm";

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
  entityTags?: { [key: string]: Tag[] };
  getEntityTags?: (entity: string) => void;
  showConfirm?: ShowConfirmCaller;
  reset?: any;
  getCustomBulkEditFields?: () => Promise<any>;
}

const BulkEditForm: React.FC<BulkEditProps> = props => {
  const {
    getCustomBulkEditFields,
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
    reset,
    dispatch
  } = props;

  const [selectAll, setSelectAll] = useState(false);
  const [bulkEditFields, setBulkEditFields] = useState(null);
  const [selectedKeyCode, setSelectedKeyCode] = useState(null);

  const getBulkEditFieldData = ():BulkEditField => {
    if (!bulkEditFields || !selectedKeyCode) return null;
    return bulkEditFields.find(field => field.keyCode === selectedKeyCode);
  };

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

  useEffect(() => {
    if (getCustomBulkEditFields) {
      getCustomBulkEditFields().then(fields => setBulkEditFields(fields));
    }
  }, [getCustomBulkEditFields]);

  useEffect(() => {
    if (showBulkEditDrawer && bulkEditFields?.length) {
      bulkEditFields.forEach(field => {
        if (field.hasOwnProperty("defaultValue")) {
          dispatch(change("BulkEditForm", field.keyCode, field.defaultValue));
        }
      });
    }
  }, [showBulkEditDrawer, bulkEditFields]);

  const onClose = () => {
    reset();
    setSelectAll(false);
    toggleBulkEditDrawer();
  };

  const onSave = values => {
    const ids = selectAll ? null : selection.map(s => Number(s));
    const searchObj = selectAll ? searchQuery : {};
    const diff = {
      [selectedKeyCode]: values[selectedKeyCode]?.toString()
    };

    doBulkEdit(rootEntity, {
      ids,
      diff,
      search: searchObj.search,
      filter: searchObj.filter,
      tagGroups: searchObj.tagGroups
    });
  };

  const tags = useMemo(() => {
    if (rootEntity === "ProductItem" && entityTags) {
      const unique = {};
      const productItemTags = [];

      [
        ...entityTags["Article"] || [],
        ...entityTags["Voucher"] || [],
        ...entityTags["Membership"] || [],
      ].forEach(t => {
        if (!unique[t.id]) {
          unique[t.id] = true;
          productItemTags.push(t);
        }
      });

      return productItemTags;
    }
    return (entityTags && rootEntity && entityTags[rootEntity]) || [];
  }, [entityTags, rootEntity]);

  const BulkEditFieldRendered = useMemo(() => {
    const field = getBulkEditFieldData();

    if (!field) {
      return null;
    }

    let fieldProps;

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
            placeholder: classes.text
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
            placeholder: classes.text,
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
            placeholder: classes.text
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
          <Typography variant="body2" color="inherit" className="mb-3" classes={{ root: classes.listItemsText }}>
            {`The following tags will be ${field.keyCode === "bulkTag" ? "added to" : "removed from"} the records...`}
          </Typography>
          <FormField
            type="tags"
            name={field.keyCode}
            tags={tags}
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
           validate={field.validate}
          {...fieldProps}
        />
      );
  }, [tags, entityTags, rootEntity, bulkEditFields, selectedKeyCode]);

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
                <Grid item xs={12} md={6}>
                  {BulkEditFieldRendered}
                </Grid>
              </Grid>

              <Grid item xs={12} className={classes.closeShareButtons}>
                <Button className={classes.closeButton} onClick={onClose} variant="text">
                  Cancel
                </Button>
                <LoadingButton
                  variant="contained"
                  disabled={invalid || validating || !selectedKeyCode}
                  className={classes.shareButton}
                  type="submit"
                  loading={submitting || validating}
                >
                  Make changes
                </LoadingButton>
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
  dispatch,
  doBulkEdit: (entity, diff: Diff) => dispatch(bulkChangeRecords(entity, diff)),
  getEntityTags: (entity: string) => dispatch(getEntityTags(entity)),
  reset: () => dispatch(reset("BulkEditForm"))
});

export default reduxForm({
  form: "BulkEditForm"
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(bottomDrawerStyles)(BulkEditForm))) as any;
