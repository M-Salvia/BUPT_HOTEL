<template>
  <v-container fluid>
    <v-row>
      <v-col cols="2">
        <v-list nav dense>
          <v-list-item
            v-for="room in roomNumbers"
            :key="room"
            :active="room === selectedRoom"
            @click="selectedRoom = room"
            class="room-btn"
          >
            <v-list-item-title>房间 {{ room }}</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-col>
      <v-col cols="10">
        <v-card elevation="2" class="pa-6">
          <v-card-title class="text-h5 mb-4">
            <v-icon color="primary" class="mr-2">mdi-door</v-icon>
            房间 {{ selectedRoom }} 空调运行信息
          </v-card-title>
          <v-divider class="mb-4" />
          <v-row>
            <v-col cols="12" md="6">
              <v-list dense>
                <v-list-item>
                  <v-list-item-title>是否开机运行</v-list-item-title>
                  <v-list-item-subtitle>
                    <v-icon :color="roomData.isOn ? 'success' : 'grey'">{{ roomData.isOn ? 'mdi-power' : 'mdi-power-off' }}</v-icon>
                    {{ roomData.isOn ? '是' : '否' }}
                  </v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>当前工作模式</v-list-item-title>
                  <v-list-item-subtitle>{{ roomData.mode }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>当前室温</v-list-item-title>
                  <v-list-item-subtitle>{{ roomData.currentTemp }} ℃</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>目标温度</v-list-item-title>
                  <v-list-item-subtitle>{{ roomData.targetTemp }} ℃</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>风速</v-list-item-title>
                  <v-list-item-subtitle>{{ roomData.windSpeed }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>是否等待</v-list-item-title>
                  <v-list-item-subtitle>
                    {{ roomData.isWaiting ? '是' : '否' }}
                    <span v-if="roomData.isWaiting">，等待时长：{{ roomData.waitingTime }} 分钟</span>
                  </v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-col>
            <v-col cols="12" md="6">
              <v-list dense>
                <v-list-item>
                  <v-list-item-title>本次服务费用</v-list-item-title>
                  <v-list-item-subtitle>￥{{ roomData.currentFee }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>累积费用</v-list-item-title>
                  <v-list-item-subtitle>￥{{ roomData.totalFee }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>总费用（含房费和空调费）</v-list-item-title>
                  <v-list-item-subtitle>￥{{ roomData.totalCost }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>开关次数</v-list-item-title>
                  <v-list-item-subtitle>{{ roomData.powerCount }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>调度次数</v-list-item-title>
                  <v-list-item-subtitle>{{ roomData.scheduleCount }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>详单条数</v-list-item-title>
                  <v-list-item-subtitle>{{ roomData.detailCount }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>调温次数</v-list-item-title>
                  <v-list-item-subtitle>{{ roomData.tempChangeCount }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>调风次数</v-list-item-title>
                  <v-list-item-subtitle>{{ roomData.windChangeCount }}</v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-col>
          </v-row>
          <v-divider class="my-4" />
          <v-row>
            <v-col cols="12" md="6">
              <v-card class="pa-2" color="#e3f2fd">
                <v-card-title class="text-subtitle-1">温度变化</v-card-title>
                <v-card-text>
                  <v-progress-linear :model-value="(roomData.currentTemp-16)*100/16" color="blue" height="18">
                    <template #default>
                      <strong>{{ roomData.currentTemp }} ℃</strong>
                    </template>
                  </v-progress-linear>
                </v-card-text>
              </v-card>
            </v-col>
            <v-col cols="12" md="6">
              <v-card class="pa-2" color="#fff3e0">
                <v-card-title class="text-subtitle-1">费用占比</v-card-title>
                <v-card-text>
                  <v-progress-circular :model-value="(roomData.totalFee/roomData.totalCost)*100" color="orange" size="60" width="8">
                    <template #default>
                      <span>{{ ((roomData.totalFee/roomData.totalCost)*100).toFixed(0) }}%</span>
                    </template>
                  </v-progress-circular>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import axios from 'axios'
import { computed, onMounted, ref, watch } from 'vue'

const tab = ref(null)

const roomNumbers = [
  ...Array.from({ length: 10 }, (_, i) => 101 + i),
  ...Array.from({ length: 10 }, (_, i) => 201 + i),
  ...Array.from({ length: 10 }, (_, i) => 301 + i),
  ...Array.from({ length: 10 }, (_, i) => 401 + i),
]
const selectedRoom = ref(101)

const roomDataRaw = ref(null)

async function fetchRoomData(roomId) {
  try {
    const res = await axios.get(`/api/room/${roomId}`)
    // 假设后端返回的数据结构为 { is_on, mode, ... }
    const raw = res.data
    // 处理字段名、类型等
    roomDataRaw.value = {
      isOn: raw.is_on,
      mode: raw.mode,
      currentTemp: raw.current_temp,
      targetTemp: raw.target_temp,
      windSpeed: raw.wind_speed,
      currentFee: raw.current_fee,
      totalFee: raw.total_fee,
      isWaiting: raw.is_waiting,
      waitingTime: raw.waiting_time,
      powerCount: raw.power_count,
      scheduleCount: raw.schedule_count,
      detailCount: raw.detail_count,
      tempChangeCount: raw.temp_change_count,
      windChangeCount: raw.wind_change_count,
      totalCost: raw.total_cost,
    }
  } catch (e) {
    roomDataRaw.value = null
  }
}

onMounted(() => {
  fetchRoomData(selectedRoom.value)
})

watch(selectedRoom, (val) => {
  fetchRoomData(val)
})

const roomData = computed(() => {
  // const d = roomDataRaw.value
  // if (d) {
  //   return {
  //     isOn: d.isOn ?? true,
  //     mode: d.mode ?? '制冷',
  //     // ... 其他字段
  //   }
  // }
    return {
    isOn: true,
    mode: '制冷',
    currentTemp: 25 + (selectedRoom.value % 5),
    targetTemp: 22,
    windSpeed: '中',
    currentFee: 3.5 + (selectedRoom.value % 2),
    totalFee: 120.8 + (selectedRoom.value % 10),
    isWaiting: selectedRoom.value % 3 === 0,
    waitingTime: selectedRoom.value % 3 === 0 ? 5 : 0,
    powerCount: 2 + (selectedRoom.value % 4),
    scheduleCount: 5 + (selectedRoom.value % 3),
    detailCount: 8 + (selectedRoom.value % 6),
    tempChangeCount: 3 + (selectedRoom.value % 2),
    windChangeCount: 2 + (selectedRoom.value % 2),
    totalCost: 200.8 + (selectedRoom.value % 20),
    }
})
</script>

<style scoped>
.room-btn {
  cursor: pointer;
}
</style>
