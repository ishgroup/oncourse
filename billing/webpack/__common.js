const webpack = require('webpack');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const { styles } = require( '@ckeditor/ckeditor5-dev-utils' );

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
      publicPath: "/",
      filename: "billing.js"
    },
    mode: mode,
    resolve: {
      modules: [
        path.resolve(dirname, 'node_modules'),
        path.resolve(dirname, 'build/generated-sources'),
        path.resolve(dirname, 'src/js'),
        path.resolve(dirname, 'src/dev'),
        path.resolve(dirname, 'src/scss'),
        path.resolve(dirname, 'src/images'),
        path.resolve(dirname, 'src/test'),
      ],
      extensions: [".ts", ".tsx", ".js", ".css", ".png"]
    },
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          loader: 'awesome-typescript-loader',
          include: [
            path.resolve(dirname, 'build/generated-sources'),
            path.resolve(dirname, "src/js"),
            path.resolve(dirname, "src/dev"),
          ],
        },
      ]
    },
    plugins: [
      new MiniCssExtractPlugin({filename: "billing.css"}),
      _DefinePlugin('development', options.BUILD_NUMBER),
    ],
    devServer: {
      inline: true,
      port: 8081
    },
    devtool: 'source-map',
  };
  _main.module.rules = [..._main.module.rules, ..._styleModule(dirname)];
  return _main;
};

const _styleModule = (dirname) => {
  return [
    {
      test: /\.(jpg|jpeg|gif|png)$/,
      use: [{
        loader: 'file-loader',
        options: {
          name: '[name].[ext]'
        },
      }]
    },
    {
      test: /\.(otf|eot|ttf|woff|woff2|svg)$/,
      loader: 'file-loader',
      options: {
        name: "fonts/[name].[ext]",
        publicPath: "./"
      },
      exclude: [
        /ckeditor5-[^/\\]+[/\\]theme[/\\]icons[/\\][^/\\]+\.svg$/,
      ]
    },
    {
      test: /ckeditor5-[^/\\]+[/\\]theme[/\\]icons[/\\][^/\\]+\.svg$/,
      use: [ 'raw-loader' ]
    },
    {
      test: /ckeditor5-[^/\\]+[/\\]theme[/\\].+\.css$/,
      use: [
        MiniCssExtractPlugin.loader,
        'css-loader',
        {
          loader: 'postcss-loader',
          options: styles.getPostCssConfig( {
            themeImporter: {
              themePath: require.resolve( '@ckeditor/ckeditor5-theme-lark' )
            },
            minify: true
          } )
        }
      ]
    },
    {
      test: /\.s?css$/,
      use: [MiniCssExtractPlugin.loader,'css-loader', 'sass-loader'],
      exclude: [
        /ckeditor5-[^/\\]+[/\\]theme[/\\].+\.css$/,
      ]
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
