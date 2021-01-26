import { yellow } from "@material-ui/core/colors";
import { fade } from "@material-ui/core/styles/colorManipulator";
import { ThemeValues, AppTheme } from "../../models/Theme";

export const theme: { [K in ThemeValues]: Partial<AppTheme> } = {
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
  dark: {
    heading: {
      color: "#9bbeff"
    },
    blog: {
      root: {
        backgroundColor: "#303030"
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
        color: fade("rgba(255, 255, 255, 0.87)", 0.4)
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
        main: "#696969",
        light: "#696969bf"
      }
    }
  },
  monochrome: {
    heading: {
      color: "#222222"
    },
    blog: {
      root: {
        backgroundColor: "#eee"
      }
    },
    share: {
      exportContainer: {
        backgroundColor: "#222222"
      },
      closeButton: {
        color: "#eaeaea"
      },
      shareButton: {
        color: "#fff",
        backgroundColor: "#3e8ef7",
        backgroundColorHover: fade("#3e8ef7", 0.9)
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
        color: "#222222"
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
        color: fade("#222222", 0.4)
      }
    },
    appBarButton: {
      close: {
        color: "#222222"
      },
      helpMenu: {
        color: "#222222"
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
  highcontrast: {
    heading: {
      color: "#424242"
    },
    blog: {
      root: {
        backgroundColor: "#fff"
      }
    },
    share: {
      exportContainer: {
        backgroundColor: "#eee"
      },
      closeButton: {
        color: "#111111"
      },
      shareButton: {
        color: "#fff",
        backgroundColor: "#111111",
        backgroundColorHover: fade("#fff", 0.9)
      },
      color: {
        headerText: "#000",
        itemText: "rgba(17,17,17,0.6)",
        selectedItemText: "rgba(17,17,17,1)",
        customLabel: "rgba(17,17,17,.5)"
      }
    },
    statistics: {
      coloredHeaderText: {
        color: "#111111"
      },
      enrolmentText: {
        color: "#111111"
      },
      revenueText: {
        color: "#111111"
      },
      rightColumn: {
        color: "#111111"
      }
    },
    appBarButton: {
      close: {
        color: "#fff"
      },
      helpMenu: {
        color: "#fff"
      }
    },
    tabList: {
      listContainer: {
        backgroundColor: "#727272"
      }
    },
    table: {
      contrastRow: {
        main: "#f8f8f8",
        light: "#f4f3ec"
      }
    }
  },
  christmas: {
    heading: {
      color: "#165B33"
    },
    blog: {
      root: {
        backgroundColor: "transparent"
      }
    },
    share: {
      exportContainer: {
        backgroundColor: "#eee"
      },
      closeButton: {
        color: "#B3000C"
      },
      shareButton: {
        color: "#fff",
        backgroundColor: "#B3000C",
        backgroundColorHover: fade("#fff", 0.9)
      },
      color: {
        headerText: "#000",
        itemText: "rgba(0,0,0,0.6)",
        selectedItemText: "rgba(0,0,0,1)",
        customLabel: "rgba(0,0,0,.5)"
      }
    },
    statistics: {
      coloredHeaderText: {
        color: "#00B32C"
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
        color: "#fff"
      },
      helpMenu: {
        color: "#fff"
      }
    },
    tabList: {
      listContainer: {
        backgroundColor: "#146B3A"
      }
    },
    table: {
      contrastRow: {
        main: "#f8f8f8",
        light: "#f4f3ec"
      }
    }
  }
};
