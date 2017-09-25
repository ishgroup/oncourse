import React from 'react';
import {NavLink} from 'react-router-dom';
import {Block} from "../../../model";
import {IconBack} from "../../../common/components/IconBack";

interface Props {
  blocks: Block[];
  onBack: () => void;
}

export const BlocksList = (props: Props) => {
  const {blocks, onBack} = props;

  const clickBack = e => {
    e.preventDefault();
    onBack();
  };

  return (
    <ul>
      <li>
        <a href="#" onClick={e => clickBack(e)}>
          <IconBack text={'Blocks'}/>
        </a>
      </li>
      {blocks.map(block => (
        <li key={block.id}>
          <NavLink
            exact={false}
            to={`/blocks/${block.id}`}
            activeClassName="active"
          >
            <span>{block.title}</span>
          </NavLink>
        </li>
      ))}
    </ul>
  );
};
