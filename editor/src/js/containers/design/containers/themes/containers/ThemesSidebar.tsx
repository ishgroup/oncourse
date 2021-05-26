import React from 'react';
import {connect} from "react-redux";
import {Dispatch} from "redux";
import clsx from "clsx";
import {URL} from "../../../../../routes";
import {Theme, Layout} from "../../../../../model";
import ThemeSettings from "../components/ThemeSettings";
import {addTheme, deleteTheme, saveTheme} from "../actions";
import SidebarList from "../../../../../components/Sidebar/SidebarList";
import {State} from "../../../../../reducers/state";
import {showModal} from "../../../../../common/containers/modal/actions";
import {showNavigation} from "../../../../../common/containers/Navigation/actions";
import {SHOW_MESSAGE} from "../../../../../common/components/message/actions";

interface Props {
  themes: Theme[];
  layouts: Layout[];
  match: any;
  onSaveTheme: (themeId, settings) => any;
  onDeleteTheme: (id) => any;
  onAddTheme: () => any;
  history: any;
  fetching: boolean;
  showError: (title) => any;
  showModal: (props) => any;
  showNavigation: () => any;
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
    const {themes, match, onSaveTheme, onDeleteTheme, showError, showModal, fetching, layouts, showNavigation} = this.props;
    const activeTheme = match.params.id && themes.find(theme => theme.id == match.params.id);

    return (
      <div className={clsx(fetching && "fetching")}>
        {!activeTheme &&
        <SidebarList
          items={themes}
          category="themes"
          onBack={() => this.goBack()}
          onAdd={() => this.onAddTheme()}
          showNavigation={showNavigation}
        />
        }

        {activeTheme &&
        <ThemeSettings
          theme={activeTheme}
          themes={themes}
          layouts={layouts}
          onBack={() => this.resetActiveTheme()}
          onSaveTheme={prop => onSaveTheme(activeTheme, prop)}
          showError={showError}
          onDelete={id => onDeleteTheme(id)}
          showModal={showModal}
          showNavigation={showNavigation}
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
    onSaveTheme: (theme, settings) => dispatch(saveTheme(theme.id, {...theme, ...settings})),
    showError: title => dispatch(
      {
        type: SHOW_MESSAGE,
        payload: {message: title, success: false},
      }
    ),
    onDeleteTheme: title => dispatch(deleteTheme(title)),
    onAddTheme: () => dispatch(addTheme()),
    showModal: props => dispatch(showModal(props)),
    showNavigation: () => dispatch(showNavigation()),
  };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ThemesSidebar as any);
