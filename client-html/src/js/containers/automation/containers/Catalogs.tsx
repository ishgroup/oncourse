/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */


import React, { useMemo } from "react";
import { RouteComponentProps } from "react-router";
import { useAppSelector } from "../../../common/utils/hooks";
import { CatalogItemType } from "../../../model/common/Catalog";
import CatalogWithSearch from "../../../common/components/layout/catalog/CatalogWithSearch";

export const EmailTemplatesCatalog = ({ history }: RouteComponentProps) => {
  const emailTemplates = useAppSelector(state => state.automation.emailTemplate.emailTemplates);

  // TODO: remove on api model change
  const items = useMemo<CatalogItemType[]>(() => emailTemplates?.map((s, index) => ({
    id: s.id,
    title: s.name,
    category: null,
    installed: true,
    enabled: s.grayOut,
    tag: index === 4 ? "New" : index === 5 ? "Popular" : null,
    shortDescription: null
  })) || [], [emailTemplates]);

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
      items={items}
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

  // TODO: remove on api model change
  const items = useMemo<CatalogItemType[]>(() => exportTemplates?.map((s, index) => ({
    id: s.id,
    title: s.name,
    category: null,
    installed: true,
    enabled: s.grayOut,
    tag: index === 4 ? "New" : index === 5 ? "Popular" : null,
    shortDescription: null
  })) || [], [exportTemplates]);

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
      items={items}
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

  // TODO: remove on api model change
  const items = useMemo<CatalogItemType[]>(() => importTemplates?.map((s, index) => ({
    id: s.id,
    title: s.name,
    category: null,
    installed: true,
    enabled: s.grayOut,
    tag: index === 4 ? "New" : index === 5 ? "Popular" : null,
    shortDescription: null
  })) || [], [importTemplates]);

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
      items={items}
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

  // TODO: remove on api model change
  const items = useMemo<CatalogItemType[]>(() => pdfReports?.map((s, index) => ({
    id: s.id,
    title: s.name,
    category: null,
    installed: true,
    enabled: s.grayOut,
    tag: index === 4 ? "New" : index === 5 ? "Popular" : null,
    shortDescription: null
  })) || [], [pdfReports]);

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
      items={items}
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

  // TODO: remove on api model change
  const items = useMemo<CatalogItemType[]>(() => pdfBackgrounds?.map((s, index) => ({
    id: s.id,
    title: s.name,
    category: null,
    installed: true,
    enabled: s.grayOut,
    tag: index === 4 ? "New" : index === 5 ? "Popular" : null,
    shortDescription: null
  })) || [], [pdfBackgrounds]);

  const onOpen = id => {
    history.push(`/automation/pdf-background/${id}`);
  };

  const onClickNew = () => {
    history.push("/automation/pdf-background/new");
  };

  return (
    <CatalogWithSearch
      items={items}
      title="PDF backgrounds"
      itemsListTitle="Available pdf backgrounds"
      onOpen={onOpen}
      customAddNew={onClickNew}
    />
  );
};

export const IntegrationsCatalog = ({ history }: RouteComponentProps) => {
  const integrations:any = useAppSelector(state => state.automation.integration.integrations);

  // TODO: remove on api model change
  const items = useMemo<CatalogItemType[]>(() => integrations?.map((s, index) => ({
    id: s.id,
    title: s.name,
    category: null,
    installed: true,
    enabled: s.grayOut,
    tag: index === 4 ? "New" : index === 5 ? "Popular" : null,
    shortDescription: null
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

// TODO: remove on api model change
const getMockedCategory = index => {
  if (index < 6) {
    return "Course Notifications";
  }
  if (index < 60) {
    return "Payroll";
  }
  return "Rostering";
};

export const ScriptsCatalog = ({ history }: RouteComponentProps) => {
  const scripts = useAppSelector(state => state.automation.script.scripts);

  // TODO: remove on api model change
  const items = useMemo<CatalogItemType[]>(() => scripts?.map((s, index) => ({
    id: s.id,
    title: s.name,
    category: getMockedCategory(index),
    installed: true,
    enabled: s.grayOut,
    tag: index === 4 ? "New" : index === 5 ? "Popular" : null,
    shortDescription: null
  })) || [], [scripts]);

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
      items={items}
      title="Automations"
      itemsListTitle="installed automations"
      onOpen={onOpen}
      onClickNew={onClickNew}
      description="Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ab, assumenda, cum cupiditate dignissimos doloribus iure modi nisi nobis possimus quos ratione recusandae tenetur totam! Aliquam laudantium nesciunt ratione tempora totam!"
    />
  );
};
