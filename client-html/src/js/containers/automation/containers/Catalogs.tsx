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
          ...t.id === id ? { installed, enabled: installed } : {}
        }))));
      };
    }
    case "ExportTemplate": {
      return (id: number, installed: boolean) => {
        dispatch(getExportTemplatesListFulfilled(exportTemplates.map(t => ({
          ...t,
          ...t.id === id ? { installed, enabled: installed } : {}
        }))));
      };
    }
    case "Import": {
      return (id: number, installed: boolean) => {
        dispatch(getImportTemplatesListFulfilled(importTemplates.map(t => ({
          ...t,
          ...t.id === id ? { installed, enabled: installed } : {}
        }))));
      };
    }
    case "Report": {
      return (id: number, installed: boolean) => {
        dispatch(getAutomationPdfReportsListFulfilled(pdfReports.map(t => ({
          ...t,
          ...t.id === id ? { installed, enabled: installed } : {}
        }))));
      };
    }
    case "Script": {
      return (id: number, installed: boolean) => {
        dispatch(getScriptsListFulfilled(scripts.map(t => ({
          ...t,
          ...t.id === id ? { installed, enabled: installed } : {}
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
        title: "Custom message template",
        category: "Advanced",
        shortDescription: "Create a new message template from scratch"
      }}
      toggleInstall={toggleInstall}
      items={emailTemplates}
      title="Message templates"
      itemsListTitle="Installed message templates"
      onOpen={onOpen}
      onClickNew={onClickNew}
      description="Use one of onCourse's message templates to simply and quickly communicate with your customers via email or SMS. Many templates rely on the standard footer and header templates, so customise those to add your logo or styling. Message templates can make use of embedded scripts, so you have full access output any data you want, styled in any way.<p>Be aware that it can be a little trick to support many different email software and devices in html emails, so be conservative with your styling choices.</p>"
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
        title: "Export template",
        category: "Advanced",
        shortDescription: "Create a new export template from scratch"
      }}

      toggleInstall={toggleInstall}
      items={exportTemplates}
      title="Exports"
      itemsListTitle="Installed export templates"
      onOpen={onOpen}
      onClickNew={onClickNew}
      description="Export every piece of data from onCourse system into any of the commonly-used formats; csv, json, xml, txt or anything else you can think of. Using export templates you can not only choose which pieces of data to export but transform your data on the way out in any way you want. Rather than exporting CSV and then spending ages every week"
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
        title: "Import template",
        category: "Advanced",
        shortDescription: "Create a new import template from scratch"
      }}

      toggleInstall={toggleInstall}
      items={importTemplates}
      title="Imports"
      itemsListTitle="Installed import templates"
      onOpen={onOpen}
      onClickNew={onClickNew}
      description="onCourse imports allow you to import any data as either a once-off event, or a regular data transfer between onCourse and other applications you use. The import script can be customised to transform any input data type into onCourse records. json, xml, and csv importers are available or you can write your own parser."
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
        title: "Custom report",
        category: "Advanced",
        shortDescription: "Create a new report template from scratch"
      }}

      toggleInstall={toggleInstall}
      items={pdfReports}
      title="Reports"
      itemsListTitle="Installed reports"
      onOpen={onOpen}
      onClickNew={onClickNew}
      description="onCourse comes with a large range of reports across all data sets within the system. You can modify the built-in reports or create your own using the free Jaspersoft Studio.
"
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
      title="Report backgrounds"
      itemsListTitle="Available report backgrounds"
      onOpen={onOpen}
      customAddNew={onClickNew}
      description="Upload a background for your reports. This is the simplest way to add your branding to reports or create a custom certificate."
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
    shortDescription: IntegrationTypes[i.type].description,
    hideDot: true
  })) || [], [integrations]);

  const onOpen = id => {
    const type = integrations.find(i => i.id === id)?.type;
    history.push(`/automation/integration/${type}/${id}`);
  };

  const onClickNew = () => {
    history.push("/automation/integrations/list");
  };

  return (
    <CatalogWithSearch
      items={items}
      title="Integrations"
      itemsListTitle="Activated integrations"
      onOpen={onOpen}
      customAddNew={onClickNew}
      description="Here you'll find pre-packaged integrations with third party systems. They will typically be used in an automation to perform some action, for example to trigger a subscription to a learning platform when an enrolment is successful."
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
        title: "Automation",
        category: "Advanced",
        shortDescription: "Create a new automation from scratch"
      }}
      toggleInstall={toggleInstall}
      items={scripts}
      title="Automations"
      itemsListTitle="Installed automations"
      onOpen={onOpen}
      onClickNew={onClickNew}
      description="Automations are the beating heart of onCourse. From basic notifications like sending a payment receipt or a enrolment confirmation through to personalised, complex workflow; automations make Business Process Automation (BPA) possible. Automations can be triggers on any of hundreds of different events or to a schedule. They can interact with external systems, send messages, and create or update data inside onCourse."
    />
  );
};
