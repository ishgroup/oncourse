/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Typography from "@mui/material/Typography";
import Link from "@mui/material/Link";
import { change, initialize, reduxForm } from "redux-form";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormErrors";
import { State } from "../../../../../../reducers/state";
import { getUSISoftwareId } from "../../../../../preferences/actions";

class XeroBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("UsiForm", props.item));
  }

  componentDidMount() {
    this.props.getSoftwareId();

    if (this.props.match.params.action === "new") {
      this.props.dispatch(change("UsiForm", "name", "USI integration"));
    }
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("UsiForm", this.props.item));
    }
  }

  render() {
    const {
      AppBarContent,
      handleSubmit,
      onSubmit,
      usiSoftwareId,
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <Typography component="div" variant="body2">
            <div>
              Go to
              {" "}
              <Link href="https://authorisationmanager.gov.au/" target="_blank" color="secondary">
                https://authorisationmanager.gov.au
              </Link>
              {" "}
              and add a Cloud Software Notification for:
            </div>
            <ul>
              <li>Digital Service Provider ABN: 74073212736</li>
              <li>
                Software ID:
                {usiSoftwareId}
              </li>
            </ul>
          </Typography>
        </AppBarContent>
      </form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  usiSoftwareId: state.usiSoftwareId
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    getSoftwareId: () => dispatch(getUSISoftwareId())
  });

export const UsiForm = reduxForm({
  form: "UsiForm",
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(XeroBaseForm));
