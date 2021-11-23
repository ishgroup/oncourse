import * as React from "react";
import AvetmissExportForm from "./containers/AvetmissExportForm";

class AvetmissExport extends React.PureComponent<any, any> {
  render() {
    return (
      <div className="root">
        <div className="w-100 relative">
          <AvetmissExportForm />
        </div>
      </div>
    );
  }
}

export default AvetmissExport;
