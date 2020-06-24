const webpack = require('webpack');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

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
  const mode = (options.NODE_ENV && options.NODE_ENV !== 'mock') ? options.NODE_ENV : 'development';

  let _main = {
    entry: [options[KEYS.ENTRY]],
    output: {
      path: path.resolve(dirname, "build"),
      publicPath: "/assets/",
      filename: "react-payment.js"
    },
    mode: mode,
    resolve: {
      modules: [
        path.resolve(dirname, 'node_modules'),
        path.resolve(dirname, 'src/js'),
      ],
      extensions: [".ts", ".tsx", ".js", ".css"]
    },
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          loader: 'ts-loader',
          include: [
            path.resolve(dirname, 'build/generated-sources'),
            path.resolve(dirname, "src/js"),
            path.resolve(dirname, "src/dev"),
          ],
        }
      ]
    },
    plugins: [
      _DefinePlugin('development', options.BUILD_NUMBER),
      new MiniCssExtractPlugin("app.css"),
    ],
    devServer: {
      inline: false
    },
    devtool: 'source-map',
  };
  _main.module.rules = [..._main.module.rules, ..._styleModule(dirname)];
  return _main;
};

const _styleModule = (dirname) => {
  return [
    {
      test: /\.scss$|\.css$/,
      use: ["style-loader", "css-loader", "sass-loader"],
      include: [path.resolve(dirname, "src/scss")]
    },
    {
      test: /\.(otf|eot|ttf|woff|woff2|svg|jpg|jpeg|gif|png)$/,
      use: [{
        loader: 'file-loader',
        options: {
          name: '[name].[ext]'
        }
      }]
    },
    {
      enforce: "pre", test: /\.js$/, loader: "source-map-loader"
    }
  ]
};


/**
 * The DefinePlugin allows you to create global constants which can be configured at compile time.
 */
const _DefinePlugin = (NODE_ENV, BUILD_NUMBER) => {
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
  styleModule: _styleModule,
  DefinePlugin: _DefinePlugin,
};
