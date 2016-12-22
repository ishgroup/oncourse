let webpack = require('webpack'),
    path = require('path'),
    ExtractTextPlugin = require("extract-text-webpack-plugin"),
    HtmlWebpackPlugin = require('html-webpack-plugin'),
    glob = require('glob');

const NODE_ENV = process.env.NODE_ENV || 'development';
const __PROD__ = NODE_ENV === 'production';
const __DEV__ = NODE_ENV === 'development';
const __TEST__ = NODE_ENV === 'test';
const CONTEXT = path.resolve(__dirname, 'src');
const DIST = path.resolve(__dirname, 'dist');

let config = {
    context: CONTEXT,

    watch: __DEV__,

    module: {
        loaders: [{
            loader: 'babel-loader',
            test: /\.js$/,
            exclude: /node_modules/,
            query: {
                presets: ['es2015', 'react'],
                plugins: ['transform-class-properties', 'transform-object-rest-spread']
            }
        }, {
            loader: __TEST__ ? 'null' : ExtractTextPlugin.extract('css-loader'),
            test: /.css$/
        }]
    },

    resolve: {
        root: path.resolve(__dirname, 'src'),
        alias: {
            app: 'app',
            css: 'styles',
            config: 'app/config/' + (__PROD__ ? 'production' : 'development')
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
        'React': 'react'
    })
);

if(__TEST__) {
    Object.assign(config, {
        entry: glob.sync('app/**/*-spec.js', { cwd: CONTEXT }),
        output: {
            filename: 'test/bundle.js',
            path: DIST
        },

        externals: {
            'cheerio': 'window',
            'react/addons': true,
            'react/lib/ExecutionEnvironment': true,
            'react/lib/ReactContext': true
        }
    });
} else {
    Object.assign(config, {
        entry: {
            ie: ['babel-polyfill'],
            enrol: `app/cmp`
        },
        output: {
            filename: '[name]/bundle.js',
            path: DIST
        }
    });
    config.plugins.push(
        new ExtractTextPlugin('[name]/bundle.css'),
        new HtmlWebpackPlugin({
            filename: 'index.html',
            inject: 'body',
            template: 'templates/index.ejs'
        })
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
