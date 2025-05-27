import '@mdi/font/css/materialdesignicons.css'
import { createApp } from 'vue'
import { createVuetify } from 'vuetify'
import 'vuetify/styles'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import App from './App.vue'
import './style.css'

// 创建 Vuetify 实例，导入组件、指令，并配置图标
const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi', // 默认图标集为 Material Design Icons
  },
})

createApp(App)
  .use(vuetify)
  .mount('#app')
