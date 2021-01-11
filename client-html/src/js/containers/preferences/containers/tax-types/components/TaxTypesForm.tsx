import * as React from "react";
import ClassNames from "clsx";
import Grid from "@material-ui/core/Grid";
import { withStyles } from "@material-ui/core/styles";
import AddIcon from "@material-ui/icons/Add";
import Typography from "@material-ui/core/Typography";
import {
 FieldArray, reduxForm, initialize, SubmissionError, arrayInsert, arrayRemove
} from "redux-form";
import { Tax } from "@api/model";
import isEqual from "lodash.isequal";
import Fab from "@material-ui/core/Fab";
import FormSubmitButton from "../../../../../common/components/form/FormSubmitButton";
import CustomAppBar from "../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import AppBarHelpMenu from "../../../../../common/components/form/AppBarHelpMenu";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";
import { formCommonStyles } from "../../../styles/formCommonStyles";
import TaxTypesRenderer from "./TaxTypesRenderer";
import { getManualLink } from "../../../../../common/utils/getManualLink";
import { idsToString } from "../../../../../common/utils/numbers/numbersNormalizing";

const manualUrl = getManualLink("generalPrefs_taxTypes");

interface Props {
  data: any;
  classes: any;
  created: Date;
  modified: Date;
  taxTypes: Tax[];
  dispatch: any;
  handleSubmit: any;
  onDelete: (id: string) => void;
  onUpdate: (taxTypes: Tax[]) => void;
  assetAccounts: any;
  liabilityAccounts: any;
  dirty: boolean;
  form: string;
  invalid: boolean;
  openConfirm: (onConfirm: any, confirmMessage?: string, confirmButtonText?: string) => void;
}

class TaxTypesBaseForm extends React.Component<Props, any> {
  private resolvePromise;

  private rejectPromise;

  private isPending: boolean;

  constructor(props) {
    super(props);

    props.dispatch(initialize("TaxTypesForm", { types: props.taxTypes }));
  }

  componentWillReceiveProps(nextProps) {
    if (!this.isPending) {
      return;
    }
    if (nextProps.fetch && nextProps.fetch.success === false) {
      this.rejectPromise(nextProps.fetch.formError);
    }
    if (nextProps.fetch && nextProps.fetch.success) {
      this.resolvePromise();
      this.isPending = false;
    }
  }

  findIndex = id => this.props.data.types.findIndex(item => item.id === id);

  getTouchedAndNew = items => {
    const initialLength = this.props.taxTypes.length;
    const newLength = items.length;

    const newItems = items.slice(0, newLength - initialLength);
    const touchedItems = items
      .slice(newLength - initialLength, newLength)
      .filter((item, index) => !isEqual(item, this.props.taxTypes[index]));

    return [...newItems, ...touchedItems];
  };

  onSave = value => {
    this.isPending = true;

    return new Promise((resolve, reject) => {
      this.resolvePromise = resolve;
      this.rejectPromise = reject;
      this.props.onUpdate(this.getTouchedAndNew(value.types));
    })
      .then(() => {
        this.props.dispatch(initialize("TaxTypesForm", { types: this.props.taxTypes }));
      })
      .catch(error => {
        this.isPending = false;

        const errors: any = {
          types: []
        };

        if (error) {
          const index = this.findIndex(error.id);
          errors.types[index] = { [error.propertyName]: error.errorMessage };
        }

        throw new SubmissionError(errors);
      });
  };

  onAddNew = () => {
    const item = {} as Tax;
    item.id = null;
    item.code = "";
    item.editable = true;
    item.systemType = false;
    item.gst = false;
    item.rate = "" as any;
    item.payableAccountId = null;
    item.receivableAccountId = null;
    item.description = "";

    this.props.dispatch(arrayInsert("TaxTypesForm", "types", 0, item));
    const domNode = document.getElementById("types[0].code");
    if (domNode) domNode.scrollIntoView({ behavior: "smooth" });
  };

  onClickDelete = (item, index) => {
    const { onDelete, openConfirm } = this.props;

    const onConfirm = () => {
      this.isPending = true;

      return new Promise((resolve, reject) => {
        this.resolvePromise = resolve;
        this.rejectPromise = reject;

        if (item.id) {
          onDelete(item.id);
        } else {
          this.props.dispatch(arrayRemove("TaxTypesForm", "types", index));
          this.resolvePromise(true);
        }
      })
        .then(clientSideDelete => {
          if (!clientSideDelete) {
            this.props.dispatch(initialize("TaxTypesForm", { types: this.props.taxTypes }));
          }
        })
        .catch(() => {
          this.isPending = false;
        });
    };

    openConfirm(onConfirm, "This item will be removed from types list", "DELETE");
  };

  render() {
    const {
     classes, handleSubmit, data, assetAccounts, liabilityAccounts, dirty, created, modified, invalid, form
    } = this.props;

    return (
      <form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(this.onSave)}>
        <RouteChangeConfirm form={form} when={dirty} />

        <CustomAppBar>
          <Grid container>
            <Grid item xs={12} className={ClassNames("centeredFlex", "relative")}>
              <Fab
                type="button"
                size="small"
                color="primary"
                classes={{
                  sizeSmall: "appBarFab"
                }}
                onClick={() => this.onAddNew()}
              >
                <AddIcon />
              </Fab>
              <Typography color="inherit" className="appHeaderFontSize pl-2" noWrap>
                Tax Types
              </Typography>

              <div className="flex-fill" />

              {data && (
                <AppBarHelpMenu
                  created={created}
                  modified={modified}
                  auditsUrl={`audit?search=~"Tax" and entityId in (${idsToString(data.types)})`}
                  manualUrl={manualUrl}
                />
              )}

              <FormSubmitButton
                disabled={!dirty}
                invalid={invalid}
              />
            </Grid>
          </Grid>
        </CustomAppBar>

        <Grid container className={classes.marginTop}>
          <Grid item sm={12} lg={10}>
            <Grid container>
              {data && (
                <FieldArray
                  name="types"
                  assetAccounts={assetAccounts}
                  liabilityAccounts={liabilityAccounts}
                  component={TaxTypesRenderer}
                  onDelete={this.onClickDelete}
                  classes={classes}
                />
              )}
            </Grid>
          </Grid>
        </Grid>
      </form>
    );
  }
}

const TaxTypesForm = reduxForm({
  onSubmitFail,
  form: "TaxTypesForm"
})(withStyles(formCommonStyles)(TaxTypesBaseForm) as any);

export default TaxTypesForm;
