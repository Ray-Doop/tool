/*
 * @generated-file-note
 * 文件：smart-tools-frontend/babel.config.js
 * 用途：前端 JavaScript 源码文件
 * 归属：前端 frontend
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
module.exports = {
  presets: ['@vue/cli-plugin-babel/preset'],
  plugins: [
    '@babel/plugin-transform-private-methods',
    '@babel/plugin-transform-class-static-block'
  ]
}
