import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createPersistedState } from 'pinia-persistedstate-plugin'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import './styles/global.scss'
import * as components from './components/index'

import App from './App.vue'
import router from './router'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
// 自定义组件
console.log('注册的自定义组件:', Object.keys(components));
for (const [key, component] of Object.entries(components)) {
  console.log(`注册组件: ${key}`);
  app.component(key, component)
}

const pinia = createPinia();
const persist = createPersistedState();
pinia.use(persist)
app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')