/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../../common/actions/IshAction";
import { IntegrationSchema } from "../../../model/automation/integrations/IntegrationSchema";
import { CatalogItemType } from "../../../model/common/Catalog";
import { GET_INTEGRATIONS_FULFILLED, } from "../actions";
import { GET_EMAIL_TEMPLATES_LIST_FULFILLED } from "../containers/email-templates/actions";
import { GET_EXPORT_TEMPLATES_LIST_FULFILLED } from "../containers/export-templates/actions";
import { GET_IMPORT_TEMPLATES_LIST_FULFILLED } from "../containers/import-templates/actions";
import {
  GET_AUTOMATION_PDF_BACKGROUNDS_LIST_FULFILLED,
  GET_PDF_BACKGROUND_COPY,
  GET_PDF_BACKGROUND_COPY_LIST_FULFILLED
} from "../containers/pdf-backgrounds/actions";
import { GET_AUTOMATION_PDF_REPORTS_LIST_FULFILLED } from "../containers/pdf-reports/actions";
import { GET_SCRIPTS_LIST_FULFILLED, GET_TIMEZONE_FULFILLED } from "../containers/scripts/actions";

export interface AutomationState {
  integration: {
    integrations: IntegrationSchema[];
  };
  script: {
    scripts: CatalogItemType[];
  };
  emailTemplate: {
    emailTemplates: CatalogItemType[];
  };
  exportTemplate: {
    exportTemplates: CatalogItemType[];
  };
  pdfReport: {
    pdfReports: CatalogItemType[];
  };
  pdfBackground: {
    loading: boolean;
    pdfBackgrounds: CatalogItemType[];
  };
  importTemplate: {
    importTemplates: CatalogItemType[];
  };
  timeZone: string;
}

const Initial: AutomationState = {
  integration: {
    integrations: [],
  },
  script: {
    scripts: []
  },
  emailTemplate: {
    emailTemplates: []
  },
  exportTemplate: {
    exportTemplates: []
  },
  pdfReport: {
    pdfReports: []
  },
  pdfBackground: {
    loading: false,
    pdfBackgrounds: []
  },
  importTemplate: {
    importTemplates: []
  },
  timeZone: null,
};

export const automationReducer = (state: AutomationState = Initial, action: IAction<any>): any => {
  switch (action.type) {
    case GET_INTEGRATIONS_FULFILLED: {
      const { integrations } = action.payload;

      return {
        ...state,
        integration: {
          ...state.integration,
          integrations
        }
      };
    }

    case GET_SCRIPTS_LIST_FULFILLED: {
      return {
        ...state,
        script: {
          ...state.script,
          ...action.payload
        }
      };
    }

    case GET_EMAIL_TEMPLATES_LIST_FULFILLED: {
      return {
        ...state,
        emailTemplate: {
          ...state.emailTemplate,
          ...action.payload
        }
      };
    }

    case GET_EXPORT_TEMPLATES_LIST_FULFILLED: {
      return {
        ...state,
        exportTemplate: {
          ...state.exportTemplate,
          ...action.payload
        }
      };
    }

    case GET_AUTOMATION_PDF_REPORTS_LIST_FULFILLED: {
      return {
        ...state,
        pdfReport: {
          ...state.pdfReport,
          ...action.payload
        }
      };
    }

    case GET_AUTOMATION_PDF_BACKGROUNDS_LIST_FULFILLED: {
      return {
        ...state,
        pdfBackground: {
          ...state.pdfBackground,
          ...action.payload
        }
      };
    }

    case GET_PDF_BACKGROUND_COPY: {
      return {
        ...state,
        pdfBackground: {
          ...state.pdfBackground,
          loading: true
        }
      };
    }

    case GET_PDF_BACKGROUND_COPY_LIST_FULFILLED: {
      return {
        ...state,
        pdfBackground: {
          ...state.pdfBackground,
          loading: !action.payload
        }
      };
    }

    case GET_IMPORT_TEMPLATES_LIST_FULFILLED: {
      return {
        ...state,
        importTemplate: {
          ...state.importTemplate,
          ...action.payload
        }
      };
    }
    
    case GET_TIMEZONE_FULFILLED: {
      return {
        ...state,
        ...action.payload
      };
    }
    
    default:
      return state;
  }
};
