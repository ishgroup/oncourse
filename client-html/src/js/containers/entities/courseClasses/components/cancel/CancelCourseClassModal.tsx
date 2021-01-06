/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import clsx from "clsx";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import {
  reduxForm, getFormValues, InjectedFormProps
} from "redux-form";
import Dialog from "@material-ui/core/Dialog";
import Typography from "@material-ui/core/Typography";
import DialogActions from "@material-ui/core/DialogActions";
import MuiButton from "@material-ui/core/Button";
import DialogContent from "@material-ui/core/DialogContent";
import FormGroup from "@material-ui/core/FormGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Grid from "@material-ui/core/Grid";
import { CancelCourseClass } from "@api/model";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../../reducers/state";
import Button from "../../../../../common/components/buttons/Button";
import { BooleanArgFunction } from "../../../../../model/common/CommonFunctions";
import { cancelCourseClass } from "../../actions";
import {clearCommonPlainRecords, setCommonPlainSearch} from "../../../../../common/actions/CommonPlainRecordsActions";

interface Props extends InjectedFormProps {
  opened: boolean;
  setDialogOpened: BooleanArgFunction;
  selection?: any;
  closeMenu?: any;
  cancelCourseClass?: (values: CancelCourseClass) => void;
  fetching?: boolean;
  clearCourseClassSearch?: any;
  selectedClass?: any;
  value?: any;
  dispatch: any;
}

const initialValues: any = {
  refundEnrolmentInvoices: true,
  refundManualInvoices: true,
  sendEmail: true
};

const FORM: string = "CANCEL_COURSE_CLASS_FORM";

class CancelCourseClassModalForm extends React.Component<Props, any> {
  constructor(props) {
    super(props);
  }

  onClose = () => {
    const { setDialogOpened, clearCourseClassSearch, reset } = this.props;
    setDialogOpened(false);
    clearCourseClassSearch();
    reset();
  };

  onSubmit = (values: any) => {
    const {
     setDialogOpened, cancelCourseClass, closeMenu, selection, clearCourseClassSearch
    } = this.props;
    const cancel = {
      classIds: selection,
      refundManualInvoices: values.refundManualInvoices,
      sendEmail: values.sendEmail
    };

    cancelCourseClass(cancel);
    clearCourseClassSearch();
    setDialogOpened(false);
    closeMenu();
  };

  getErrorMsg = (item: any) => {
    if (item.length > 0) {
      if (item[0].cancelWarningMessage != null && item[0].cancelWarningMessage !== "No warnings") {
        return item[0].cancelWarningMessage;
      }
    }
    return null;
  };

  getEnrolmentCount = (item: any) => {
    if (item.length > 0) {
      if (item[0].validEnrolmentCount) {
        return Number(item[0].validEnrolmentCount);
      }
    }
    return null;
  };

  getSelectedClassRecord = field => {
    const { selectedClass } = this.props;
    if (selectedClass.length > 0) {
      return selectedClass[0][field];
    }
    return "";
  };

  render() {
    const {
     opened, handleSubmit, fetching, invalid, selectedClass, value
    } = this.props;
    const errMsg = this.getErrorMsg(selectedClass);
    const enrolmentCount = this.getEnrolmentCount(selectedClass);

    return (
      <Dialog
        open={opened}
        onClose={this.onClose}
        maxWidth="md"
        disableAutoFocus
        disableEnforceFocus
        disableRestoreFocus
      >
        <form autoComplete="off" onSubmit={handleSubmit(this.onSubmit)}>
          <DialogContent>
            <Grid container>
              <Grid item xs={12}>
                <div className="centeredFlex">
                  <div className="heading mt-2 mb-2">
                    You are about to cancel class "
                    {`${this.getSelectedClassRecord("course.name")} ${this.getSelectedClassRecord(
                      "course.code"
                    )}-${this.getSelectedClassRecord("code")}`}
                    "
                  </div>
                </div>
                <div className={clsx("centeredFlex", !errMsg && "invisible")}>
                  <Typography variant="body2" className="errorColor">
                    {errMsg}
                  </Typography>
                </div>
                <div className="centeredFlex">
                  <FormGroup>
                    <FormControlLabel
                      classes={{
                        root: "checkbox"
                      }}
                      control={
                        <FormField type="checkbox" name="refundEnrolmentInvoices" color="secondary" disabled />
                      }
                      label="Create credit notes to reverse the enrolment fee."
                    />
                  </FormGroup>
                  <Typography variant="caption" className="pl-2">
                    {enrolmentCount}
                    {' '}
                    credit note
                    {enrolmentCount > 1 ? "s" : ""}
                    {' '}
                    will be created
                  </Typography>
                </div>
                <div>
                  <FormGroup>
                    <FormControlLabel
                      classes={{
                        root: "checkbox"
                      }}
                      control={<FormField type="checkbox" name="refundManualInvoices" color="secondary" />}
                      label="Create credit notes to reverse invoices manually linked to the class."
                    />
                  </FormGroup>
                </div>
                <div className="pb-2">
                  <FormGroup>
                    <FormControlLabel
                      classes={{
                        root: "checkbox"
                      }}
                      control={<FormField type="checkbox" name="sendEmail" color="secondary" disabled={!value} />}
                      label="Send credit notes email."
                    />
                  </FormGroup>
                </div>
              </Grid>
            </Grid>
          </DialogContent>

          <DialogActions className="p-3">
            <MuiButton color="primary" onClick={this.onClose}>
              Cancel
            </MuiButton>

            <Button disabled={fetching || invalid} variant="contained" color="primary" type="submit">
              Proceed
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    );
  }
}

const mapStateToProps = (state: State) => ({
  value: getFormValues(FORM)(state),
  selectedClass: state.plainSearchRecords["CourseClass"].items
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  cancelCourseClass: (values: CancelCourseClass) => dispatch(cancelCourseClass(values)),
  clearCourseClassSearch: () => dispatch(clearCommonPlainRecords("CourseClass"))
});

const CancelCourseClassModal = reduxForm({
  form: FORM,
  initialValues
})(CancelCourseClassModalForm);

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CancelCourseClassModal as any);
