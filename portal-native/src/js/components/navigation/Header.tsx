import React from 'react';
import { Appbar } from 'react-native-paper';
import { Platform, StyleSheet } from 'react-native';
import { getStatusBarHeight } from 'react-native-status-bar-height';
import { DrawerHeaderProps } from '@react-navigation/drawer/lib/typescript/src/types';

const styles = StyleSheet.create({
  header: {
    backgroundColor: '#666666',
    zIndex: 1,
    ...Platform.OS === 'android' ? { elevation: 1 } : {},
  },
  badge: {
    position: 'absolute',
    right: 52,
    top: 5
  }
});

export const HeaderBase = ({ children }) => (
  <Appbar.Header statusBarHeight={getStatusBarHeight()} style={styles.header}>
    {children}
  </Appbar.Header>
);

export const Header = ({ route, navigation: { openDrawer } }: DrawerHeaderProps) => (
  <HeaderBase>
    <Appbar.Action
      icon="menu"
      color="white"
      onPress={openDrawer}
    />
    <Appbar.Content title={route.name} color="white" />
  </HeaderBase>
);

export default Header;
