<template>
  <div id="app">
    <CheckIn v-if="page==='checkin'" @check-in-success="handleCheckInSuccess" />
    <Room v-else-if="page==='room'" :roomNumber="roomInfo.roomNumber" :wifiPassword="roomInfo.wifiPassword" @checkout="goToCheckout" />
    <Checkout v-else-if="page==='checkout'"
      :roomNumber="roomInfo.roomNumber"
      :days="roomInfo.days"
      :deposit="roomInfo.deposit"
      @backToCheckIn="reset"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import CheckIn from './components/CheckIn.vue';
import Checkout from './components/Checkout.vue';
import Room from './components/Room.vue';

const page = ref<'checkin' | 'room' | 'checkout'>('checkin');
//用响应式变量page决定当前显示哪个界面
const roomInfo = ref({
  roomNumber: '',
  wifiPassword: '',
  days: 1,
  deposit: 0
});
//这是当前房间的关键信息

function handleCheckInSuccess(info: any) {
  roomInfo.value = {
    roomNumber: info.roomNumber,
    wifiPassword: info.wifiPassword,
    days: info.days,
    deposit: info.deposit
  };
  page.value = 'room';
}
//监听到入住成功后,切换房间到room
function goToCheckout() {
  page.value = 'checkout';
}
//监听到退房请求后,切换到前台进行退房
function reset() {
  page.value = 'checkin';
  roomInfo.value = { roomNumber: '', wifiPassword: '', days: 1, deposit: 0 };
}
//退房完成后重置房间信息
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>