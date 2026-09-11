import useEventCallback from '@mui/utils/useEventCallback';
import $t from '@t';
import clsx from 'clsx';
import * as React from 'react';
import { useState } from 'react';
import { withStyles } from 'tss-react/mui';
import { FilterGroup } from '../../../../../model/common/ListView';
import { FormMenuTag } from '../../../../../model/tags';
import HamburgerMenu from '../../../layout/swipeable-sidebar/components/HamburgerMenu';
import { VARIANTS } from '../../../layout/swipeable-sidebar/utils';
import ChecklistsFilters from './components/ChecklistsFilters';
import FilterGroupComp from './components/FilterGroup';
import { DeleteFilterHandler } from './components/FilterItem';
import FiltersSwitcher from './components/FiltersSwitcher';
import ListTagGroups from './components/ListTagGroups';
import StubFilterItem from './components/StubFilterItem';

const styles = theme =>
  ({
    root: {
      padding: theme.spacing(0, 0, 2, 2)
    },
    header: {
      height: "64px",
      display: "flex",
      justifyContent: "space-between",
      flexDirection: "row",
      alignItems: "center",
      padding: theme.spacing(0, 3)
    },
    hamburgerMenu: {
      position: "relative",
      zIndex: theme.zIndex.drawer + 1
    }
  });

interface Props {
  classes?: any;
  onChangeFilters: any;
  filterGroups: FilterGroup[]
  deleteFilter: any;
  rootEntity: string;
  filterEntity?: string;
  savingFilter: any;
  fetching: boolean;
}

const SideBar = (props: Props) => {
  const {
   classes, onChangeFilters, filterGroups, deleteFilter, rootEntity, filterEntity, savingFilter, fetching
  } = props;

  const [filterBy, setFilterBy] = useState(0);

  const hasCustomFilters = filterGroups.some(i => i.title === "Custom Filters");

  // `onChangeFilters` and `deleteFilter` are rebuilt on every ListView render, so they are wrapped
  // here once - the memoized filter, tag and checklist subtrees below then only re-render on data
  // changes instead of on every keystroke in the list search
  const UpdateFilters = useEventCallback((index: string, value: boolean) => {
    const groupIndex = Number(index.split("/")[0]);
    const filterIndex = Number(index.split("/")[1]);

    const clone = filterGroups.map((fg, gi) => ({
      ...fg,
      filters: fg.filters.map((f, fi) => ({
        ...f,
        active: gi === groupIndex && fi === filterIndex ? value : f.active
      }))
    }));
    onChangeFilters(clone, "filters");
  });

  const onDeleteFilter = useEventCallback<DeleteFilterHandler>(
    (id, entity, checked, isPrivate) => deleteFilter(id, entity, checked, isPrivate)
  );

  const onChangeTagGroups = useEventCallback((tags: FormMenuTag[], type: string) => onChangeFilters(tags, type));

  const updateCheckedChecklists = useEventCallback((filters: FormMenuTag[]) => onChangeFilters(filters, "checkedChecklists"));

  const updateUncheckedChecklists = useEventCallback((filters: FormMenuTag[]) => onChangeFilters(filters, "uncheckedChecklists"));

  return (
    <div>
      <div className={clsx("pl-2", classes.hamburgerMenu)}>
        <HamburgerMenu variant={VARIANTS.temporary} />
      </div>
      <nav className={clsx(classes.root, fetching && "disabled")}>

        <FiltersSwitcher value={filterBy} setValue={setFilterBy} />

        <div className={clsx(filterBy !== 0 && "d-none")}>
          {filterGroups.map((i, index) => (
            <FilterGroupComp
              key={index}
              groupIndex={index}
              deleteFilter={onDeleteFilter}
              rootEntity={rootEntity}
              onUpdate={UpdateFilters}
              title={i.title}
              filters={i.filters}
            />
          ))}

          {savingFilter && !hasCustomFilters && <div className="heading mt-2">{$t('custom_filters')}</div>}

          {savingFilter && <StubFilterItem rootEntity={rootEntity} savingFilter={savingFilter} filterEntity={filterEntity} />}

          <ListTagGroups onChangeTagGroups={onChangeTagGroups} rootEntity={rootEntity} />
        </div>

        <div className={clsx(filterBy !== 1 && "d-none")}>
          <ChecklistsFilters
            updateChecked={updateCheckedChecklists}
            updateUnChecked={updateUncheckedChecklists}
          />
        </div>
      </nav>
    </div>
  );
};
export default withStyles(SideBar, styles);
