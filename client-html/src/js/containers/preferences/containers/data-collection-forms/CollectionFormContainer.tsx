/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import * as React from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { State } from "../../../../reducers/state";
import DataCollectionForm from "./components/DataCollectionForm";

const CollectionFormContainer: React.FC<any> = props => {
  return (
    <DataCollectionForm {...props} />
  );
};

const mapStateToProps = (state: State) => ({
  collectionForms: state.preferences.dataCollectionForms,
});

export default connect<any, any, any>(mapStateToProps, null)(withRouter(CollectionFormContainer));
