/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Tag } from "@api/model";

const isParent = (tag: Tag, ID: number) => (tag.id === ID ? true : tag.childTags.find(c => isParent(c, ID)));

export const validateTagsList = (tags: Tag[], value, allValues, props, rootEntity?) => {
  let error;

  if (!tags) {
    return error;
  }

  const rootTagsWithRequirements = {};

  tags.forEach(t => {
    const match = t.requirements.filter(r => (r.type === (rootEntity || props.rootEntity) && (r.mandatory || r.limitToOneTag)));

    if (match.length) {
      rootTagsWithRequirements[t.id] = { name: t.name, requirements: match[0] };
    }
  });

  const usedRootTags = {};

  if (value) {
    value.forEach(i => {
      const match = tags.find(t => isParent(t, i));

      if (match) {
        if (usedRootTags[match.id]) {
          usedRootTags[match.id].push(match);
        } else {
          usedRootTags[match.id] = [match];
        }
      }
    });
  }

  Object.keys(rootTagsWithRequirements).forEach(u => {
    if (!usedRootTags[u] && rootTagsWithRequirements[u].requirements.mandatory) {
      error = `The ${
        rootTagsWithRequirements[u].name
      } Tag is mandatory. Modify your tag settings before removing this tag`;
    }

    if (usedRootTags[u] && rootTagsWithRequirements[u].requirements.limitToOneTag && usedRootTags[u].length > 1) {
      error = `The ${rootTagsWithRequirements[u].name} Tag group can be used only once`;
    }
  });

  return error;
};
