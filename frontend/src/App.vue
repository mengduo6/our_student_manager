<template>
  <div id="app-root">
    <NavBar v-if="showNav" />
    <router-view />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from './components/NavBar.vue'

const route = useRoute()

const showNav = computed(() => {
  return !['Login', 'Register'].includes(route.name)
})
</script>

<style>
#app-root {
  min-height: 100vh;
}
</style>
