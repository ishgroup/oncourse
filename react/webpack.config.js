let webpack = require('webpack'),
    path = require('path'),
    ExtractTextPlugin = require("extract-text-webpack-plugin"),
    glob = require('glob');

const NODE_ENV = process.env.NODE_ENV || 'development';
const WATCH = process.env.WATCH === '1';
const __PROD__ = NODE_ENV === 'production';
const __DEV__ = NODE_ENV === 'development';
const __TEST__ = NODE_ENV === 'test';
const CONTEXT = path.resolve(__dirname, 'src');
const DIST = path.resolve(__dirname, 'dist');
const TMP = path.resolve(__dirname, 'tmp');

let config = {
    target: __TEST__ ? 'node' : 'web',

    context: CONTEXT,

    watch: WATCH,

    module: {
        loaders: [{
            loader: 'babel-loader',
            test: /\.js$/,
            exclude: /node_modules/,
            query: {
                presets: [['es2015', { loose: true }], 'react'],
                plugins: ['transform-object-rest-spread']
            }
        }, {
            loader: __TEST__ ? 'null' : ExtractTextPlugin.extract('css-loader'),
            test: /.css$/
        }]
    },

    resolve: {
        root: path.resolve(__dirname, 'src'),
        alias: {
            js: 'js',
            config: 'js/config/' + (function() {
                if(__PROD__) {
                    return 'production';
                } else if(__DEV__) {
                    return 'development';
                } else {
                    return 'testing'
                }
            })()
        }
    }
};

if(!config.plugins) {
    config.plugins = [];
}

config.plugins.push(
    new webpack.DefinePlugin({
        'process.env':{
            'NODE_ENV': JSON.stringify(NODE_ENV)
        }
    }),
    new webpack.ProvidePlugin({
        'React': 'react',
        '$': 'jquery'
    })
);

if(__TEST__) {
    Object.assign(config, {
        entry: glob.sync('test/**/*.js', { cwd: CONTEXT }),
        output: {
            filename: 'test.js',
            path: TMP
        }
    });
} else {
    Object.assign(config, {
        entry: {
            polyfill: ['babel-polyfill'],
            dynamic: `js/app`
        },
        output: {
            filename: '[name].js',
            path: DIST
        }
    });
    config.plugins.push(
        new ExtractTextPlugin('[name]/bundle.css')
    );
}

if(__PROD__) {
    config.plugins.push(
        new webpack.optimize.UglifyJsPlugin({
            compress: {
                warnings: false
            }
        })
    );
}

module.exports = config;