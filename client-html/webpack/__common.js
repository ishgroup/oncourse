/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

const webpack = require("webpack");
const CompressionPlugin = require("compression-webpack-plugin");
const ForkTsCheckerWebpackPlugin = require("fork-ts-checker-webpack-plugin");
const WebpackPwaManifest = require('webpack-pwa-manifest');
const WorkboxPlugin = require('workbox-webpack-plugin');
const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');
const path = require("path");
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const { styles } = require( '@ckeditor/ckeditor5-dev-utils' );

const _info = (NODE_ENV, SOURCE_MAP, BUILD_NUMBER) => {
  console.log(`
Build started with following configuration:
===========================================
→ NODE_ENV: ${NODE_ENV}
→ SOURCE_MAP: ${SOURCE_MAP}
→ BUILD_NUMBER: ${BUILD_NUMBER}
`);
};

const KEYS = {
  ENTRY: "entry",
};

const _common = (dirname, options) => {
  const _main = {
    entry: [
      "webpack-dev-server/client?http://localhost:8100",
      options[KEYS.ENTRY],
    ],
    output: {
      publicPath: "/",
      path: path.resolve(dirname, "build"),
      filename: `[name].js?v=${options.BUILD_NUMBER}`,
      chunkFilename: `[name].js?v=${options.BUILD_NUMBER}`,
    },
    optimization: {
      splitChunks: {
        chunks: 'all',
        cacheGroups: {
          vendor: {
            test: /[\\/]node_modules[\\/]/,
            name: "vendors",
            enforce: true,
            reuseExistingChunk: true,
          },
        },
      },
    },
    mode: "development",
    resolve: {
      modules: [
        "node_modules",
        path.resolve(dirname, "build/generated-sources"),
        path.resolve(dirname, "src/images"),
      ],
      extensions: [".ts", ".tsx", ".js"],
      plugins: [new TsconfigPathsPlugin({ configFile: path.resolve(dirname, './tsconfig.dev.json') })],
    },
    module: {
      rules: [
        {
          test: /\.ts(x?)$/,
          use: [
            {
              loader: "ts-loader",
              options: {
                transpileOnly: true,
                happyPackMode: true,
              },
            },
          ],
          include: [
            path.resolve(dirname, "build/generated-sources"),
            path.resolve(dirname, "build/grammar"),
            path.resolve(dirname, "src/js"),
            path.resolve(dirname, "src/dev"),
          ],
          exclude: [
            path.resolve(dirname, "node_modules"),
          ],
        },
      ],
    },
    plugins: [
      _DefinePlugin("development", options.BUILD_NUMBER),
      new webpack.ProvidePlugin({
        process: 'process/browser',
      }),
      new webpack.WatchIgnorePlugin({
       paths: [
          /\.js$/,
          path.resolve(dirname, "node_modules"),
        ],
      }),
      new MiniCssExtractPlugin({ filename: '[name].css' }),
      new ForkTsCheckerWebpackPlugin({
        async: false,
        typescript: {
          configFile: path.resolve(dirname, './tsconfig.dev.json'),
        },
      }),
      new webpack.SourceMapDevToolPlugin({
        filename: "[file].map",
        test: /^[a-zA-Z-]*.js/,
        exclude: [/vendor/, /hot-update/],
      }),
    ],
    devServer: {
      inline: true,
      hot: true,
      port: 8100
    },
    devtool: false,
  };
  _main.module.rules = [..._main.module.rules, ..._styleModule(dirname)];
  return _main;
};

const _styleModule = dirname => [
    {
      test: /\.(otf|eot|ttf|woff|woff2|svg|jpg|jpeg|gif|png)$/,
      use: [{
        loader: 'file-loader',
        options: {
          name: '[name].[ext]',
        },
      }],
      exclude: [
        /ckeditor5-[^/\\]+[/\\]theme[/\\]icons[/\\][^/\\]+\.svg$/,
      ],
    },
    {
      test: /ckeditor5-[^/\\]+[/\\]theme[/\\]icons[/\\][^/\\]+\.svg$/,
      use: ['raw-loader'],
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
              themePath: require.resolve( '@ckeditor/ckeditor5-theme-lark' ),
            },
            minify: true,
          } ),
        },
      ],
    },
    {
      test: /\.s?css$/,
      use: [MiniCssExtractPlugin.loader, 'css-loader'],
      exclude: [
        /ckeditor5-[^/\\]+[/\\]theme[/\\].+\.css$/,
      ],
    },
    {
      enforce: "pre",
      test: /\.js$/,
      loader: "source-map-loader",
      exclude: [
        path.resolve(dirname, "node_modules/antlr4ts"),
        path.resolve(dirname, "node_modules/ace-builds"),
      ],
    },
  ];

/**
 * The DefinePlugin allows you to create global constants which can be configured at compile time.
 */
const _DefinePlugin = (NODE_ENV, BUILD_NUMBER) => new webpack.EnvironmentPlugin({
    NODE_ENV,
    RELEASE_VERSION: BUILD_NUMBER,
  });

const _CompressionPlugin = () => new CompressionPlugin({
    asset: "[path].gz[query]",
    algorithm: "gzip",
    test: /\.(js|html)$/,
    threshold: 10240,
    minRatio: 0.8,
  });

const _PwaManifestPlugin = () => new WebpackPwaManifest({
  filename: "manifest.json",
  name: 'onCourse',
  short_name: 'onCourse',
  scope: '/',
  icons: [
    {
      src: path.resolve(__dirname, "../src/images/ish-onCourse-icon-192.png"),
      size: "192x192",
    },
    {
      src: path.resolve(__dirname, "../src/images/ish-onCourse-icon-512.png"),
      size: "512x512",
    },
  ],
  start_url: "/",
  orientation: "landscape",
  background_color: "#fff",
  display: "standalone",
  prefer_related_applications: false,
  theme_color: "#f7941d",
  categories: ["productivity", "education", "business"],
});

const _GenerateSW = () => new WorkboxPlugin.GenerateSW({
  clientsClaim: true,
  skipWaiting: true,
});

module.exports = {
  KEYS,
  info: _info,
  common: _common,
  styleModule: _styleModule,
  DefinePlugin: _DefinePlugin,
  CompressionPlugin: _CompressionPlugin,
  PwaManifestPlugin: _PwaManifestPlugin,
  GenerateSW: _GenerateSW,
};
