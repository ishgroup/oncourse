/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { RouteComponentProps } from "react-router-dom";
import { ADMIN_EMAIL_KEY } from "../../constants/Config";
import { State } from "../../reducers/state";
import { SidebarWithSearch } from "../../common/components/layout/sidebar-with-search/SidebarWithSearch";
import { getColumnsWidth, getPreferencesByKeys, updateColumnsWidth } from "../preferences/actions";
import SideBar from "./components/AutomationSideBar";
import { getIntegrations } from "./actions";
import { getScriptsList, getTimeZone } from "./containers/scripts/actions";
import { getExportTemplatesList } from "./containers/export-templates/actions";
import { getAutomationPdfReportsList } from "./containers/pdf-reports/actions";
import { getAutomationPdfBackgroundsList } from "./containers/pdf-backgrounds/actions";
import { getEmailTemplatesList } from "./containers/email-templates/actions";
import { getImportTemplatesList } from "./containers/import-templates/actions";
import { NumberArgFunction } from "../../model/common/CommonFunctions";
import automationRoutes from "./routes";
import { getAllTags } from "../tags/actions";

interface Props extends RouteComponentProps {
  formName: string;
  dirty: boolean;
  onSetSwipeableDrawerDirtyForm: (isDirty: boolean, formName: string) => void;
  leftColumnWidth: number;
  updateColumnsWidth: NumberArgFunction;
}

const Automation = React.memo<Props>(props => {
  const {
    history,
    location: {
      pathname
    }
  } = props;

  useEffect(() => {
    if (pathname === "/automation") {
      history.replace("/automation/scripts");
    }
  }, []);

  return (
    <SidebarWithSearch {...props} SideBar={SideBar} routes={automationRoutes} noSearch appFrameClass="w-50" />
  );
});

const mapStateToProps = (state: State) => ({
  leftColumnWidth: state.preferences.columnWidth?.automationLeftColumnWidth
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(getColumnsWidth());
    dispatch(getIntegrations());
    dispatch(getPreferencesByKeys([ADMIN_EMAIL_KEY], 14));
    dispatch(getScriptsList());
    dispatch(getEmailTemplatesList());
    dispatch(getExportTemplatesList());
    dispatch(getImportTemplatesList());
    dispatch(getAutomationPdfReportsList());
    dispatch(getAutomationPdfBackgroundsList());
    dispatch(getTimeZone());
    dispatch(getAllTags());
  },
  updateColumnsWidth: (automationLeftColumnWidth: number) => {
    dispatch(updateColumnsWidth({ automationLeftColumnWidth }));
  }
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Automation);