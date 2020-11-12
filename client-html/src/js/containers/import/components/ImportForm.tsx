/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useState } from "react";
import clsx from "clsx";
import { connect } from "react-redux";
import {
  reduxForm, getFormValues, InjectedFormProps
} from "redux-form";
import { format as formatDate } from "date-fns";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";
import MuiButton from "@material-ui/core/Button";
import HelpOutline from "@material-ui/icons/HelpOutline";
import FormField from "../../../common/components/form/form-fields/FormField";
import { State } from "../../../reducers/state";
import Button from "../../../common/components/buttons/Button";
import CustomAppBar from "../../../common/components/layout/CustomAppBar";
import AppBarActions from "../../../common/components/form/AppBarActions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { III_DD_MMM_YYYY } from "../../../common/utils/dates/format";
import FileUploadContainer from "./FileUploadContainer";

const styles = theme =>
  createStyles({
    mainContent: {
      display: "flex",
      justifyContent: "center"
    },
    buttons: {
      display: "flex",
      justifyContent: "flex-end",
      "& > *": {
        marginRight: theme.spacing(3)
      },
      "& > *:last-child": {
        marginRight: 0
      }
    }
  });

export const FORM: string = "ImportForm";

const manualUrl = getManualLink("importExport");

const openManual = () => {
  window.open(manualUrl);
};

interface Props extends InjectedFormProps {
  classes?: any;
  values?: any;
}

const initialValues: any = {
  dateRange: "",
  studentTag: "Imported",
  createdDate: formatDate(new Date(), III_DD_MMM_YYYY),
  importDateTime: false
};

const ImportForm: React.FC<Props> = props => {
  const { handleSubmit, classes } = props;
  const [files, setFiles] = useState([]);

  const onSubmit = useCallback(() => {
    console.log(files);
  }, [files]);

  return (
    <form className="container" onSubmit={handleSubmit(onSubmit)}>
      <CustomAppBar fullWidth>
        <Grid container>
          <Grid item xs={12} className="centeredFlex">
            <Typography variant="h6" color="inherit" noWrap>
              Import
            </Typography>
            <div className="flex-fill" />

            <AppBarActions actions={[]} />
            <Tooltip title="Additional information">
              <IconButton onClick={openManual}>
                <HelpOutline className="text-white" />
              </IconButton>
            </Tooltip>
            <Button onClick={() => {}} className="whiteAppBarButton">
              Save
            </Button>
          </Grid>
        </Grid>
      </CustomAppBar>
      <Grid container className={classes.mainContent}>
        <Grid item sm={5}>
          <Card>
            <CardContent className="mb-0 p-3">
              <Typography
                color="inherit"
                component="div"
                className="heading pt-2 pb-2 centeredFlex"
              >
                Import
              </Typography>
              <FormField
                type="select"
                name="dateRange"
                items={[
                  {
                    label: "onCourse AVETMISS outcome import",
                    value: "onCourse AVETMISS outcome import"
                  },
                  {
                    label: "onCourse AVETMISS outcome update import",
                    value: "onCourse AVETMISS outcome update import"
                  }
                ]}
                formatting="primary"
              />
              <Typography variant="caption" component="div" className="pb-3">
                Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et
                dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.
              </Typography>
              <span className="p-3" />
              <FileUploadContainer files={files} setSelectedFiles={setFiles} />
              <div className="pt-3 pb-3">
                <FormField
                  type="text"
                  name="studentTag"
                  label="Tag students with"
                  formatting="primary"
                  className="pb-2"
                />
                <FormField
                  type="date"
                  name="createdDate"
                  label="Import students created after"
                  className="pb-2"
                />
                <FormControlLabel
                  className="checkbox pb-2"
                  control={<FormField type="checkbox" name="importDateTime" color="secondary" fullWidth />}
                  label="importDateTime"
                />
              </div>
              <div className={clsx("pt-2", classes.buttons)}>
                <MuiButton color="primary" onClick={() => {}}>
                  Cancel
                </MuiButton>
                <Button variant="contained" type="submit" color="primary" onClick={() => {}}>
                  Import
                </Button>
              </div>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </form>
  );
};

const mapStateToProps = (state: State) => ({
  values: getFormValues(FORM)(state)
});

const ImportFormWrapped = reduxForm({
  form: FORM,
  initialValues
})(connect<any, any, any>(mapStateToProps, null)(withStyles(styles)(ImportForm)));

export default ImportFormWrapped as any;
