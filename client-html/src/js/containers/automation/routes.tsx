import EmailTemplates from "./containers/email-templates/EmailTemplates";
import integrationsForm from "./containers/integrations/components/integrationsFormContainer";
import Scripts from "./containers/scripts/Scripts";
import ExportTemplates from "./containers/export-templates/ExportTemplates";
import PdfReports from "./containers/pdf-reports/PdfReports";
import PdfBackgrounds from "./containers/pdf-backgrounds/PdfBackgrounds";
import ImportTemplates from "./containers/import-templates/ImportTemplates";
import {
  EmailTemplatesCatalog,
  ExportTemplatesCatalog,
  ImportTemplatesCatalog, IntegrationsCatalog, PDFBackgroundsCatalog, PDFReportsCatalog,
  ScriptsCatalog
} from "./containers/Catalogs";
import Integrations from "./containers/integrations/Integrations";

const automationRoutes = [
  {
    path: "/automation/email-templates",
    url: "/automation/email-templates",
    noMenuLink: true,
    main: EmailTemplatesCatalog
  },
  {
    path: "/automation/email-template/:id?",
    url: "/automation/email-template",
    noMenuLink: true,
    main: EmailTemplates
  },
  {
    path: "/automation/export-templates",
    url: "/automation/export-templates",
    noMenuLink: true,
    main: ExportTemplatesCatalog
  },
  {
    path: "/automation/export-template/:id?",
    url: "/automation/export-template",
    noMenuLink: true,
    main: ExportTemplates
  },
  {
    path: "/automation/pdf-reports",
    url: "/automation/pdf-reports",
    noMenuLink: true,
    main: PDFReportsCatalog
  },
  {
    path: "/automation/pdf-report/:id?",
    url: "/automation/pdf-report",
    noMenuLink: true,
    main: PdfReports
  },
  {
    path: "/automation/pdf-backgrounds",
    url: "/automation/pdf-backgrounds",
    noMenuLink: true,
    main: PDFBackgroundsCatalog
  },
  {
    path: "/automation/pdf-background/:id?",
    url: "/automation/pdf-background",
    noMenuLink: true,
    main: PdfBackgrounds
  },
  {
    path: "/automation/integrations",
    url: "/automation/integrations",
    noMenuLink: true,
    main: IntegrationsCatalog
  },
  {
    path: "/automation/integrations/list",
    url: "/automation/integrations/list",
    noMenuLink: true,
    main: Integrations
  },
  {
    path: "/automation/integration/:type/:id",
    url: "/automation/integration",
    noMenuLink: true,
    customAppBar: true,
    main: integrationsForm
  },
  {
    path: "/automation/scripts",
    url: "/automation/scripts",
    noMenuLink: true,
    main: ScriptsCatalog
  },
  {
    path: "/automation/script/:id",
    url: "/automation/script",
    main: Scripts
  },
  {
    path: "/automation/import-templates",
    url: "/automation/import-templates",
    noMenuLink: true,
    main: ImportTemplatesCatalog
  },
  {
    path: "/automation/import-template/:id?",
    url: "/automation/import-template",
    noMenuLink: true,
    main: ImportTemplates
  }
];

export default automationRoutes;
