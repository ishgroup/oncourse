import React from 'react';
import {Input} from 'reactstrap';
import {NavLink} from 'react-router-dom';
import {Page, Block, Theme} from "../../model";
import {IconBack} from "../../common/components/IconBack";

interface Props {
  items: (Page | Block | Theme)[];
  onBack: () => void;
  category: string;
  subTitleKey?: string;
}

export class SidebarList extends React.Component<Props, any> {

  constructor() {
    super();

    this.state = {
      filter: '',
    };
  }

  clickBack(e) {
    const {onBack} = this.props;

    e.preventDefault();
    onBack();
  }

  onChangeFilter(e) {
    this.setState({
      filter: e.target.value,
    });
  }

  render() {
    const {items, category, subTitleKey} = this.props;

    return (
      <ul>
        <li>
          <a href="#" onClick={e => this.clickBack(e)}>
            <IconBack text={category}/>
          </a>
        </li>
        <li>
          <div className="sidebar__settings">
            <Input
              placeholder="Filter"
              name="filter"
              value={this.state.filter}
              onChange={e => this.onChangeFilter(e)}
            />
          </div>
        </li>
        {items && items
          .filter(item => item.title.toLocaleLowerCase().indexOf(this.state.filter.toLocaleLowerCase()) !== -1)
          .map(item => (
            <li key={item.id}>
              <NavLink
                exact={false}
                to={`/${category}/${item.id}`}
                activeClassName="active"
              >
                <span>{item.title}</span>

                {item[subTitleKey] &&
                  <small>{item[subTitleKey]}</small>
                }
              </NavLink>
            </li>
          ))}
      </ul>
    );
  }
}

