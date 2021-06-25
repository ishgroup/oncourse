import * as React from "react";
import Content from "../../common/components/layout/Content";
import AvetmissExportForm from "./containers/AvetmissExportForm";

class AvetmissExport extends React.PureComponent<any, any> {
  render() {
    return (
      <div className="root">
        <div className="w-100 relative defaultBackgroundColor">
          <Content>
            <AvetmissExportForm />
          </Content>
        </div>
      </div>
    );
  }
}


export default AvetmissExport;
