import { yellow } from "@material-ui/core/colors";
import { fade } from "@material-ui/core/styles/colorManipulator";
import { AppTheme } from "./themeInterface";

export const theme: { default: Partial<AppTheme> } = {
  default: {
    heading: {
      color: "#4b6390"
    },
    blog: {
      root: {
        backgroundColor: yellow[100]
      }
    },
    share: {
      exportContainer: {
        backgroundColor: "#302A22"
      },
      closeButton: {
        color: "#f7941d"
      },
      shareButton: {
        color: "#302A22",
        backgroundColor: "#fff",
        backgroundColorHover: fade("#fff", 0.9)
      },
      color: {
        headerText: "#fff",
        itemText: "rgba(255,255,255,0.6)",
        selectedItemText: "rgba(255,255,255,1)",
        customLabel: "rgba(255,255,255,.5)"
      }
    },
    statistics: {
      coloredHeaderText: {
        color: "#f7941d"
      },
      enrolmentText: {
        color: "#73cba7",
        strokeColor: "#73cba7"
      },
      revenueText: {
        color: "#ffd876",
        strokeColor: "#ffd876"
      },
      rightColumn: {
        color: fade("rgba(0, 0, 0, 0.87)", 0.4)
      }
    },
    appBarButton: {
      close: {
        color: "white"
      },
      helpMenu: {
        color: "#fff"
      }
    },
    tabList: {
      listContainer: {
        backgroundColor: "#302A22"
      }
    },
    table: {
      contrastRow: {
        main: "#f8f8f8",
        light: "#f4f3ec"
      }
    }
  },
};
