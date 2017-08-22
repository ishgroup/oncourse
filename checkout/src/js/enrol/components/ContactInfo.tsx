import React from "react";
import {StudentMembership, Concession} from "../../model";
import {ContactState} from "../../services/IshState";

interface Props {
  contact: ContactState;
  controls?: any;
  concessions?: Concession[];
  memberships?: StudentMembership[];
  onChangeParent?: (contactId: string) => void;
}

export class ContactInfo extends React.Component<Props, any> {

  handleChangeGuardian(e) {
    e.preventDefault();

    const {onChangeParent, contact} = this.props;
    onChangeParent(contact.id);
  }

  public render() {
    const {contact, controls, concessions, memberships} = this.props;
    return (
      <div className="col-xs-24 student-name">
        <div className="student-info">
          { `${contact.firstName || ''}  ${contact.lastName || ''} `}
          <span className="student-email">({ contact.email })</span>

          {contact.parent &&
            <span className="student-affilation">
              child of <strong>{` ${contact.parent.firstName} ${contact.parent.lastName} `}</strong>
              <a className="add-Guardian" href="#" onClick={e => this.handleChangeGuardian(e)}>change...</a>
            </span>
          }

        </div>
        {memberships && memberships.map((item, i) => (
          <div className="membership" key={i}><i>{item.name}</i></div>
        ))}
        {concessions && concessions.map((item, i) => (
          <div className="concession" key={i}><i>{item.name}</i></div>
        ))}
        {controls}
      </div>
    );
  }
}