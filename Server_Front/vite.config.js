import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3001,
    open: true,
    proxy: {
      '/api': {
        target: 'http://10.29.90.25:8080', // 后端地址
        changeOrigin: true,
      },
    }
  }
})
