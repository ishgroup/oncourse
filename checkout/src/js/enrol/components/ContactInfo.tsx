import React from "react";
import {Concession, StudentMembership} from "../../model";
import {ContactState} from "../../services/IshState";

interface Props {
  contact: ContactState;
  controls?: any;
  concessions?: Concession[];
  memberships?: StudentMembership[];
  onChangeParent?: (contactId: string) => void;
  onRemoveContact?: (contactId: string) => void;
}

export class ContactInfo extends React.Component<Props, any> {

  handleChangeGuardian(e) {
    e.preventDefault();
    const {onChangeParent, contact} = this.props;
    if (onChangeParent && contact) {
      onChangeParent(contact.id);
    }
  }

  public render() {
    const {contact, controls, concessions, memberships, onRemoveContact, onChangeParent} = this.props;

    return (
      <div className="col-xs-24 student-name">
        <div className="student-info">
          <h3>{`${contact.firstName || ''}  ${contact.lastName || ''} `}</h3>
          <span className="student-email">{contact.email}</span>

          {onChangeParent && contact.parent &&
          <span className="student-affilation">
              child of <strong>{` ${contact.parent.firstName} ${contact.parent.lastName} `}</strong>
              <a className="add-Guardian" href="#" onClick={e => this.handleChangeGuardian(e)}>change...</a>
            </span>
          }

          {onRemoveContact &&
            <span className="student-remove" onClick={() => onRemoveContact(contact.id)}/>
          }

        </div>

        {contact.warning &&
          <div className="student-warning">{contact.warning}</div>
        }

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
