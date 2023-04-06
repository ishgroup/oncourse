import * as React from "react";
import TabsList, { TabsListItem } from "../../../../common/components/navigation/TabsList";
import WaitingListGeneral from "./WaitingListGeneral";
import WaitingListNotes from "./WaitingListNotes";
import WaitingListSites from "./WaitingListSites";

const items: TabsListItem[] = [
  {
    label: "General",
    component: props => <WaitingListGeneral {...props} />
  },
  {
    label: "Sites",
    component: props => <WaitingListSites {...props} />
  },
  {
    label: "Notes",
    component: props => <WaitingListNotes {...props} />
  }
];

const WaitingListEditView: React.FC<any> = props => {
  const {
    values,
  } = props;

  return values ? (
    <TabsList
      items={items}
      itemProps={{ ...props }}
    />
  ) : null;
};

export default WaitingListEditView;
