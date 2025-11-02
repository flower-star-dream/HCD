import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import { resolve } from 'path'

export default defineConfig({
  plugins: [uni()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
      '@uview-plus': resolve(__dirname, 'uni_modules/uview-plus')
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false, // 允许使用http协议
        // 根据需要重写路径，确保与后端API匹配
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})