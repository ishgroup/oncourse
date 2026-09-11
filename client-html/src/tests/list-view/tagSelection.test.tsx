import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import { TreeItem } from '@mui/x-tree-view/TreeItem';
import { act, cleanup, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React, { useEffect, useState } from 'react';
import {
  getActiveTags,
  getTagIdsWithActiveDescendants,
  getTagsUpdatedByIdsWithIndeterminate,
  getTagsUpdatedBySelection,
  setIndeterminate
} from '../../js/common/components/list-view/utils/listFiltersUtils';

const tag = (id, name, prefix, children = []) => ({
  tagBody: { id, name, color: 'ffffff', system: false } as any,
  prefix, children, active: false, indeterminate: false
}) as any;

const groupKey = g => g.prefix + g.tagBody.id.toString();
const activeTagsByGroup = tags => new Map(tags.map(t =>
  [groupKey(t), getActiveTags(t.children).map(c => c.tagBody.id.toString())]));
const urlOf = tags => Array.from(new Set(getActiveTags(tags).map(t => t.tagBody.id))).toString();

const Item = ({ item }) => (
  <TreeItem itemId={item.tagBody.id.toString()} label={item.tagBody.name}>
    {item.children.map(c => <Item key={c.tagBody.id} item={c} />)}
  </TreeItem>
);

// mirrors ListTagGroup after the fix
const Group = ({ rootTag, activeTags, onChange = null }) => {
  const [expanded, setExpanded] = useState([]);
  useEffect(() => {
    if (!activeTags.length) return;
    const next = Array.from(new Set([...activeTags, ...getTagIdsWithActiveDescendants(rootTag.children, activeTags)]));
    setExpanded(prev => (next.some(id => !prev.includes(id)) ? next : prev));
  }, [activeTags, rootTag.children]);
  return (
    <div data-group={rootTag.prefix}>
      <SimpleTreeView
        multiSelect checkboxSelection
        expandedItems={expanded} selectedItems={activeTags}
        onSelectedItemsChange={(e, items) => onChange?.(e, items)}
        onExpandedItemsChange={(e, items) => setExpanded(items)}
        selectionPropagation={{ descendants: true, parents: true }}
      >
        {rootTag.children.map(t => <Item key={t.tagBody.id} item={t} />)}
      </SimpleTreeView>
    </div>
  );
};

const dump = () => Array.from(document.querySelectorAll('[data-group]')).map(g =>
  g.getAttribute('data-group') + ': ' + Array.from(g.querySelectorAll('[role=treeitem]')).map(li => {
    const cb: any = li.querySelector('input[type=checkbox]');
    const name = li.getAttribute('id').replace(/^mui-tree-view-\d+-/, '');
    return `${name}=${cb?.getAttribute('data-indeterminate') === 'true' ? 'INDET' : (cb?.checked ? 'CHECKED' : 'off')}`;
  }).join(' ')).join('  |  ');

const renderRestored = async (build, url) => {
  const restored = getTagsUpdatedByIdsWithIndeterminate(build(), url ? url.split(',').map(Number) : []);
  const byGroup = activeTagsByGroup(restored);
  render(<>{restored.map((g, i) => <Group key={i} rootTag={g} activeTags={byGroup.get(groupKey(g))} />)}</>);
  await act(async () => { await new Promise(r => setTimeout(r, 50)); });
  return restored;
};

const applyClick = (group, ids) => {
  const updated = { ...group, children: getTagsUpdatedBySelection(group.children, ids) };
  setIndeterminate(updated);
  return updated;
};

afterEach(cleanup);

it('one child ticked: parent stays indeterminate across a reload', async () => {
  const build = () => [tag(1, 'Tags', '', [tag(10, 'P', '', [tag(101, 'C1', ''), tag(102, 'C2', '')])])];
  const store = [applyClick(build()[0], [101])];
  expect(urlOf(store)).toBe('101');

  await renderRestored(build, '101');
  expect(dump()).toBe(': 10=INDET 101=CHECKED 102=off');
});

it('every ancestor of a selection is expanded, so no parent is read as a childless leaf', async () => {
  const build = () => [tag(1, 'Tags', '', [tag(10, 'P', '', [tag(101, 'C1', '', [tag(1001, 'G1', '')]), tag(102, 'C2', '')])])];

  await renderRestored(build, '1001');

  // P and C1 both hold the selection below them, so both are open rather than read as leaves:
  // C1 has only the selected G1 under it, P still has the untouched C2
  expect(dump()).toBe(': 10=INDET 101=CHECKED 1001=CHECKED 102=off');
});

it('the stored selection does not grow when the tree view reports its own mounting', async () => {
  // 941 holds 943 and 944, and 943 holds 942 - opening the branch mounts descendants of an
  // already selected tag, which the tree view reports back as a selection change with no event
  const build = () => [tag(1, 'Tags', '', [
    tag(941, 'P', '', [tag(943, 'C1', '', [tag(942, 'G1', '')]), tag(944, 'C2', '')])
  ])];

  let url = '943,944';
  const seen = [url];

  for (let n = 0; n < 3; n++) {
    let store = getTagsUpdatedByIdsWithIndeterminate(build(), url.split(',').map(Number));

    const Harness = () => {
      const [tags, setTags] = useState(store);
      return <Group
        rootTag={tags[0]}
        activeTags={activeTagsByGroup(tags).get(groupKey(tags[0]))}
        onChange={(e, items) => {
          if (!e) return;
          const updated = { ...tags[0], children: getTagsUpdatedBySelection(tags[0].children, items.map(Number)) };
          setIndeterminate(updated);
          store = [updated];
          setTags(store);
        }}
      />;
    };

    render(<Harness />);
    await act(async () => { await new Promise(r => setTimeout(r, 100)); });
    url = urlOf(store);
    seen.push(url);
    cleanup();
  }

  expect(seen).toEqual(['943,944', '943,944', '943,944', '943,944']);
});

it('a tick in one group is not shown in another group built from the same tags', async () => {
  const build = () => ['Enrolled', 'Teaching'].map(p =>
    tag(1, 'Course', p, [tag(10, 'P', p, [tag(101, 'C1', p), tag(102, 'C2', p)])]));

  const store = build();
  const live = activeTagsByGroup([applyClick(store[0], [101]), store[1]]);

  expect(live.get('Enrolled1')).toEqual(['101']);
  expect(live.get('Teaching1')).toEqual([]);
});

it('ticking a collapsed tag selects the children it has not mounted yet', async () => {
  // P is closed, so the tree view can only report P itself when its checkbox is ticked
  const build = () => [tag(1, 'Tags', '', [
    tag(10, 'P', '', [tag(101, 'C1', '', [tag(1001, 'G1', '')]), tag(102, 'C2', '')]),
    tag(20, 'Q', '')
  ])];

  let store = build();

  const Harness = () => {
    const [tags, setTags] = useState(store);
    return <Group
      rootTag={tags[0]}
      activeTags={activeTagsByGroup(tags).get(groupKey(tags[0]))}
      onChange={(e, items) => {
        if (!e) return;
        const updated = { ...tags[0], children: getTagsUpdatedBySelection(tags[0].children, items.map(Number)) };
        setIndeterminate(updated);
        store = [updated];
        setTags(store);
      }}
    />;
  };

  render(<Harness />);
  const user = userEvent.setup();

  const p = screen.getAllByRole('treeitem').find(li => li.getAttribute('id').endsWith('-10'));
  expect(p.getAttribute('aria-expanded')).toBe('false');
  await user.click(p.querySelector('input[type=checkbox]') as any);
  await act(async () => { await new Promise(r => setTimeout(r, 50)); });

  // the whole subtree is stored, not just the tag that was on screen
  expect(urlOf(store)).toBe('10,101,1001,102');
  expect(dump()).toBe(': 10=CHECKED 101=CHECKED 1001=CHECKED 102=CHECKED 20=off');
});

it('unticking one child clears the parent without clearing its siblings', async () => {
  const build = () => [tag(1, 'Tags', '', [tag(10, 'P', '', [tag(101, 'C1', ''), tag(102, 'C2', '')])])];

  let store = [{ ...build()[0], children: getTagsUpdatedBySelection(build()[0].children, [10, 101, 102]) }];

  const Harness = () => {
    const [tags, setTags] = useState(store);
    return <Group
      rootTag={tags[0]}
      activeTags={activeTagsByGroup(tags).get(groupKey(tags[0]))}
      onChange={(e, items) => {
        if (!e) return;
        const updated = { ...tags[0], children: getTagsUpdatedBySelection(tags[0].children, items.map(Number)) };
        setIndeterminate(updated);
        store = [updated];
        setTags(store);
      }}
    />;
  };

  render(<Harness />);
  await act(async () => { await new Promise(r => setTimeout(r, 50)); });

  const user = userEvent.setup();
  const c1 = screen.getAllByRole('treeitem').find(li => li.getAttribute('id').endsWith('-101'));
  await user.click(c1.querySelector('input[type=checkbox]') as any);
  await act(async () => { await new Promise(r => setTimeout(r, 50)); });

  expect(urlOf(store)).toBe('102');
  expect(dump()).toBe(': 10=INDET 101=off 102=CHECKED');
});
