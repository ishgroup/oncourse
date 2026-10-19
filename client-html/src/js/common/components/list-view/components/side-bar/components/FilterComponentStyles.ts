import { makeAppStyles } from 'ish-ui';

export const useFilterStyles = makeAppStyles<void, 'collapseWrapper' | 'checkboxLabel' | 'deleteButton'>()((theme, params, classes) => ({
  content: {
    "&:not(:hover)[data-selected],&:not(:hover)[data-focused],&:not(:hover)[data-selected][data-focused]": {
      backgroundColor: 'unset'
    },
    [`&[data-expanded] .${classes.collapseWrapper}`]: {
      transform: "rotate(180deg)"
    }
  },
  label: {
    cursor: "pointer",
    userSelect: "none"
  },
  collapseWrapper: {
    transition: `transform ${theme.transitions.duration.shortest}ms ${theme.transitions.easing.easeInOut}`,
  },
  checkbox: {
    height: "1em",
    width: "1em",
    marginRight: theme.spacing(0.5),
  },
  checkboxFontSize: {
    fontSize: "18px"
  },
  labelRoot: {
    [`& .${classes.checkboxLabel}`]: {
      fontSize: "12px",
    }
  },
  checkboxLabel: {},
  root: {
    display: "flex",
    alignItems: "center",
    [`&:hover .${classes.deleteButton}`]: {
      visibility: "visible"
    },
    height: theme.spacing(3)
  },
  deleteButton: {
    visibility: "hidden",
    fontSize: "20px",
    height: "30px",
    width: "30px",
    padding: `${theme.spacing(0.5)}`,
    marginLeft: 'auto'
  }
}));