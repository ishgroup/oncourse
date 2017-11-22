import React from 'react';
import {Input, Button} from 'reactstrap';
import {NavLink} from 'react-router-dom';
import {Page, Block, Theme} from "../../model";
import {IconBack} from "../../common/components/IconBack";

interface Props {
  items: (Page | Block | Theme)[];
  onBack: () => void;
  category: string;
  subTitleKey?: string;
  idKey?: string;
  subTitleFilter?: (items, parent?) => any;
  onAdd?: () => any;
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
    const {items, category, subTitleKey, subTitleFilter, onAdd, idKey = 'id'} = this.props;
    const reg = new RegExp(this.state.filter, 'gi');

    return (
      <ul>

        <li>
          <a href="#" className="link-back" onClick={e => this.clickBack(e)}>
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

        {onAdd &&
          <li>
            <div className="sidebar__settings">
              <Button onClick={onAdd} color="primary"><span className="icon icon-add_circle"/>Add new</Button>
            </div>
          </li>
        }

        {items && items
          .filter(item => item.title.toLocaleLowerCase().indexOf(this.state.filter.toLocaleLowerCase()) !== -1)
          .map(item => (
            <li key={item[idKey]}>
              <NavLink
                exact={false}
                to={`/${category}/${item[idKey]}`}
                activeClassName="active"
              >
                {this.state.filter &&
                  <span dangerouslySetInnerHTML={{__html: item.title.replace(reg, str => (`<mark>${str}</mark>`))}}/>
                }

                {!this.state.filter &&
                  <span>{item.title}</span>
                }

                {item[subTitleKey] &&
                  <small>{subTitleFilter ? subTitleFilter(item[subTitleKey], item) : item[subTitleKey]}</small>
                }
              </NavLink>
            </li>
          ))}

      </ul>
    );
  }
}

