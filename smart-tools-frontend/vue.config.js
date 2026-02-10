/*
 * @generated-file-note
 * 文件：smart-tools-frontend/vue.config.js
 * 用途：前端 JavaScript 源码文件
 * 归属：前端 frontend
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
// Vue CLI 配置：本地开发代理 /api 到后端，避免跨域问题
const { defineConfig } = require('@vue/cli-service')
const webpack = require('webpack')

module.exports = defineConfig({
  transpileDependencies: ['pdfjs-dist', 'mermaid'],
  chainWebpack: config => {
    config.module.rule('js')
      .include.add(/node_modules[\\/]mermaid/)
      .add(/node_modules[\\/]pdfjs-dist/)
  },
  configureWebpack: {
    resolve: {
      fallback: {
        stream: require.resolve('stream-browserify'),
        buffer: require.resolve('buffer/'),
        process: require.resolve('process/browser')
      }
    },
    plugins: [
      new webpack.ProvidePlugin({
        Buffer: ['buffer', 'Buffer'],
        process: 'process/browser'
      })
    ]
  },
  devServer: {
    port: 5173,
    proxy: {
      '/api': {
        target: process.env.VUE_APP_BACKEND_URL || 'http://localhost:9090',
        changeOrigin: true,
        ws: false
      },
      '/uploads': {
        target: process.env.VUE_APP_BACKEND_URL || 'http://localhost:9090',
        changeOrigin: true
      }
    }
  }
})
