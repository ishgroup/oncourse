/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import Grid from "@mui/material/Grid";
import CustomFields from "./CustomFieldsTypes";
import ExpandableContainer from "../../../../common/components/layout/expandable/ExpandableContainer";
import { State } from "../../../../reducers/state";
import { getCustomFieldTypes } from "../actions";

const ExpandableCustomFields = React.memo<any>(props => {
  const {
    values,
    dispatch,
    form,
    twoColumn,
    tabIndex,
    expanded,
    setExpanded,
    invalid,
    syncErrors,
    header = "Custom Fields",
    entityName,
    fieldName = "customFields",
    customFieldTypes,
    getFieldTypes,
  } = props;

  useEffect(() => {
    if (invalid && syncErrors.customFields) {
      const updated = [...expanded];

      if (!updated.includes(tabIndex)) {
        updated.push(tabIndex);
      }

      setExpanded(updated);
    }
  }, [invalid, syncErrors]);

  useEffect(() => {
    if (!customFieldTypes || !customFieldTypes[entityName]) {
      getFieldTypes(entityName);
    }
  }, [entityName, customFieldTypes]);

  const gridItemProps: any = {
    xs: twoColumn ? 6 : 12,
    lg: twoColumn ? 4 : 12
  };

  return values && values[fieldName] && customFieldTypes && customFieldTypes[entityName] && customFieldTypes[entityName].length ? (
    <Grid container className="pt-2 pl-3 pr-3">
      <Grid item xs={12}>
        <ExpandableContainer index={tabIndex} expanded={expanded} setExpanded={setExpanded} mountAll header={header}>
          <Grid container columnSpacing={3} rowSpacing={2} className="mb-2">
            <CustomFields
              entityName={entityName}
              fieldName={fieldName}
              entityValues={values}
              dispatch={dispatch}
              form={form}
              gridItemProps={gridItemProps}
            />
          </Grid>
        </ExpandableContainer>
      </Grid>
    </Grid>
  ) : null;
});

const mapStateToProps = (state: State) => ({
  customFieldTypes: state.customFieldTypes.types
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  getFieldTypes: entity => dispatch(getCustomFieldTypes(entity))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ExpandableCustomFields);
