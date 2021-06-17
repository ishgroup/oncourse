const {
  resolve,
} = require('path');

module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'jsdom',
  globals: {
    'ts-jest': {
      tsconfig: "tsconfig.test.json",
    },
  },
  transform: {
    ".+\\.(css|styl|less|sass|scss|png|jpg|gif|svg|ttf|woff|woff2)$": "jest-transform-stub",
  },
  reporters: ["default"],
  collectCoverage: true,
  coverageDirectory: "build/reports/test",
  moduleDirectories: ["node_modules", "src"],
  moduleNameMapper: {
    '@api/model': resolve(__dirname, './build/generated-sources/swagger-js/api.ts'),
    '@aql/AqlLexer': resolve(__dirname, './build/generated-sources/aql-parser/AqlLexer.ts'),
    '@aql/AqlParser': resolve(__dirname, './build/generated-sources/aql-parser/AqlParser.ts'),
    '@aql/queryLanguageModel': resolve(__dirname, './build/generated-sources/aql-model/queryLanguageModel.ts'),
    "ace-builds": "<rootDir>/node_modules/ace-builds",
    "\\.css$|@ckeditor": "<rootDir>/src/dev/mock/importMock.ts",
  },
};
