import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    open: true,
    proxy: {
      '/api': {
        target: 'http://10.29.90.25:8080', // 后端地址
        changeOrigin: true,
      },
      '/front': {
        target: 'http://10.29.90.26:3001',
        changeOrigin: true,
      },
    }
  },
  build: {
    outDir: 'dist'
  }
})
