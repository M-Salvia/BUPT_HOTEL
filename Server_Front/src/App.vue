<template>
  <v-app>
    <v-app-bar app color="#3722F6" dark style="color: white;">
      <v-icon left class="ml-3" color="white">mdi-bank</v-icon>
      <v-toolbar-title style="color: white;">巴普特酒店中控</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn icon @click="showSetPage = !showSetPage">
        <v-icon color="white">mdi-cog</v-icon>
      </v-btn>
    </v-app-bar>
    <v-main>
      <RoomStatus v-if="!showSetPage" />
      <SetPage v-else />
    </v-main>
  </v-app>
</template>

<script setup>
import { ref } from 'vue'
import RoomStatus from './components/RoomStatus.vue'
import SetPage from './components/SetPage.vue'
const showSetPage = ref(false)
</script>

<style scoped>
.v-app-bar .v-toolbar-title,
.v-app-bar .v-icon,
.v-app-bar .v-btn {
  color: #fff !important;
}
</style>
