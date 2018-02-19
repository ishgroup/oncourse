import React from 'react';
import {Input, Button} from 'reactstrap';
import {NavLink} from 'react-router-dom';
import {Page, Block, Theme} from "../../model";
import {IconBack} from "../../common/components/IconBack";

type Item = (Page | Block | Theme);

interface Props {
  items: Item[];
  onBack: () => void;
  category: string;
  subTitleKey?: string;
  idKey?: string;
  subTitleFilterFunc?: (items, parent?) => any;
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
    const {items, category, subTitleKey, subTitleFilterFunc, onAdd, idKey = 'id'} = this.props;
    const reg = new RegExp(this.state.filter.replace('(', '\\(').replace(')', '\\)'), 'gi');

    const getSubtitle = (item: Item) => (
      subTitleFilterFunc ? subTitleFilterFunc(item[subTitleKey], item) : item[subTitleKey]
    );

    // apply filter to title and subtitle
    const applyFilter = (item: Item): boolean => (
      item.title && item.title.toLocaleLowerCase().indexOf(this.state.filter.toLocaleLowerCase()) !== -1 ||
      item[subTitleKey] && getSubtitle(item).toLocaleLowerCase().indexOf(this.state.filter.toLocaleLowerCase()) !== -1
    );

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

        {items &&
        <li>
          <ul className="sidebar__list">
            {items
              .filter(item => applyFilter(item))
              .map(item => (
                <li key={item[idKey]}>
                  <NavLink
                    exact={false}
                    to={`/${category}/${item[idKey]}`}
                    activeClassName="active"
                  >
                    {this.state.filter &&
                    <span>
                      <span
                        dangerouslySetInnerHTML={{__html: item.title.replace(reg, str => (`<mark>${str}</mark>`))}
                    }/>

                      {item[subTitleKey] &&
                        <small
                          dangerouslySetInnerHTML={{
                            __html: getSubtitle(item).replace(reg, str => (`<mark>${str}</mark>`)),
                          }}
                        />
                      }
                      </span>
                    }

                    {!this.state.filter &&
                    <span>
                      <span>{item.title}</span>

                      {item[subTitleKey] &&
                        <small>{getSubtitle(item)}</small>
                      }
                    </span>
                    }

                  </NavLink>

                </li>
              ))}
          </ul>
        </li>
        }

      </ul>
    );
  }
}

