export type RootDrawerParamList = {
  Dashboard: undefined;
  Timetable: undefined;
  Resourses: undefined;
  'My profile': undefined;
  Subscriptions: undefined;
  History: undefined;
  Approvals: undefined;
  Logout: undefined;
};

export type RootStackParamList = {
  NotFound: undefined;
  Login: undefined;
  Root: RootDrawerParamList;
};

export type AppRoute = keyof RootStackParamList;

export type BottomTabParamList = {
  TabOne: undefined;
  TabTwo: undefined;
};

export type TabOneParamList = {
  TabOneScreen: undefined;
};

export type TabTwoParamList = {
  TabTwoScreen: undefined;
};
