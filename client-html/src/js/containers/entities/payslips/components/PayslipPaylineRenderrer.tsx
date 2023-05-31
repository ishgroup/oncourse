import * as React from "react";
import { createStyles } from "@mui/material";
import Grid from "@mui/material/Grid";
import withStyles from "@mui/styles/withStyles";
import PayslipPaylineItem from "./PayslipPaylineItem";
import { formatCurrency } from "../../../../common/utils/numbers/numbersNormalizing";
import { PayLineWithDefer } from "../../../../model/entities/Payslip";

const styles = theme =>
  createStyles({
    deferSwitch: {
      display: "flex",
      alignSelf: "flex-end",
      marginRight: "-12px",
      marginTop: "-4px"
    },
    deleteButtonMargin: {
      margin: theme.spacing(-1, 0, 1, -1)
    },
    twoColumnDeleteButton: {
      top: 0,
      right: 0
    },
    infoContainer: {
      background: theme.palette.background.default,
      borderRadius: "4px",
      padding: theme.spacing(1,2)
    },
    threeColumnCard: {
      marginBottom: "20px",
      padding: "20px"
    },
    infoItem: {
      display: "flex",
      alignItems: "center",
      justifyContent: "flex-end"
    },
    fieldMargin: {
      marginRight: "-10px"
    },
    payLabelOffset: {
      marginLeft: "16px"
    }
  });

class PayslipPaylineRenderrer extends React.PureComponent<any, any> {
  render() {
    const {
 fields, classes, threeColumn, onDelete, paylineLayout, currency
} = this.props;

    const classGroups = {};

    const classGroupsTotal = { "Custom Lines": 0 };

    const customLines = [];

    const shortCurrencySymbol = currency != null ? currency.shortCurrencySymbol : "$";

    fields.forEach((item, index) => {
      const field: PayLineWithDefer = fields.get(index);

      const renderedItem = (
        <PayslipPaylineItem
          key={index}
          index={index}
          item={item}
          onDelete={onDelete}
          field={field}
          threeColumn={threeColumn}
          classes={classes}
          paylineLayout={paylineLayout}
          currency={currency}
        />
      );

      if (field.className) {
        if (typeof classGroupsTotal[field.className] === "number") {
          classGroupsTotal[field.className] += field.quantity * field.value;
        } else {
          classGroupsTotal[field.className] = field.quantity * field.value;
        }

        if (classGroups[field.className]) {
          classGroups[field.className].push(renderedItem);
        } else {
          classGroups[field.className] = [renderedItem];
        }
      } else {
        classGroupsTotal["Custom Lines"] += field.quantity * field.value;

        customLines.push(renderedItem);
      }
    });

    return (
      <Grid container columnSpacing={3}>
        <Grid item xs={12} className="pt-3">
          {Object.keys(classGroups).map((g, i) => (
            <React.Fragment key={g + i}>
              <div className="heading mb-1 money">
                {g}
                {' '}
                (
                {formatCurrency(classGroupsTotal[g], shortCurrencySymbol)}
                )
              </div>
              {classGroups[g].map((t, n) => (
                <React.Fragment key={i + n}>{t}</React.Fragment>
              ))}
            </React.Fragment>
          ))}

          {customLines.length ? (
            <>
              <div className="heading mb-1 money">
                Custom Lines (
                {formatCurrency(classGroupsTotal["Custom Lines"], shortCurrencySymbol)}
                )
              </div>
              {customLines}
            </>
          ) : null}
        </Grid>
      </Grid>
    );
  }
}

export default withStyles(styles)(PayslipPaylineRenderrer) as any;
