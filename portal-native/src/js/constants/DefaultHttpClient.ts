import axios from 'axios';
import Constants from 'expo-constants';

const instance = axios.create();

instance.defaults.baseURL = Constants.manifest.extra.enableMockedApi
  ? '/p/'
  : 'https://www.skillsoncourse.com.au/p/';

export const defaultAxios = instance;
