import React from 'react';
import { Appbar } from 'react-native-paper';
import { StyleSheet } from 'react-native';
import { getStatusBarHeight } from 'react-native-status-bar-height';
import { useRootNavigation } from '../../hooks/navigation';

const styles = StyleSheet.create({
  header: {
    backgroundColor: '#666666'
  },
  badge: {
    position: 'absolute',
    right: 52,
    top: 5
  }
});

export const HeaderBase = ({ children }) => {
  return (
    <Appbar.Header statusBarHeight={getStatusBarHeight()} style={styles.header}>
      {children}
    </Appbar.Header>
  );
};

export const Header = ({ scene }) => {
  const { options } = scene.descriptor;
  const title = options.headerTitle !== undefined
    ? options.headerTitle
    : options.title !== undefined
      ? options.title
      : scene.route.name;

  const navigation = useRootNavigation();

  return (
    <HeaderBase>
      <Appbar.Action
        icon="menu"
        color="white"
        onPress={() => navigation.openDrawer()}
      />
      <Appbar.Content title={title} color="white" />
    </HeaderBase>
  );
};

export default Header;
