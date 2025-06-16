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
                    <v-icon :color="roomRaw.isOn ? 'success' : 'grey'">{{ roomRaw.isOn ? 'mdi-power' : 'mdi-power-off' }}</v-icon>
                    {{ roomRaw.isOn ? '是' : '否' }}
                  </v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>当前工作模式</v-list-item-title>
                  <v-list-item-subtitle>{{ roomRaw.mode }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>当前室温</v-list-item-title>
                  <v-list-item-subtitle>{{ roomRaw.currentTemp }} ℃</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>目标温度</v-list-item-title>
                  <v-list-item-subtitle>{{ roomRaw.targetTemp }} ℃</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>风速</v-list-item-title>
                  <v-list-item-subtitle>{{ roomRaw.windSpeed }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>是否等待</v-list-item-title>
                  <v-list-item-subtitle>
                    {{ roomRaw.isWaiting ? '是' : '否' }}
                    <span v-if="roomRaw.isWaiting">，等待时长：{{ roomRaw.waitingTime }} 分钟</span>
                  </v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-col>
            <v-col cols="12" md="6">
              <v-list dense>
                <v-list-item>
                  <v-list-item-title>本次服务费用</v-list-item-title>
                  <v-list-item-subtitle>￥{{ roomRaw.currentFee }}</v-list-item-subtitle>
                </v-list-item>

                <v-list-item>
                  <v-list-item-title>总费用（含房费和空调费）</v-list-item-title>
                  <v-list-item-subtitle>￥{{ roomRaw.totalCost }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>开关次数</v-list-item-title>
                  <v-list-item-subtitle>{{ roomRaw.powerCount }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>调度次数</v-list-item-title>
                  <v-list-item-subtitle>{{ roomRaw.scheduleCount }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>详单条数</v-list-item-title>
                  <v-list-item-subtitle>{{ roomRaw.detailCount }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>调温次数</v-list-item-title>
                  <v-list-item-subtitle>{{ roomRaw.tempChangeCount }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>调风次数</v-list-item-title>
                  <v-list-item-subtitle>{{ roomRaw.windChangeCount }}</v-list-item-subtitle>
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
                  <v-progress-linear :model-value="(roomRaw.currentTemp-16)*100/16" color="blue" height="18">
                    <template #default>
                      <strong>{{ roomRaw.currentTemp }} ℃</strong>
                    </template>
                  </v-progress-linear>
                </v-card-text>
              </v-card>
            </v-col>
            <v-col cols="12" md="6">
              <v-card class="pa-2" color="#fff3e0">
                <v-card-title class="text-subtitle-1">费用占比</v-card-title>
                <v-card-text>
                  <v-progress-circular :model-value="(roomRaw.totalFee/roomRaw.totalCost)*100" color="orange" size="60" width="8">
                    <template #default>
                      <span>{{ ((roomRaw.totalFee/roomRaw.totalCost)*100).toFixed(0) }}%</span>
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
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
const defaultRoomData = {
  isOn: false,
  targetTemp: 24,
  windSpeed: '中风',
  currentFee: 0.0,
  isWaiting: false,
  serviceTime: 0,
  waitingTime: 0,
  detailCount: 0,
  tempChangeCount: 0,
  windChangeCount: 0,
  powerCount: 0,
  mode: 'Warm',
  currentTemp: 26,
  totalFee: 0.0,
  totalCost: 1.0,
  TempFee: 0.0,
}
let eventSource = null
const roomRaw = ref({ ...defaultRoomData })

const tab = ref(null)
const TempFee = ref(0.0)

const roomNumbers = Array.from({ length:40}, (_, i) => {
  const num = i + 1; // 从 R003 开始
  return `R${num.toString().padStart(3, '0')}`;
});

const selectedRoom = ref('R001')

const totalFee = ref(0.0)
function connectSSE(roomId) {
  // 如果之前连接存在，先关闭
  if (eventSource) {
    eventSource.close()
  }

  // 创建新的 SSE 连接
  eventSource = new EventSource(`/api/room-status/stream/${roomId}`)
  // 接收名为 "room-status" 的事件
  eventSource.addEventListener("room-status", (event) => {
    const data = JSON.parse(event.data)
    // totalFee.value = totalFee.value + data.currentFee.value - TempFee.value;
    roomRaw.value = {
      isOn: data.isOn,
      targetTemp: data.targetTemp,
      windSpeed: data.windSpeed,
      currentFee: data.currentFee,
      isWaiting: data.isWaiting,
      serviceTime: data.serviceTime,
      waitingTime: data.waitingTime,
      detailCount: data.detailCount,
      tempChangeCount: data.tempChangeCount,
      windChangeCount: data.windChangeCount,
      powerCount: data.dayCount ,
      currentTemp: data.currentTemp,
      mode: data.mode,
      // TempFee: data.currentFee,
    }
  })

  eventSource.onerror = (e) => {
    console.error("SSE 连接错误:", e)
    eventSource.close()
    roomRaw.value = { ...defaultRoomData }
}
}

onMounted(() => {
  connectSSE(selectedRoom.value)
});

watch(selectedRoom, (newVal) => {
  connectSSE(newVal)
});

onUnmounted(() => {
  if (eventSource) {
    eventSource.close()
  }
});
</script>

<style scoped>
.room-btn {
  cursor: pointer;
}
</style>
