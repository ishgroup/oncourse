import * as React from "react";
import { arrayInsert, arrayRemove, FieldArray } from "redux-form";
import { Grid } from "@mui/material";
import { createStyles, withStyles } from "@mui/styles";
import IconButton from "@mui/material/IconButton";
import AddCircle from "@mui/icons-material/AddCircle";
import { TutorRolePayRate } from "@api/model";
import { format, subYears } from "date-fns";
import PayRateItem from "./PayRateItem";
import { YYYY_MM_DD_MINUSED } from "../../../../../common/utils/dates/format";
import { ShowConfirmCaller } from "../../../../../model/common/Confirm";

const styles = () => createStyles({
  payRateItem: {
    marginBottom: 30
  }
});

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
      <Grid container columnSpacing={3} className="h-100 overflow-hidden justify-content-center" alignContent="flex-start">
        <Grid item xs={12}>
          <div className="centeredFlex">
            <div className="heading">Pay Rate</div>
            <IconButton onClick={this.addPayRate}>
              <AddCircle className="addButtonColor" />
            </IconButton>
          </div>
        </Grid>

        <FieldArray name={FIELD_NAME} component={PayRateItem} classes={classes} onDelete={this.deletePayRate} />
      </Grid>
    );
  }
}

export default withStyles(styles)(PayRates);
