import { OutputType } from "@api/model";
import { TemplateOutputDisplayName } from "../../../../../../model/common/Share";

export const getTemplateOutputDisplayName = (type: OutputType): TemplateOutputDisplayName => {
  switch (type) {
    case "csv": {
      return "Excel";
    }
    case "json": {
      return "JSON";
    }
    case "xml": {
      return "XML";
    }
    case "ics": {
      return "Calendar";
    }
    case "txt": {
      return "Text";
    }
    case "pdf": {
      return "PDF";
    }
  }
};
