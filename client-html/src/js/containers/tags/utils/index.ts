import { Tag } from "@api/model";
import { MenuTag } from "../../../model/tags";

export const getAllTags = (tags: Tag[], res?: Tag[]): Tag[] => {
  const result = res || [];

  for (let i = 0; i < tags.length; i++) {
    result.push(tags[i]);

    if (tags[i].childTags.length) {
      getAllTags(tags[i].childTags, result);
    }
  }

  return result;
};

export const getAllMenuTags = (tags: MenuTag[], res?: MenuTag[], path = ""): MenuTag[] => {
  const result = res || [];

  for (let i = 0; i < tags.length; i++) {
    const tag = tags[i];
    tag.path = path;
    result.push(tag);

    if (tag.children.length) {
      getAllMenuTags(tags[i].children, result, `${path ? `${path} / ` : ""}${(tag.tagBody && tag.tagBody.name) || ""}`);
    }
  }
  return result;
};

export const getTagNamesSuggestions = (tags: Tag[]) => {
  const allTags = getAllTags(tags);

  return allTags.map(i => {
    const name = i.name.replace(/\s/g, "_");

    return {
      token: "Identifier",
      value: name,
      label: name
    };
  });
};
