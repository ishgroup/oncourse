const path = require("path");

const _info = (NODE_ENV, SOURCE_MAP, API_ROOT) => {
  console.log(`
Build started with following configuration:
===========================================
→ NODE_ENV: ${NODE_ENV}
→ SOURCE_MAP: ${SOURCE_MAP}
→ API_ROOT: ${API_ROOT}
`);
};

const _common = (dirname) => {
  return {
    output: {
      path: path.resolve(dirname, "build"),
      publicPath: "/assets/",
      filename: "bundle.js"
    },
    resolve: {
      modules: [
        path.resolve(dirname, 'src/js'),
        path.resolve(dirname, 'src/dev'),
        path.resolve(dirname, 'src/scss'),
        path.resolve(dirname, 'node_modules')
      ],
      extensions: [".ts", ".tsx", ".js", ".css"]
    },
    module: {
      rules: [
        {
          test: /\.scss$/,
          loaders: ['style-loader', 'css-loader', 'sass-loader'],
          include: [
            path.resolve(dirname, "src/scss"),
          ]
        },
        {
          test: /\.tsx?$/,
          loader: 'ts-loader',
          include: [
            path.resolve(dirname, "src/js"),
            path.resolve(dirname, "src/dev"),
          ],
        },
        {
          enforce: "pre", test: /\.js$/, loader: "source-map-loader"
        }
      ]
    },
    devServer: {
      inline: false
    },
    devtool: 'source-map',
  }
};



module.exports = {
    info: _info,
    common: _common
};
