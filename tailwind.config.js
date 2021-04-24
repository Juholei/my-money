module.exports = {
    mode: 'jit',
    purge: [
      './src/**/*.cljs',
      './resources/templates/*.html'
    ],
    corePlugins: {
      preflight: false,
    },
    theme: {
      extend: {
        colors: {
          'red': {
            'danger': '#dc3545'
          }
        }
      }
    },
    variants: {},
    plugins: [],
  }