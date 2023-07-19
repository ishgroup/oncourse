import * as React from "react";
import TabsList, { TabsListItem } from "../../../../common/components/navigation/TabsList";
import WaitingListGeneral from "./WaitingListGeneral";
import WaitingListNotes from "./WaitingListNotes";
import WaitingListSites from "./WaitingListSites";
import { EditViewProps } from "../../../../model/common/ListView";
import { WaitingList } from "@api/model";

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

const WaitingListEditView: React.FC<EditViewProps<WaitingList>> = props => {
  const {
    values,
    onScroll
  } = props;

  return values ? (
    <TabsList
      onParentScroll={onScroll}
      items={items}
      itemProps={{ ...props }}
    />
  ) : null;
};

export default WaitingListEditView;
