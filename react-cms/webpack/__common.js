const webpack = require('webpack');
const ExtractTextPlugin = require("extract-text-webpack-plugin");

const path = require("path");

const _info = (NODE_ENV, SOURCE_MAP, API_ROOT, BUILD_NUMBER) => {
  console.log(`
Build started with following configuration:
===========================================
→ NODE_ENV: ${NODE_ENV}
→ SOURCE_MAP: ${SOURCE_MAP}
→ API_ROOT: ${API_ROOT}
→ BUILD_NUMBER: ${BUILD_NUMBER}
`);
};

const KEYS = {
  ENTRY: "entry"
};

const _common = (dirname, options) => {
  return {
    entry: [options[KEYS.ENTRY]],
    output: {
      path: path.resolve(dirname, 'build', 'dist'),
      publicPath: "/assets/",
      filename: "cms.js"
    },
    resolve: {
      modules: [
        path.resolve(dirname, 'src/js'),
        path.resolve(dirname, 'src/dev'),
        path.resolve(dirname, 'src/scss'),
        path.resolve(dirname, 'node_modules')
      ],
      extensions: [".ts", ".tsx", ".js", ".css", "scss"]
    },
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          loader: 'ts-loader',
          include: [
            path.resolve(dirname, "src/js"),
            path.resolve(dirname, "src/dev"),
          ],
        },
        {
          test: /\.css$/,
          loaders: ExtractTextPlugin.extract({fallback: 'style-loader', use: 'css-loader'}),
          include: [
            path.resolve(dirname, 'node_modules')
          ]
        },
        {
          test: /\.scss$/,
          loaders: ExtractTextPlugin.extract({fallback: 'style-loader', use: ['css-loader', 'sass-loader']}),
          include: [
            path.resolve(dirname, "src/scss"),
          ]
        },
        {
          test: /\.(jpg|jpeg|gif|png)$/,
          loader: 'url-loader?limit=1024&name=images/[name].[ext]'
        },
        {
          test: /\.(woff|woff2|eot|ttf|svg)$/,
          loader: 'url-loader?limit=1024&name=fonts/[name].[ext]'
        },
        {
          enforce: "pre", test: /\.js$/, loader: "source-map-loader"
        }
      ]
    },
    plugins: [
      _DefinePlugin('development', 'http://localhost:10080', options.BUILD_NUMBER),
      new ExtractTextPlugin("[name].css"),
      new webpack.optimize.ModuleConcatenationPlugin(),
    ],
    devServer: {
      inline: false
    },
    devtool: 'source-map',
  }
};


/**
 * The DefinePlugin allows you to create global constants which can be configured at compile time.
 */
const _DefinePlugin = (NODE_ENV, BUILD_NUMBER) => {
  console.log(process.env);
  return new webpack.DefinePlugin({
    'process.env': {
      'NODE_ENV': JSON.stringify(NODE_ENV)
    },
    _APP_VERSION: JSON.stringify(BUILD_NUMBER || "DEV")
  });
};


module.exports = {
  KEYS: KEYS,
  info: _info,
  common: _common,
  DefinePlugin: _DefinePlugin
};
