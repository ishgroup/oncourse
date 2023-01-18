import { grey, yellow } from "@mui/material/colors";
import { alpha, darken } from "@mui/material/styles";
import { ThemeValues, AppTheme } from "../../model/common/Theme";

export const theme: { [K in ThemeValues]: Partial<AppTheme> } = {
  default: {
    heading: {
      color: '#4b6390'
    },
    blog: {
      root: {
        backgroundColor: yellow[100]
      }
    },
    share: {
      exportContainer: {
        backgroundColor: '#302A22'
      },
      closeButton: {
        color: '#f7941d'
      },
      shareButton: {
        color: '#302A22',
        backgroundColor: '#fff',
        backgroundColorHover: alpha('#fff', 0.9)
      },
      color: {
        headerText: '#fff',
        itemText: 'rgba(255,255,255,0.6)',
        selectedItemText: 'rgba(255,255,255,1)',
        customLabel: 'rgba(255,255,255,.5)'
      }
    },
    statistics: {
      coloredHeaderText: {
        color: '#f7941d'
      },
      enrolmentText: {
        color: '#73cba7',
        strokeColor: '#73cba7'
      },
      revenueText: {
        color: '#ffd876',
        strokeColor: '#ffd876'
      },
      rightColumn: {
        color: alpha('rgba(0, 0, 0, 0.87)', 0.4)
      }
    },
    appBar: {
      header: {
        background: "#fbf9f0",
        color: "#222",
      },
      headerAlternate: {
        background: "#fff",
        color: "#222",
      },
    },
    appBarButton: {
      close: {
        color: "#f7941d",
      },
      helpMenu: {
        color: "#f7941d",
      }
    },
    tabList: {
      listContainer: {
        backgroundColor: '#4b6390'
      },
      listItemRoot: {
        color: "#fff"
      },
    },
    table: {
      contrastRow: {
        main: '#f8f8f8',
        light: '#f4f3ec'
      }
    },
    addButtonColor: {
      color: "#f7941d"
    }
  },
  dark: {
    heading: {
      color: '#9bbeff'
    },
    blog: {
      root: {
        backgroundColor: '#303030'
      }
    },
    share: {
      exportContainer: {
        backgroundColor: '#302A22'
      },
      closeButton: {
        color: '#f7941d'
      },
      shareButton: {
        color: '#302A22',
        backgroundColor: '#fff',
        backgroundColorHover: alpha('#fff', 0.9)
      },
      color: {
        headerText: '#fff',
        itemText: 'rgba(255,255,255,0.6)',
        selectedItemText: 'rgba(255,255,255,1)',
        customLabel: 'rgba(255,255,255,.5)'
      }
    },
    statistics: {
      coloredHeaderText: {
        color: '#f7941d'
      },
      enrolmentText: {
        color: '#73cba7',
        strokeColor: '#73cba7'
      },
      revenueText: {
        color: '#ffd876',
        strokeColor: '#ffd876'
      },
      rightColumn: {
        color: alpha('rgba(255, 255, 255, 0.87)', 0.4)
      }
    },
    appBar: {
      header: {
        background: "#1b1b1b",
        color: "#fff",
      },
      headerAlternate: {
        background: "#f7941d",
        color: "#fff",
      },
    },
    appBarButton: {
      close: {
        color: 'white'
      },
      helpMenu: {
        color: '#fff'
      }
    },
    tabList: {
      listContainer: {
        backgroundColor: '#302A22'
      },
      listItemRoot: {
        color: "#fff"
      },
    },
    table: {
      contrastRow: {
        main: '#696969',
        light: '#696969bf'
      }
    },
    addButtonColor: {
      color: darken(grey[400], 0.1)
    }
  },
  monochrome: {
    heading: {
      color: '#000'
    },
    blog: {
      root: {
        backgroundColor: '#eee'
      }
    },
    share: {
      exportContainer: {
        backgroundColor: '#222222'
      },
      closeButton: {
        color: '#eaeaea'
      },
      shareButton: {
        color: '#fff',
        backgroundColor: '#3e8ef7',
        backgroundColorHover: alpha('#3e8ef7', 0.9)
      },
      color: {
        headerText: '#fff',
        itemText: 'rgba(255,255,255,0.6)',
        selectedItemText: 'rgba(255,255,255,1)',
        customLabel: 'rgba(255,255,255,.5)'
      }
    },
    statistics: {
      coloredHeaderText: {
        color: '#222222'
      },
      enrolmentText: {
        color: '#73cba7',
        strokeColor: '#73cba7'
      },
      revenueText: {
        color: '#ffd876',
        strokeColor: '#ffd876'
      },
      rightColumn: {
        color: alpha('#222222', 0.4)
      }
    },
    appBar: {
      header: {
        background: "#fff",
        color: "#000",
      },
      headerAlternate: {
        background: "#f4f4f4",
        color: "#000",
      },
    },
    appBarButton: {
      close: {
        color: '#222222'
      },
      helpMenu: {
        color: '#222222'
      }
    },
    tabList: {
      listContainer: {
        backgroundColor: '#e3e3e3'
      },
      listItemRoot: {
        color: "#000",
      },
    },
    table: {
      contrastRow: {
        main: '#f8f8f8',
        light: '#f4f3ec'
      }
    },
    addButtonColor: {
      color: "#000"
    }
  },
  highcontrast: {
    heading: {
      color: '#424242'
    },
    blog: {
      root: {
        backgroundColor: '#fff'
      }
    },
    share: {
      exportContainer: {
        backgroundColor: '#eee'
      },
      closeButton: {
        color: '#111111'
      },
      shareButton: {
        color: '#fff',
        backgroundColor: '#111111',
        backgroundColorHover: alpha('#fff', 0.9)
      },
      color: {
        headerText: '#000',
        itemText: 'rgba(17,17,17,0.6)',
        selectedItemText: 'rgba(17,17,17,1)',
        customLabel: 'rgba(17,17,17,.5)'
      }
    },
    statistics: {
      coloredHeaderText: {
        color: '#111111'
      },
      enrolmentText: {
        color: '#111111'
      },
      revenueText: {
        color: '#111111'
      },
      rightColumn: {
        color: '#111111'
      }
    },
    appBar: {
      header: {
        background: "#f2f2f2",
        color: "#222",
      },
      headerAlternate: {
        background: "#111",
        color: "#fff",
      },
    },
    appBarButton: {
      close: {
        color: '#222'
      },
      helpMenu: {
        color: '#222'
      }
    },
    tabList: {
      listContainer: {
        backgroundColor: '#292929'
      },
      listItemRoot: {
        color: "#fff"
      },
    },
    table: {
      contrastRow: {
        main: '#f8f8f8',
        light: '#f4f3ec'
      }
    },
    addButtonColor: {
      color: "#111111"
    }
  }
};
