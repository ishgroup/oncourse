import { combineEpics } from "redux-observable";
import { EpicDeleteEntityRelationType } from "./EpicDeleteEntityRelationType";
import { EpicGetEntityRelationTypes } from "./EpicGetEntityRelationTypes";
import { EpicUpdateEntityRelationTypes } from "./EpicUpdateEntityRelationTypes";

export const EpicEntityRelationTypes = combineEpics(
    EpicGetEntityRelationTypes,
    EpicUpdateEntityRelationTypes,
    EpicDeleteEntityRelationType
);