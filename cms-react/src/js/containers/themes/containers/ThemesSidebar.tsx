import React from 'react';
import {connect, Dispatch} from "react-redux";
import {getHistoryInstance} from "../../../history";
import {URL} from "../../../routes";
import {Theme} from "../../../model";
import {ThemeSettings} from "../components/ThemeSettings";
import {deleteTheme, saveTheme} from "../actions/index";
import {SidebarList} from "../../../components/Sidebar/SidebarList";

interface Props {
  themes: Theme[];
  match: any;
  onEditSettings: (themeId, settings) => any;
  onDeleteTheme: (id) => any;
}

class ThemesSidebar extends React.Component<Props, any> {

  goBack() {
    getHistoryInstance().push(URL.CONTENT);
  }

  resetActiveTheme() {
    getHistoryInstance().push(URL.THEMES);
  }

  render() {
    const {themes, match, onEditSettings, onDeleteTheme} = this.props;
    const activeTheme = match.params.id && themes.find(theme => theme.id == match.params.id);


    return (
      <div>
        {!activeTheme &&
          <SidebarList
            items={themes}
            onBack={this.goBack}
            category="themes"
          />
        }

        {activeTheme &&
          <ThemeSettings
            theme={activeTheme}
            onBack={this.resetActiveTheme}
            onEdit={prop => onEditSettings(activeTheme.id, prop)}
            onDelete={id => onDeleteTheme(id)}
          />
        }
      </div>
    );
  }
}

const mapStateToProps = state => ({
  themes: state.theme.items,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onEditSettings: (themeId, settings) => dispatch(saveTheme(themeId, settings)),
    onDeleteTheme: id => dispatch(deleteTheme(id)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ThemesSidebar);
