import { merge } from 'webpack-merge';
import devConfig from './webpack.dev.js';
import prodConfig from './webpack.prod.js';

const isProduction = process.env.NODE_ENV === 'production';

export default isProduction ? prodConfig : devConfig;