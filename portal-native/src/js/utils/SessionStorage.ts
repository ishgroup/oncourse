import AsyncStorage from '@react-native-async-storage/async-storage';
import * as SecureStore from 'expo-secure-store';
import { defaultAxios } from '../constants/DefaultHttpClient';

const TOKEN_KEY = 'token';

export const getToken = async () => {
  const useSS = await SecureStore.isAvailableAsync();
  return useSS ? SecureStore.getItemAsync(TOKEN_KEY) : AsyncStorage.getItem(TOKEN_KEY);
};

export const setToken = async (value: string) => {
  const useSS = await SecureStore.isAvailableAsync();
  defaultAxios.defaults.headers = {
    Authorization: value
  };
  useSS ? await SecureStore.setItemAsync(TOKEN_KEY, value) : await AsyncStorage.setItem(TOKEN_KEY, value);
};
