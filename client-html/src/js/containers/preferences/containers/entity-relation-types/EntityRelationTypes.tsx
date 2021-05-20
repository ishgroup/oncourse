/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { getFormValues } from "redux-form";
import { Discount, EntityRelationType } from "@api/model";
import {
    updateEntityRelationTypes,
    deleteEntityRelationType,
    getEntityRelationTypes
} from "../../actions";
import { State } from "../../../../reducers/state";
import { Fetch } from "../../../../model/common/Fetch";
import EntityRelationTypesForm from "./components/EntityRelationTypesForm";
import getTimestamps from "../../../../common/utils/timestamps/getTimestamps";
import { showConfirm } from "../../../../common/actions";
import { getDiscounts } from "../../../entities/discounts/actions";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";

interface Props {
    getTypes: () => void;
    getDiscounts: () => void;
    updateEntityRelationTypes: (entityRelationTypes: EntityRelationType[]) => void;
    deleteEntityRelationType: (id: string) => void;
    entityRelationTypes: EntityRelationType[];
    data: EntityRelationType[];
    discounts: Discount[];
    timestamps: Date[];
    fetch: Fetch;
    openConfirm?: ShowConfirmCaller;
}

class EntityRelationTypes extends React.Component<Props, any> {
    componentDidMount() {
        this.props.getTypes();
        this.props.getDiscounts();
    }

    render() {
        const {
            updateEntityRelationTypes,
            deleteEntityRelationType,
            data,
            discounts,
            entityRelationTypes,
            fetch,
            timestamps,
            openConfirm
        } = this.props;

        const created = timestamps && timestamps[0];
        const modified = timestamps && timestamps[1];

        const discountsMap = discounts && discounts.map(item => ({
            value: Number(item.id),
            label: item.name
        }));

        const form = <EntityRelationTypesForm />;

        const componentForm = React.cloneElement(form, {
            created,
            modified,
            openConfirm,
            entityRelationTypes,
            data,
            discountsMap,
            fetch,
            onUpdate: updateEntityRelationTypes,
            onDelete: deleteEntityRelationType
        });

        return <div>{entityRelationTypes && discounts && componentForm}</div>;
    }
}

const mapStateToProps = (state: State) => ({
    data: getFormValues("EntityRelationTypesForm")(state),
    entityRelationTypes: state.preferences.entityRelationTypes,
    timestamps: state.preferences.entityRelationTypes && getTimestamps(state.preferences.entityRelationTypes),
    discounts: state.discounts.items,
    fetch: state.fetch
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
    getTypes: () => dispatch(getEntityRelationTypes()),
    getDiscounts: () => dispatch(getDiscounts("")),
    updateEntityRelationTypes: (entityRelationTypes: EntityRelationType[]) =>
      dispatch(updateEntityRelationTypes(entityRelationTypes)),
    deleteEntityRelationType: (id: string) => dispatch(deleteEntityRelationType(id)),
    openConfirm: props => dispatch(showConfirm(props))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(EntityRelationTypes);
