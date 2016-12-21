let webpack = require('webpack'),
    path = require('path'),
    ExtractTextPlugin = require("extract-text-webpack-plugin"),
    HtmlWebpackPlugin = require('html-webpack-plugin');

const NODE_ENV = process.env.NODE_ENV || 'development';
const MODULE_PATH = './app/views';
const __PROD__ = NODE_ENV === 'production';
const __DEV__ = NODE_ENV === 'development';

let plugins = [
        new webpack.DefinePlugin({
            'process.env':{
                'NODE_ENV': JSON.stringify(NODE_ENV)
            }
        }),
        new webpack.ProvidePlugin({
            'React': 'react'
        }),
        new ExtractTextPlugin('[name]/bundle.css')
    ];

if(__PROD__) {
    plugins.push(new webpack.optimize.UglifyJsPlugin({
        compress: {
            warnings: false
        }
    }));
}

plugins.push(new HtmlWebpackPlugin({
    filename: 'index.html',
    inject: 'body',
    template: 'templates/index.ejs'
}));

module.exports = {
    context: path.resolve(__dirname, 'src'),
    entry: {
        ie: ['babel-polyfill'],
        enrol: `${MODULE_PATH}/enrol/Enrol`
    },
    output: {
        filename: '[name]/bundle.js',
        path: path.resolve(__dirname, 'dist')
    },

    watch: NODE_ENV === 'development',

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
            loader: ExtractTextPlugin.extract('css-loader'),
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
    },

    plugins
};
