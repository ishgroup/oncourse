/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo } from "react";
import { RouteComponentProps } from "react-router";
import { useAppDispatch, useAppSelector } from "../../../common/utils/hooks";
import CatalogWithSearch from "../../../common/components/layout/catalog/CatalogWithSearch";
import { CatalogItemType } from "../../../model/common/Catalog";
import IntegrationTypes from "./integrations/IntegrationTypes";
import { AutomationEntity } from "../../../model/automation/integrations";
import { installAutomation, uninstallAutomation } from "../actions";
import { showConfirm } from "../../../common/actions";
import { getEmailTemplatesListFulfilled } from "./email-templates/actions";
import { getExportTemplatesListFulfilled } from "./export-templates/actions";
import { getImportTemplatesListFulfilled } from "./import-templates/actions";
import { getAutomationPdfReportsListFulfilled } from "./pdf-reports/actions";
import { getScriptsListFulfilled } from "./scripts/actions";

const useUpdateAutomationStatus = (entity: AutomationEntity) => {
  const dispatch = useAppDispatch();
  const emailTemplates = useAppSelector(state => state.automation.emailTemplate.emailTemplates);
  const exportTemplates = useAppSelector(state => state.automation.exportTemplate.exportTemplates);
  const importTemplates = useAppSelector(state => state.automation.importTemplate.importTemplates);
  const pdfReports = useAppSelector(state => state.automation.pdfReport.pdfReports);
  const scripts = useAppSelector(state => state.automation.script.scripts);
  
  switch (entity) {
    case "EmailTemplate": {
      return (id: number, installed: boolean) => {
        dispatch(getEmailTemplatesListFulfilled(emailTemplates.map(t => ({
          ...t,
          installed: t.id === id ? installed : t.installed,
          enabled: installed
        }))));
      };
    }
    case "ExportTemplate": {
      return (id: number, installed: boolean) => {
        dispatch(getExportTemplatesListFulfilled(exportTemplates.map(t => ({
          ...t,
          installed: t.id === id ? installed : t.installed,
          enabled: installed
        }))));
      };
    }
    case "Import": {
      return (id: number, installed: boolean) => {
        dispatch(getImportTemplatesListFulfilled(importTemplates.map(t => ({
          ...t,
          installed: t.id === id ? installed : t.installed,
          enabled: installed
        }))));
      };
    }
    case "Report": {
      return (id: number, installed: boolean) => {
        dispatch(getAutomationPdfReportsListFulfilled(pdfReports.map(t => ({
          ...t,
          installed: t.id === id ? installed : t.installed,
          enabled: installed
        }))));
      };
    }
    case "Script": {
      return (id: number, installed: boolean) => {
        dispatch(getScriptsListFulfilled(scripts.map(t => ({
          ...t,
          installed: t.id === id ? installed : t.installed,
          enabled: installed
        }))));
      };
    }
    default: 
      throw Error("Unknown automation type");
  }
};

const useInstallToggle = (entity: AutomationEntity) => {
  const dispatch = useAppDispatch();
  const updateAutomationStatus = useUpdateAutomationStatus(entity);
  
  return (automation: CatalogItemType) => {
    if (!automation.installed) {
      dispatch(installAutomation(automation, entity));
      updateAutomationStatus(automation.id, true);
      return;
    }
    dispatch(showConfirm({
      onConfirm: () => {
        dispatch(uninstallAutomation(automation, entity));
        updateAutomationStatus(automation.id, false);
      },
      confirmMessage: "Automation will be uninstalled",
      confirmButtonText: "Uninstall"
    }));
  };
};

export const EmailTemplatesCatalog = ({ history }: RouteComponentProps) => {
  const emailTemplates = useAppSelector(state => state.automation.emailTemplate.emailTemplates);

  const toggleInstall = useInstallToggle("EmailTemplate");

  const onOpen = id => {
    history.push(`/automation/email-template/${id}`);
  };

  const onClickNew = () => {
    history.push("/automation/email-template/new");
  };

  return (
    <CatalogWithSearch
      addNewItem={{
        title: "Custom email template",
        category: "Advanced",
        shortDescription: "Create a new email template from scratch"
      }}
      toggleInstall={toggleInstall}
      items={emailTemplates}
      title="Email templates"
      itemsListTitle="installed email templates"
      onOpen={onOpen}
      onClickNew={onClickNew}
      description="Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab, assumenda, cum cupiditate dignissimos doloribus iure modi nisi nobis possimus quos ratione recusandae tenetur totam! Aliquam laudantium nesciunt ratione tempora totam!"
    />
  );
};

