import Vue from 'vue'
import Router from 'vue-router'
import Hello from '@/components/Hello'
import DbModal from '@/components/DbModal'

Vue.use(Router)
Vue.use(DbModal)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Hello',
      component: Hello
    }
  ]
})
