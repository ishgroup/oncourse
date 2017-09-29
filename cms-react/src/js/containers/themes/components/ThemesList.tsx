import React from 'react';
import {NavLink} from 'react-router-dom';
import {Theme} from "../../../model";
import {IconBack} from "../../../common/components/IconBack";

interface Props {
  themes: Theme[];
  onBack: () => void;
}

export const ThemesList = (props: Props) => {
  const {themes, onBack} = props;

  const clickBack = e => {
    e.preventDefault();
    onBack();
  };

  return (
    <ul>
      <li>
        <a href="#" onClick={e => clickBack(e)}>
          <IconBack text={'Themes'}/>
        </a>
      </li>
      {themes.map(theme => (
        <li key={theme.id}>
          <NavLink
            exact={false}
            to={`/themes/${theme.id}`}
            activeClassName="active"
          >
            <span>{theme.title}</span>
          </NavLink>
        </li>
      ))}
    </ul>
  );
};
