/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { arrayInsert, arrayRemove, FieldArray } from "redux-form";
import { Grid } from "@mui/material";
import { TutorRolePayRate } from "@api/model";
import { format, subYears } from "date-fns";
import PayRateItem from "./PayRateItem";
import { YYYY_MM_DD_MINUSED } from "../../../../../common/utils/dates/format";
import { ShowConfirmCaller } from "../../../../../model/common/Confirm";
import AddButton from "../../../../../common/components/icons/AddButton";

const FIELD_NAME: string = "payRates";

interface Props {
  classes?: any;
  form: string;
  value: any;
  dispatch: any;
  showConfirm?: ShowConfirmCaller;
}

const newPayRate: TutorRolePayRate = {
  type: "Per session",
  validFrom: format(subYears(new Date(), 5), YYYY_MM_DD_MINUSED),
  rate: null,
  oncostRate: 0,
  notes: ""
};

class PayRates extends React.Component<Props, any> {
  addPayRate = () => {
    const { form, dispatch, value } = this.props;

    const payRate = value.payRates.length > 0 ? ({ ...newPayRate, validFrom: "" }) : newPayRate;

    dispatch(arrayInsert(form, FIELD_NAME, 0, payRate));
  };

   deletePayRate = (index: number) => {
     const { form, dispatch, showConfirm } = this.props;

     showConfirm({
       onConfirm: () => {
         dispatch(arrayRemove(form, FIELD_NAME, index));
       },
       confirmMessage: "This item will be removed from pay rate list",
       confirmButtonText: "Delete"
     });
  };

  render() {
    const { classes } = this.props;

    return (
      <Grid container className="h-100 overflow-hidden justify-content-center" alignContent="flex-start">
        <Grid item xs={12}>
          <div className="centeredFlex">
            <div className="heading">Pay Rate</div>
            <AddButton onClick={this.addPayRate} />
          </div>
        </Grid>

        <FieldArray name={FIELD_NAME} component={PayRateItem} classes={classes} onDelete={this.deletePayRate} />
      </Grid>
    );
  }
}

export default PayRates;
