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

export const COLORS = [
  "9e0142", "d53e4f", "f46d43", "fdae61", "fee08b", "ffffbf", "e6f598", "abdda4", "66c2a5", "3288bd", "5e4fa2",
  "a30fe9", "480fec", "d7e90f", "e0e0e0", "bababa", "878787", "4d4d4d", "abd9e9", "74add1", "66bd63", "1a9850",
  "006837", "a50026", "d73027"
];