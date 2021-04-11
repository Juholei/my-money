module.exports = {
    mode: 'jit',
    purge: [
      './src/**/*.cljs',
      './resources/templates/*.html'
    ],
    corePlugins: {
      preflight: false,
    },
    theme: {},
    variants: {},
    plugins: [],
  }