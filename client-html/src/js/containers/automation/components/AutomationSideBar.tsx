/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import { connect } from "react-redux";
import LockOutlined from "@material-ui/icons/LockOutlined";
import CropLandscapeIcon from "@material-ui/icons/CropLandscape";
import CropPortraitIcon from "@material-ui/icons/CropPortrait";
import CollapseMenuList from "../../../common/components/layout/side-bar-list/CollapseSideBarList";
import { IntegrationSchema } from "../../../model/automation/integrations/IntegrationSchema";
import { State } from "../../../reducers/state";
import { CommonListFilterCondition, CommonListItem, SidebarSharedProps } from "../../../model/common/sidebar";

interface Props {
  className?: any;
  classes?: any;
  hasLicense?: any;
  integrations: IntegrationSchema[];
  scripts: CommonListItem[];
  emailTemplates: CommonListItem[];
  exportTemplates: CommonListItem[];
  importTemplates: CommonListItem[];
  pdfReports: CommonListItem[];
  pdfBackgrounds: CommonListItem[];
  search: string;
  history: any;
  activeFiltersConditions: CommonListFilterCondition[];
}

const PdfBackgroundItemIcon: React.FC<any> = ({ item, className }) => (
  item.isPortrait ? <CropPortraitIcon className={className} /> : <CropLandscapeIcon className={className} />
);

const AutomationSideBar = React.memo<Props>(
  ({
    className,
    integrations,
    search,
    history,
    scripts,
    emailTemplates,
    exportTemplates,
    importTemplates,
    activeFiltersConditions,
    pdfReports,
    pdfBackgrounds
  }) => {
    const integrationLinkCondition = useCallback(int => `/automation/integrations/edit/${int.type}/${int.name}`, []);

    const sharedProps = useMemo<SidebarSharedProps>(() => ({
        history, search, activeFiltersConditions, category: "Automation"
      }), [history, search, activeFiltersConditions]);

    return (
      <div className={className}>
        <CollapseMenuList
          name="Import Templates"
          basePath="/automation/import-templates/"
          plusIconPath="new"
          data={importTemplates}
          sharedProps={sharedProps}
          ItemIcon={LockOutlined}
          defaultCollapsed
        />

        <CollapseMenuList
          name="Export Templates"
          basePath="/automation/export-templates/"
          plusIconPath="new"
          data={exportTemplates}
          sharedProps={sharedProps}
          ItemIcon={LockOutlined}
          defaultCollapsed
        />

        <CollapseMenuList
          name="Message Templates"
          basePath="/automation/email-templates/"
          plusIconPath="new"
          data={emailTemplates}
          sharedProps={sharedProps}
          ItemIcon={LockOutlined}
          defaultCollapsed
        />

        <CollapseMenuList
          name="PDF Backgrounds"
          basePath="/automation/pdf-backgrounds/"
          plusIconPath="new"
          data={pdfBackgrounds}
          sharedProps={sharedProps}
          ItemIcon={PdfBackgroundItemIcon}
          defaultCollapsed
        />

        <CollapseMenuList
          name="PDF Reports"
          basePath="/automation/pdf-reports/"
          plusIconPath="new"
          data={pdfReports}
          sharedProps={sharedProps}
          ItemIcon={LockOutlined}
          defaultCollapsed
        />

        <CollapseMenuList
          name="Integrations"
          basePath="/automation/integrations"
          plusIconFullPath="/automation/integrations/list"
          linkCondition={integrationLinkCondition}
          data={integrations}
          sharedProps={sharedProps}
          defaultCollapsed
        />

        <CollapseMenuList
          name="Scripts"
          plusIconPath="new"
          basePath="/automation/script/"
          data={scripts}
          sharedProps={sharedProps}
          defaultCollapsed
          ItemIcon={LockOutlined}
        />
      </div>
    );
  }
);

const mapStateToProps = (state: State) => ({
  integrations: state.automation.integration.integrations,
  scripts: state.automation.script.scripts,
  emailTemplates: state.automation.emailTemplate.emailTemplates,
  exportTemplates: state.automation.exportTemplate.exportTemplates,
  importTemplates: state.automation.importTemplate.importTemplates,
  pdfReports: state.automation.pdfReport.pdfReports,
  pdfBackgrounds: state.automation.pdfBackground.pdfBackgrounds
});

export default connect<any, any, any>(mapStateToProps, null)(AutomationSideBar);
