import * as React from "react";
import TabsList, { TabsListItem } from "../../../../common/components/layout/TabsList";
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

class WaitingListEditView extends React.Component<any, any> {
  render() {
    const {
      values,
      isNew,
      isNested,
      classes,
      dispatch,
      dirty,
      form,
      nestedIndex,
      rootEntity,
      twoColumn,
      showConfirm,
      openNestedEditView,
      manualLink
    } = this.props;

    return values ? (
      <TabsList
        items={items}
        itemProps={{
          isNew,
          isNested,
          values,
          classes,
          dispatch,
          dirty,
          form,
          nestedIndex,
          rootEntity,
          twoColumn,
          showConfirm,
          openNestedEditView,
          manualLink
        }}
      />
    ) : null;
  }
}

export default WaitingListEditView;
