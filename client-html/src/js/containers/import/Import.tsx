import React from "react";
import Content from "../../common/components/layout/Content";
import ImportForm from "./components/ImportForm";

class Import extends React.PureComponent<any, any> {
  render() {
    return (
      <div className="root">
        <div className="w-100 defaultBackgroundColor relative">
          <Content>
            <ImportForm />
          </Content>
        </div>
      </div>
    );
  }
}

export default Import;
