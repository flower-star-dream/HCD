import { merge } from 'webpack-merge';
import common from './webpack.common.js';

export default merge(common, {
  mode: 'development',

  devtool: 'eval-cheap-module-source-map',

  devServer: {
    port: 3000,
    open: true,
    historyApiFallback: true,
    hot: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
        // 移除pathRewrite，保持/api前缀以匹配网关路由规则
      },
    },
  },

  optimization: {
    splitChunks: {
      chunks: 'all',
      cacheGroups: {
        vendor: {
          test: /[\\/]node_modules[\\/]/,
          name: 'vendors',
          priority: 10,
          chunks: 'all',
        },
      },
    },
  },
});