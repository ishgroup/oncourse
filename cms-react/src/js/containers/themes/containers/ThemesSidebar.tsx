import React from 'react';
import {connect, Dispatch} from "react-redux";
import {getHistoryInstance} from "../../../history";
import {URL} from "../../../routes";
import {Theme} from "../../../model";
import {ThemesList} from "../components/ThemesList";

interface Props {
  themes: Theme[];
}

class ThemesSidebar extends React.Component<Props, any> {

  goBack() {
    getHistoryInstance().push(URL.CONTENT);
  }

  render() {
    const {themes} = this.props;

    return (
      <div>
        <ThemesList themes={themes} onBack={this.goBack}/>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  themes: state.themes.items,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ThemesSidebar);
