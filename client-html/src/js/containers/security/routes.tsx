import Settings from "./containers/settings/Settings";
import SecurityLevels from "./containers/security-levels/SecurityLevels";
import UserRoles from "./containers/user-roles/UserRoles";
import Users from "./containers/users/Users";
import ApiTokens from "./containers/api-tokens/ApiTokens";

const securityRoutes = [
  {
    path: "/security/settings",
    url: "/security/settings",
    noMenuLink: true,
    main: Settings
  },
  {
    path: "/security/userRoles/:id?",
    url: "/security/userRoles/:id?",
    noMenuLink: true,
    main: UserRoles
  },
  {
    path: "/security/securityLevels/:name?",
    url: "/security/securityLevels/:name?",
    noMenuLink: true,
    main: SecurityLevels
  },
  {
    path: "/security/users/:id?",
    url: "/security/users/:id?",
    noMenuLink: true,
    main: Users
  },
  {
    path: "/security/api-tokens",
    url: "/security/api-tokens",
    noMenuLink: true,
    main: ApiTokens
  }
];

export default securityRoutes;
