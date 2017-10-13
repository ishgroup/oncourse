import React from 'react';
import {connect, Dispatch} from "react-redux";
import {URL} from "../../../../../routes";
import {Theme} from "../../../../../model";
import {ThemeSettings} from "../components/ThemeSettings";
import {deleteTheme, saveTheme} from "../actions";
import {SidebarList} from "../../../../../components/Sidebar/SidebarList";
import {showModal} from "../../../../../common/containers/modal/actions";
import {getDefaultTheme} from "../Themes";

interface Props {
  themes: Theme[];
  match: any;
  onEditSettings: (themeId, settings) => any;
  onDeleteTheme: (id) => any;
  history: any;
  showModal: (props) => any;
}

class ThemesSidebar extends React.Component<Props, any> {

  goBack() {
    this.props.history.push(URL.DESIGN);
  }

  resetActiveTheme() {
    this.props.history.push(URL.THEMES);
  }

  onAddTheme() {
    this.props.history.push(`${URL.THEMES}/-1`);
  }

  render() {
    const {themes, match, onEditSettings, onDeleteTheme, showModal} = this.props;
    const activeTheme = match.params.id && (themes.find(theme => theme.id == match.params.id) || getDefaultTheme());

    return (
      <div>
        {!activeTheme &&
        <SidebarList
          items={themes}
          category="themes"
          onBack={() => this.goBack()}
          onAdd={() => this.onAddTheme()}
        />
        }

        {activeTheme &&
        <ThemeSettings
          theme={activeTheme}
          onBack={() => this.resetActiveTheme()}
          onEdit={prop => onEditSettings(activeTheme, prop)}
          onDelete={id => onDeleteTheme(id)}
          showModal={showModal}
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
    onEditSettings: (theme, settings) => dispatch(saveTheme(theme.id, {...theme, ...settings})),
    onDeleteTheme: id => dispatch(deleteTheme(id)),
    showModal: props => dispatch(showModal(props)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ThemesSidebar);
