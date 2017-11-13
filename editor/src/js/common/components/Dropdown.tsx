import React from 'react';
import {UncontrolledDropdown, DropdownToggle, DropdownMenu, DropdownItem} from 'reactstrap';

interface Props {
  items: {key: number, value: string}[];
}

export const Dropdown = (props: Props) => {
  const {items = []} = props;

  return (
    <UncontrolledDropdown>
      <DropdownToggle caret>
        Dropdown
      </DropdownToggle>
      <DropdownMenu>
        {items.map(item =>
          <DropdownItem key={item.key}>{item.value}</DropdownItem>,
        )}
      </DropdownMenu>
    </UncontrolledDropdown>
  );
};
