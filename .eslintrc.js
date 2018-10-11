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
    node: true
  },
  globals: {
    __static: true
  },
  "rules": {
    "react/prop-types": [0],
    "no-console": ["error", { allow: ["warn", "error"] }]
  }
}
