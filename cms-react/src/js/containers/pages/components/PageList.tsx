import React from 'react';
import {NavLink} from 'react-router-dom';
import {Page} from "../../../model";
import {IconBack} from "../../../common/components/IconBack";

interface Props {
  pages: Page[];
  onBack: () => void;
}

export const PagesList = (props: Props) => {
  const {pages, onBack} = props;

  const clickBack = e => {
    e.preventDefault();
    onBack();
  };

  return (
    <ul>
      <li>
        <a href="#" onClick={e => clickBack(e)}>
          <IconBack text={'Pages'}/>
        </a>
      </li>
      {pages.map(page => (
        <li key={page.id}>
          <NavLink
            exact={false}
            to={`/pages/${page.id}`}
            activeClassName="active"
          >
            <span>{page.title}</span>
            <small>{page.url}</small>
          </NavLink>
        </li>
      ))}
    </ul>
  );
};
