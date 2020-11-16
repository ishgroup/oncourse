/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import {
    updateEntityRelationTypes,
    deleteEntityRelationType,
    getEntityRelationTypes
} from "../../actions";
import { getFormValues } from "redux-form";
import { State } from "../../../../reducers/state";
import {EntityRelationCartAction, EntityRelationType} from "@api/model";
import { Fetch } from "../../../../model/common/Fetch";
import EntityRelationTypesForm from "./components/EntityRelationTypesForm";
import getTimestamps from "../../../../common/utils/timestamps/getTimestamps";
import { showConfirm } from "../../../../common/actions";

interface Props {
    getTypes: () => void;
    updateEntityRelationTypes: (entityRelationTypes: EntityRelationType[]) => void;
    deleteEntityRelationType: (id: string) => void;
    entityRelationTypes: EntityRelationType[];
    data: EntityRelationType[];
    timestamps: Date[];
    fetch: Fetch;
    openConfirm?: (onConfirm: any, confirmMessage?: string) => void;
}

class EntityRelationTypes extends React.Component<Props, any> {
    componentDidMount() {
        this.props.getTypes();
    }

    render() {
        const {
            updateEntityRelationTypes,
            deleteEntityRelationType,
            data,
            entityRelationTypes,
            fetch,
            timestamps,
            openConfirm
        } = this.props;

        const created = timestamps && timestamps[0];
        const modified = timestamps && timestamps[1];

        const form = <EntityRelationTypesForm />;

        const componentForm = React.cloneElement(form, {
            created,
            modified,
            openConfirm,
            entityRelationTypes,
            data,
            fetch,
            onUpdate: updateEntityRelationTypes,
            onDelete: deleteEntityRelationType
        });

        return <div>{entityRelationTypes && componentForm}</div>;
    }
}

const mapStateToProps = (state: State) => ({
    data: getFormValues("EntityRelationTypesForm")(state),
    entityRelationTypes: state.preferences.entityRelationTypes,
    timestamps: state.preferences.entityRelationTypes && getTimestamps(state.preferences.entityRelationTypes),
    fetch: state.fetch
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
    return {
        getTypes: () => dispatch(getEntityRelationTypes()),
        updateEntityRelationTypes: (entityRelationTypes: EntityRelationType[]) =>
            dispatch(updateEntityRelationTypes(entityRelationTypes)),
        deleteEntityRelationType: (id: string) => dispatch(deleteEntityRelationType(id)),
        openConfirm: (onConfirm: any, confirmMessage?: string) => dispatch(showConfirm(onConfirm, confirmMessage))
    };
};

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(EntityRelationTypes);
