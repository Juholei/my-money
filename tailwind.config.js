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
          },
          'button': {
            'info': '#17a2b8',
            'primary': '#007bff',
            'primary-hover': '#0069d9',
            'primary-active': '#0062cc',
            'danger': '#dc3545',
            'danger-hover': '#c82333',
            'danger-secondary': '#bd2130'
          }
        }
      }
    },
    variants: {
      extend:
      {
        borderRadius: ['first', 'last']
      }
    },
    plugins: [],
  }