import EmailTemplates from "./containers/email-templates/EmailTemplates";
import Integrations from "./containers/integrations/Integrations";
import integrationsForm from "./containers/integrations/components/integrationsFormContainer";
import Scripts from "./containers/scripts/Scripts";
import ExportTemplates from "./containers/export-templates/ExportTemplates";
import PdfReports from "./containers/pdf-reports/PdfReports";
import PdfBackgrounds from "./containers/pdf-backgrounds/PdfBackgrounds";
import ImportTemplates from "./containers/import-templates/ImportTemplates";

const automationRoutes = [
  {
    path: "/automation/email-templates/:id?",
    url: "/automation/email-templates",
    noMenuLink: true,
    main: EmailTemplates
  },
  {
    path: "/automation/export-templates/:id?",
    url: "/automation/export-templates",
    noMenuLink: true,
    main: ExportTemplates
  },
  {
    path: "/automation/pdf-reports/:id?",
    url: "/automation/pdf-reports",
    noMenuLink: true,
    main: PdfReports
  },
  {
    path: "/automation/pdf-backgrounds/:id?",
    url: "/automation/pdf-backgrounds",
    noMenuLink: true,
    main: PdfBackgrounds
  },
  {
    title: "Integrations",
    path: "/automation/integrations/list",
    url: "/automation/integrations/list",
    noMenuLink: true,
    main: Integrations
  },
  {
    path: "/automation/integrations/:action/:type/:name?",
    url: "/automation/integrations",
    noMenuLink: true,
    customAppBar: true,
    main: integrationsForm
  },
  {
    title: "Scripts",
    path: "/automation/script/:id?",
    url: "/automation/script",
    main: Scripts,
    group: "Preferences"
  },
  {
    path: "/automation/import-templates/:id?",
    url: "/automation/import-templates",
    noMenuLink: true,
    main: ImportTemplates
  }
];

export default automationRoutes;
