const path = require('path');
const webpack = require('webpack');
const fs = require('fs');

module.exports = function (config) {

    const API_ROOT = 'http://localhost:10080';
    const APP_VERSION = 'DEV';

    config.set({
        basePath: '',
        frameworks: ['jasmine'],
        files: [
            'spec.js'
        ],
        webpack: {
            // Shared with webpack.config.js
            // Can be moved to common module...
            externals: [function (context, request, callback) {
                const p = path.resolve(context, request) + '.js';

                if (/.custom.js$/.test(p)) {
                    fs.stat(p, (err) => {
                        if (err) {
                            callback(null, "{}");
                            return;
                        }

                        callback();
                    });
                    return;
                }

                callback();
            }],
            resolve: {
                extensions: ['.ts', '.tsx', '.js']
            },
            module: {
                rules: [{
                    test: /\.tsx?$/,
                    loader: 'ts-loader',
                    exclude: /node_modules/,
                }, {
                    test: /\.js$/,
                    loader: 'babel-loader',
                    options: {
                        presets: [['es2015', {loose: true}], 'react'],
                        plugins: ['transform-object-rest-spread']
                    },
                    exclude: /node_modules/
                }]
            },
            devtool: 'inline-source-map',
            plugins: [
                new webpack.DefinePlugin({
                    'process.env': {
                        'NODE_ENV': JSON.stringify("DEVELOPMENT")
                    },
                    _API_ROOT: JSON.stringify(API_ROOT),
                    _APP_VERSION: JSON.stringify(APP_VERSION)
                }),
                new webpack.ProvidePlugin({
                    'React': 'react',
                    '$': 'jquery'
                })
            ]
        },
        webpackMiddleware: {
            stats: {
                chunkModules: false,
                colors: true
            }
        },
        preprocessors: {
            'spec.js': ['webpack', 'sourcemap']
        },
        reporters: ['progress'],
        port: 9876,
        colors: true,
        logLevel: config.LOG_INFO,
        autoWatch: false,
        browsers: ['PhantomJS'],
        singleRun: true
    });
};