export const ExportTemplatesCatalog = ({ history }: RouteComponentProps) => {
  const exportTemplates = useAppSelector(state => state.automation.exportTemplate.exportTemplates);

  const toggleInstall = useInstallToggle("ExportTemplate");

  const onOpen = id => {
    history.push(`/automation/export-template/${id}`);
  };

  const onClickNew = () => {
    history.push("/automation/export-template/new");
  };

  return (
    <CatalogWithSearch
      addNewItem={{
        title: "Custom export template",
        category: "Advanced",
        shortDescription: "Create a new export template from scratch"
      }}
      
      toggleInstall={toggleInstall}
      items={exportTemplates}
      title="Export templates"
      itemsListTitle="Installed export templates"
      onOpen={onOpen}
      onClickNew={onClickNew}
      description="Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab, assumenda, cum cupiditate dignissimos doloribus iure modi nisi nobis possimus quos ratione recusandae tenetur totam! Aliquam laudantium nesciunt ratione tempora totam!"
    />
  );
};

export const ImportTemplatesCatalog = ({ history }: RouteComponentProps) => {
  const importTemplates = useAppSelector(state => state.automation.importTemplate.importTemplates);

  const toggleInstall = useInstallToggle("Import");

  const onOpen = id => {
    history.push(`/automation/import-template/${id}`);
  };

  const onClickNew = () => {
    history.push("/automation/import-template/new");
  };

  return (
    <CatalogWithSearch
      addNewItem={{
        title: "Custom import template",
        category: "Advanced",
        shortDescription: "Create a new import template from scratch"
      }}
      
      toggleInstall={toggleInstall}
      items={importTemplates}
      title="Import templates"
      itemsListTitle="installed import templates"
      onOpen={onOpen}
      onClickNew={onClickNew}
      description="Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab, assumenda, cum cupiditate dignissimos doloribus iure modi nisi nobis possimus quos ratione recusandae tenetur totam! Aliquam laudantium nesciunt ratione tempora totam!"
    />
  );
};

export const PDFReportsCatalog = ({ history }: RouteComponentProps) => {
  const pdfReports = useAppSelector(state => state.automation.pdfReport.pdfReports);

  const toggleInstall = useInstallToggle("Report");

  const onOpen = id => {
    history.push(`/automation/pdf-report/${id}`);
  };

  const onClickNew = () => {
    history.push("/automation/pdf-report/new");
  };

  return (
    <CatalogWithSearch
      addNewItem={{
        title: "Custom PDF report",
        category: "Advanced",
        shortDescription: "Create a new pdf report template from scratch"
      }}
      
      toggleInstall={toggleInstall}
      items={pdfReports}
      title="PDF reports"
      itemsListTitle="Installed pdf reports"
      onOpen={onOpen}
      onClickNew={onClickNew}
      description="Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab, assumenda, cum cupiditate dignissimos doloribus iure modi nisi nobis possimus quos ratione recusandae tenetur totam! Aliquam laudantium nesciunt ratione tempora totam!"
    />
  );
};

export const PDFBackgroundsCatalog = ({ history }: RouteComponentProps) => {
  const pdfBackgrounds = useAppSelector(state => state.automation.pdfBackground.pdfBackgrounds);

  const onOpen = id => {
    history.push(`/automation/pdf-background/${id}`);
  };

  const onClickNew = () => {
    history.push("/automation/pdf-background/new");
  };

  return (
    <CatalogWithSearch
      items={pdfBackgrounds}
      title="PDF backgrounds"
      itemsListTitle="Available pdf backgrounds"
      onOpen={onOpen}
      customAddNew={onClickNew}
    />
  );
};

export const IntegrationsCatalog = ({ history }: RouteComponentProps) => {
  const integrations = useAppSelector(state => state.automation.integration.integrations);

  const items = useMemo<CatalogItemType[]>(() => integrations?.map(i => ({
    id: i.id,
    title: i.name,
    category: null,
    installed: true,
    enabled: true,
    tag: null,
    shortDescription: IntegrationTypes[i.type].description
  })) || [], [integrations]);

  const onOpen = id => {
    history.push(`/automation/integration/${id}`);
  };

  const onClickNew = () => {
    history.push("/automation/integrations/list");
  };

  return (
    <CatalogWithSearch
      items={items}
      title="Integrations"
      itemsListTitle="Added integrations"
      onOpen={onOpen}
      customAddNew={onClickNew}
    />
  );
};

export const ScriptsCatalog = ({ history }: RouteComponentProps) => {
  const scripts = useAppSelector(state => state.automation.script.scripts);

  const toggleInstall = useInstallToggle("Script");

  const onOpen = id => {
    history.push(`/automation/script/${id}`);
  };

  const onClickNew = () => {
    history.push("/automation/script/new");
  };

  return (
    <CatalogWithSearch
      addNewItem={{
        title: "Custom script",
        category: "Advanced",
        shortDescription: "Create a new script from scratch"
      }}
      
      toggleInstall={toggleInstall}
      items={scripts}
      title="Automations"
      itemsListTitle="installed automations"
      onOpen={onOpen}
      onClickNew={onClickNew}
      description="Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab, assumenda, cum cupiditate dignissimos doloribus iure modi nisi nobis possimus quos ratione recusandae tenetur totam! Aliquam laudantium nesciunt ratione tempora totam!"
    />
  );
};
