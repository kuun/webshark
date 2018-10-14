module.exports = {
  /* your base configuration of choice */
  extends: [
    'eslint:recommended',
    "plugin:react/recommended"
  ],

  parser: 'babel-eslint',
  parserOptions: {
    sourceType: 'module'
  },
  env: {
    browser: true,
    node: true,
    es6: true
  },
  globals: {
    __static: true
  },
  "rules": {
    "react/prop-types": [0],
    "no-console": ["error", { allow: ["log", "warn", "error"] }]
  },
  "settings": {
    "react": {
      "createClass": "createReactClass", // Regex for Component Factory to use,
                                         // default to "createReactClass"
      "pragma": "React",  // Pragma to use, default to "React"
      "version": "16.5.2", // React version, default to the latest React stable release
      "flowVersion": "0.53" // Flow version
    }
  }
}
