/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React from "react";
import NameForm from "./NameForm";
import TemplateForm from "./TemplateForm";
import ContactForm from "./ContactForm";
import OrganisationForm from "./OrganisationForm";
import FinishPage from "./FinishPage";

export interface TabsListItem {
  label: string;
  component: (props: any) => React.ReactNode;
  labelAdornment?: React.ReactNode;
  expandable?: boolean;
}

export const items: TabsListItem[] = [
  {
    label: "Site name",
    component: () => <NameForm/>
  },
  {
    label: "Templates",
    component: () => <TemplateForm/>
  },
  {
    label: "Contact",
    component: () => <ContactForm/>
  },
  {
    label: "Organisation",
    component: () => <OrganisationForm/>
  },
  {
    label: "All done!",
    component: () => <FinishPage/>
  },
];
