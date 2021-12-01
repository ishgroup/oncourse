/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect } from "react";
import { Grid } from "@mui/material";
import CustomFields from "../../customFieldTypes/components/CustomFieldsTypes";
import ExpandableContainer from "../../../../common/components/layout/expandable/ExpandableContainer";

const ContactCustomFields: React.FC<any> = props => {
  const {
    values,
    dispatch,
    form,
    twoColumn,
    tabIndex,
    expanded,
    setExpanded,
    invalid,
    syncErrors
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

  const gridItemProps: any = {
    xs: twoColumn ? 6 : 12,
    lg: twoColumn ? 4 : 12
  };

  return (
    <Grid container className="pt-2 pl-3 pr-3">
      <Grid item xs={12}>
        <ExpandableContainer index={tabIndex} expanded={expanded} setExpanded={setExpanded} mountAll header="Custom Fields">
          <Grid container columnSpacing={3} rowSpacing={2} className="mb-2">
            <CustomFields
              entityName="Contact"
              fieldName="customFields"
              entityValues={values}
              dispatch={dispatch}
              form={form}
              gridItemProps={gridItemProps}
            />
          </Grid>
        </ExpandableContainer>
      </Grid>
    </Grid>
  )
};

export default ContactCustomFields;