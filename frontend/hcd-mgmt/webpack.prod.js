import { merge } from 'webpack-merge';
import common from './webpack.common.js';
import { fileURLToPath, URL } from 'node:url';
import path from 'node:path';
import MiniCssExtractPlugin from 'mini-css-extract-plugin';
import { BundleAnalyzerPlugin } from 'webpack-bundle-analyzer';

const __dirname = fileURLToPath(new URL('.', import.meta.url));
const analyze = process.env.ANALYZE === 'true';

export default merge(common, {
  mode: 'production',

  devtool: 'source-map',

  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'js/[name].[contenthash:8].js',
    chunkFilename: 'js/[name].[contenthash:8].js',
    clean: true,
    publicPath: '/',
  },

  plugins: [
    new MiniCssExtractPlugin({
      filename: 'css/[name].[contenthash:8].css',
      chunkFilename: 'css/[name].[contenthash:8].css',
    }),
    ...(analyze ? [new BundleAnalyzerPlugin()] : []),
  ],

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
        elementPlus: {
          test: /[\\/]element-plus[\\/]/,
          name: 'element-plus',
          priority: 20,
          chunks: 'all',
        },
        common: {
          name: 'common',
          minChunks: 2,
          priority: 5,
          chunks: 'all',
          reuseExistingChunk: true,
        },
      },
    },
    runtimeChunk: {
      name: 'runtime',
    },
  },

  performance: {
    hints: 'warning',
    maxEntrypointSize: 512000,
    maxAssetSize: 512000,
  },
});