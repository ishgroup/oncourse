/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useMemo } from "react";
import { isDirty, reset } from "redux-form";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import Lock from "@material-ui/icons/LockOutlined";
import { LICENSE_SCRIPTING_KEY, ADMIN_EMAIL_KEY } from "../../constants/Config";
import { State } from "../../reducers/state";
import { CommonListFilter } from "../../model/common/sidebar";
import { SidebarWithSearch } from "../../common/components/layout/sidebar-with-search/SidebarWithSearch";
import { setSwipeableDrawerDirtyForm } from "../../common/components/layout/swipeable-sidebar/actions";
import { getUserPreferences } from "../../common/actions";
import { getColumnsWidth, updateColumnsWidth, getPreferencesByKeys } from "../preferences/actions";
import SideBar from "./components/AutomationSideBar";
import AutomatiomAppFrame from "./components/AutomationAppFrame";
import { getIntegrations } from "./actions";
import { getScriptsList } from "./containers/scripts/actions";
import { getExportTemplatesList } from "./containers/export-templates/actions";
import { getAutomationPdfReportsList } from "./containers/pdf-reports/actions";
import { getAutomationPdfBackgroundsList } from "./containers/pdf-backgrounds/actions";
import { getEmailTemplatesList } from "./containers/email-templates/actions";
import { getImportTemplatesList } from "./containers/import-templates/actions";

const filters: CommonListFilter[] = [
  {
    name: "Built-in",
    condition: item => item.hasIcon,
    icon: <Lock />
  },
  {
    name: "Custom",
    condition: item => !item.hasIcon
  },
  {
    name: "Disabled",
    condition: item => item.grayOut
  }
];

const Automation = React.memo<any>(props => {
  const {
    location: {
      pathname
    },
    formName,
    dirty,
    onSetSwipeableDrawerDirtyForm
  } = props;

  const isNew = useMemo(() => {
    const pathArray = pathname.split("/");
    return pathArray.length > 3 && pathArray[3] === "new";
  }, [pathname]);

  useEffect(() => {
    onSetSwipeableDrawerDirtyForm(dirty || isNew, formName);
  }, [isNew, dirty, formName]);

  return (
    <SidebarWithSearch SideBar={SideBar} AppFrame={AutomatiomAppFrame} filters={filters} {...props} />
  );
});

const getFormName = form => form && Object.keys(form)[0];

const mapStateToProps = (state: State) => ({
  leftColumnWidth: state.preferences.columnWidth && state.preferences.columnWidth.automationLeftColumnWidth,
  formName: getFormName(state.form),
  dirty: isDirty(getFormName(state.form))(state)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(getColumnsWidth());
    dispatch(getIntegrations());
    dispatch(getUserPreferences([LICENSE_SCRIPTING_KEY]));
    dispatch(getPreferencesByKeys([ADMIN_EMAIL_KEY], 14));
    dispatch(getScriptsList());
    dispatch(getEmailTemplatesList());
    dispatch(getExportTemplatesList());
    dispatch(getImportTemplatesList());
    dispatch(getAutomationPdfReportsList());
    dispatch(getAutomationPdfBackgroundsList());
  },
  updateColumnsWidth: (automationLeftColumnWidth: number) => {
    dispatch(updateColumnsWidth({ automationLeftColumnWidth }));
  },
  onSetSwipeableDrawerDirtyForm: (isDirty: boolean, formName: string) => dispatch(
    setSwipeableDrawerDirtyForm(isDirty, () => dispatch(reset(formName)))
  )
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Automation);
