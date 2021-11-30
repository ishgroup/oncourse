const webpack = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');

const path = require('path');

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
  ENTRY: 'entry'
};

const _common = (dirname, options) => {
  const mode = options.NODE_ENV || 'development';
  const _main = {
    entry: ['babel-polyfill', 'url-polyfill', 'custom-event-polyfill', options[KEYS.ENTRY]],
    output: {
      path: path.resolve(dirname, 'build'),
      publicPath: '/assets/',
      filename: 'dynamic.js'
    },
    resolve: {
      modules: [
        path.resolve(dirname, 'src/js'),
        path.resolve(dirname, 'src/dev'),
        path.resolve(dirname, 'src/scss'),
        path.resolve(dirname, 'node_modules')
      ],
      fallback: {
        util: require.resolve('util/')
      },
      extensions: ['.ts', '.tsx', '.js', '.css'],
      plugins: [new TsconfigPathsPlugin({ configFile: path.resolve(dirname, './tsconfig.json') })],
    },
    mode,
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          use: [
            {
              loader: 'babel-loader',
              options: {
                presets: ['@babel/preset-react', '@babel/preset-env']
              }
            },
            {
              loader: 'ts-loader'
            }
          ],
          include: [
            path.resolve(dirname, 'src/js'),
            path.resolve(dirname, 'src/dev'),
          ],
          exclude: [
            path.resolve(dirname, 'node_modules')
          ]
        },
        {
          test: /\.js$/,
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-react', '@babel/preset-env']
          }
        }
      ]
    },
    plugins: [
      _DefinePlugin('development', options.BUILD_NUMBER),
      new MiniCssExtractPlugin({ filename: '[name].css' }),
      new webpack.optimize.ModuleConcatenationPlugin(),
      new webpack.SourceMapDevToolPlugin({}),
    ],
    devServer: {
      inline: false,
      port: 8100
    },
    devtool: false,
  };
  _main.module.rules = [..._main.module.rules, ..._styleModule(dirname)];
  return _main;
};

const _styleModule = (dirname) => [
  {
    test: /\.css$/,
    use: [
      MiniCssExtractPlugin.loader,
      {
        loader: 'css-loader',
        options: {
          url: false
        },
      }
    ],
    include: [
      path.resolve(dirname, 'node_modules')
    ]
  },
  {
    test: /\.scss$/,
    use: [
      MiniCssExtractPlugin.loader,
      {
        loader: 'css-loader',
        options: {
          url: false
        },
      },
      'sass-loader'
    ],
    include: [
      path.resolve(dirname, 'src/scss'),
    ]
  },
  {
    test: /\.(jpg|jpeg|gif|png)$/,
    use: 'url-loader?limit=1024&name=images/[name].[ext]'
  },
  {
    test: /\.(woff|woff2|eot|ttf|svg)$/,
    use: 'url-loader?limit=1024&name=fonts/[name].[ext]'
  },
  {
    enforce: 'pre', test: /\.js$/, loader: 'source-map-loader'
  }
];

/**
 * The DefinePlugin allows you to create global constants which can be configured at compile time.
 */
const _DefinePlugin = (NODE_ENV, BUILD_NUMBER) => new webpack.DefinePlugin({
  'process.env': {
    NODE_ENV: JSON.stringify(NODE_ENV),
    BUILD_NUMBER: JSON.stringify(BUILD_NUMBER || 'DEV'),
  },
  _APP_VERSION: JSON.stringify(BUILD_NUMBER || 'DEV')
});

module.exports = {
  KEYS,
  info: _info,
  common: _common,
  styleModule: _styleModule,
  DefinePlugin: _DefinePlugin
};
