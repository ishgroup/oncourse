import { combineEpics } from "redux-observable";
import { EpicGetEntityRelationTypes } from "./EpicGetEntityRelationTypes";
import { EpicUpdateEntityRelationTypes } from "./EpicUpdateEntityRelationTypes";
import { EpicDeleteEntityRelationType } from "./EpicDeleteEntityRelationType";

export const EpicEntityRelationTypes = combineEpics(
    EpicGetEntityRelationTypes,
    EpicUpdateEntityRelationTypes,
    EpicDeleteEntityRelationType
);