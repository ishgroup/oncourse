import { useNavigation } from '@react-navigation/native';
import { DrawerNavigationProp } from '@react-navigation/drawer';
import { RootDrawerParamList } from '../model/types';

export const useRootNavigation = () => useNavigation<DrawerNavigationProp<RootDrawerParamList>>();
