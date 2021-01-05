/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from "../../../common/actions/IshAction";
import {
  GET_INTEGRATIONS_FULFILLED,
  UPDATE_INTEGRATION_ITEM_FULFILLED,
  GET_MYOB_AUTH_URL_REQUEST,
  GET_MYOB_AUTH_URL_FULFILLED
} from "../actions";
import { IntegrationSchema } from "../../../model/automation/integrations/IntegrationSchema";
import { CommonListItem } from "../../../model/common/sidebar";
import { GET_SCRIPTS_LIST_FULFILLED } from "../containers/scripts/actions";
import { GET_EXPORT_TEMPLATES_LIST_FULFILLED } from "../containers/export-templates/actions";
import { GET_AUTOMATION_PDF_REPORTS_LIST_FULFILLED } from "../containers/pdf-reports/actions";
import { GET_AUTOMATION_PDF_BACKGROUNDS_LIST_FULFILLED } from "../containers/pdf-backgrounds/actions";
import { GET_IMPORT_TEMPLATES_LIST_FULFILLED } from "../containers/import-templates/actions";
import { GET_EMAIL_TEMPLATES_LIST_FULFILLED } from "../containers/email-templates/actions";

export interface AutomationState {
  integration: {
    integrations: IntegrationSchema[];
    myobAuthUrl: string;
  };
  script: {
    scripts: CommonListItem[];
  };
  emailTemplate: {
    emailTemplates: CommonListItem[];
  };
  exportTemplate: {
    exportTemplates: CommonListItem[];
  };
  pdfReport: {
    pdfReports: CommonListItem[];
  };
  pdfBackground: {
    pdfBackgrounds: CommonListItem[];
  };
  importTemplate: {
    importTemplates: CommonListItem[];
  };
}

const Initial: AutomationState = {
  integration: {
    integrations: [],
    myobAuthUrl: null
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
    pdfBackgrounds: []
  },
  importTemplate: {
    importTemplates: []
  }
};

export const automationReducer = (state: AutomationState = Initial, action: IAction<any>): any => {
  switch (action.type) {
    case GET_INTEGRATIONS_FULFILLED:
    case UPDATE_INTEGRATION_ITEM_FULFILLED: {
      const { integrations } = action.payload;

      return {
        ...state,
        integration: {
          ...state.integration,
          integrations
        }
      };
    }

    case GET_MYOB_AUTH_URL_REQUEST: {
      return {
        ...state,
        integration: {
          ...state.integration,
          myobAuthUrl: null
        }
      };
    }

    case GET_MYOB_AUTH_URL_FULFILLED: {
      const { url, type } = action.payload;

      return {
        ...state,
        integration: {
          ...state.integration,
          [type]: url
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

    case GET_IMPORT_TEMPLATES_LIST_FULFILLED: {
      return {
        ...state,
        importTemplate: {
          ...state.importTemplate,
          ...action.payload
        }
      };
    }

    default:
      return state;
  }
};
