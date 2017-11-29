import React from 'react';
import {connect, Dispatch} from "react-redux";
import classnames from "classnames";
import {URL} from "../../../../../routes";
import {Theme, Layout} from "../../../../../model";
import {ThemeSettings} from "../components/ThemeSettings";
import {addTheme, deleteTheme, saveTheme} from "../actions";
import {SidebarList} from "../../../../../components/Sidebar/SidebarList";
import {State} from "../../../../../reducers/state";
import {showModal} from "../../../../../common/containers/modal/actions";

interface Props {
  themes: Theme[];
  layouts: Layout[];
  match: any;
  onEditSettings: (themeId, settings) => any;
  onDeleteTheme: (title) => any;
  onAddTheme: () => any;
  history: any;
  fetching: boolean;
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
    const {onAddTheme} = this.props;
    onAddTheme();
  }

  render() {
    const {themes, match, onEditSettings, onDeleteTheme, showModal, fetching, layouts} = this.props;
    const activeTheme = match.params.id && themes.find(theme => theme.id == match.params.id);

    return (
      <div className={classnames({fetching})}>
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
          layouts={layouts}
          onBack={() => this.resetActiveTheme()}
          onEdit={prop => onEditSettings(activeTheme, prop)}
          onDelete={title => onDeleteTheme(title)}
          showModal={showModal}
        />
        }
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  themes: state.theme.items,
  layouts: state.theme.layouts,
  fetching: state.fetching,
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onEditSettings: (theme, settings) => dispatch(saveTheme(theme.id, {...theme, ...settings})),
    onDeleteTheme: title => dispatch(deleteTheme(title)),
    onAddTheme: () => dispatch(addTheme()),
    showModal: props => dispatch(showModal(props)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ThemesSidebar);
