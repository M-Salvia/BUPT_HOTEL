<template>
  <div class="room-page">
    <h2>房间信息</h2>
    <div class="info">
      <div>房间号：{{ roomNumber }}</div>
      <div>WiFi 密码：{{ wifiPassword }}</div>
    </div>
    <div class="ac-control">
      <h3>空调控制</h3>
      <div>当前模式：{{ mode === 'Cold' ? '制冷' : '制热' }}</div>
      <div>房间实时温度：{{ roomTemperature }} ℃</div>
      <div>
        <label>开关：</label>
        <button @click="togglePower">{{ acOn ? '关闭' : '开启' }}</button>
      </div>
      <div>
        <label>目标温度：</label>
        <button @click="changeTemp(-1)" :disabled="!acOn">-</button>
        <span>{{ targetTemperature }} ℃</span>
        <button @click="changeTemp(1)" :disabled="!acOn">+</button>
      </div>
      <div>
        <label>风速：</label>
        <button
          v-for="speed in ['低', '中', '高']"
          :key="speed"
          :class="{ active: fanSpeed === speed }"
          @click="setFanSpeed(speed as '低' | '中' | '高')"
          :disabled="!acOn"
        >
          {{ speed }}
        </button>
        <span>当前：{{ fanSpeed }}</span>
      </div>
    </div>
    <div class="extras">
      <h3>客房用品</h3>
      <div class="kettle-water">
        <div class="kettle">
          <!-- 简单SVG热水壶 -->
          <svg width="48" height="48" viewBox="0 0 48 48">
            <ellipse cx="24" cy="40" rx="12" ry="4" fill="#bbb"/>
            <rect x="12" y="16" width="24" height="24" rx="8" fill="#eee" stroke="#888" stroke-width="2"/>
            <rect x="20" y="10" width="8" height="10" rx="3" fill="#bbb" stroke="#888" stroke-width="2"/>
            <path d="M36 20 Q44 24 36 28" stroke="#888" stroke-width="2" fill="none"/>
          </svg>
          <div>热水壶</div>
        </div>
        <div class="water">
          <!-- 简单SVG矿泉水瓶 -->
          <svg width="32" height="48" viewBox="0 0 32 48">
            <rect x="12" y="2" width="8" height="8" rx="2" fill="#b3e5fc" stroke="#0288d1" stroke-width="1"/>
            <rect x="8" y="10" width="16" height="32" rx="6" fill="#e1f5fe" stroke="#0288d1" stroke-width="2"/>
            <rect x="12" y="42" width="8" height="4" rx="2" fill="#b3e5fc" stroke="#0288d1" stroke-width="1"/>
          </svg>
          <div>矿泉水</div>
        </div>
      </div>
    </div>
    <button class="back-btn" @click="$emit('checkout')">退房</button>
  </div>
</template>

<script setup lang="ts">
import { defineProps, onMounted, onUnmounted, ref, watch } from 'vue';

const props = defineProps<{
  roomNumber: string;
  wifiPassword: string;
}>();

const acOn = ref(false);
const targetTemperature = ref(25); // 空调目标温度（可调节）
const roomTemperature = ref();
const mode = ref<'Cold' | 'Warm'>('Cold');
const fanSpeed = ref<'低' | '中' | '高'>('中');

function getTempRange() {
  return mode.value === 'Cold'
    ? { min: 16, max: 24 }
    : { min: 22, max: 28 };
}

function togglePower() {
  acOn.value = !acOn.value;
  if (acOn.value) {
    // 开机时目标温度重置为默认25度
    targetTemperature.value = 25;
  }
}
function changeTemp(delta: number) {
  if (!acOn.value) return;
  const { min, max } = getTempRange();
  targetTemperature.value = Math.min(max, Math.max(min, targetTemperature.value + delta));
}

function setFanSpeed(speed: '低' | '中' | '高') {
  fanSpeed.value = speed;
}

// 监听模式变化，自动调整目标温度到区间内
watch(mode, () => {
  const { min, max } = getTempRange();
  if (targetTemperature.value < min || targetTemperature.value > max) {
    targetTemperature.value = 25;
  }
});

// 定时获取房间实时温度
let timer: number | undefined;
let tick = 0;

async function fetchRoomTemperature() {
  try {
    if (acOn.value) {
      tick++;
      let shouldChange = false;
      if (fanSpeed.value === '高') {
        shouldChange = true; // 每5秒变化
      } else if (fanSpeed.value === '中' && tick % 2 === 0) {
        shouldChange = true; // 每10秒变化
      } else if (fanSpeed.value === '低' && tick % 3 === 0) {
        shouldChange = true; // 每15秒变化
      }
      if (shouldChange) {
        const diff = targetTemperature.value - roomTemperature.value;
        let step = 0.1;
        if (Math.abs(diff) < step) {
          roomTemperature.value = Number(targetTemperature.value.toFixed(1));
        } else {
          roomTemperature.value = Number((roomTemperature.value + (diff > 0 ? step : -step)).toFixed(1));
        }
      }
      if (tick >= 6) tick = 0; // 防止tick无限增大
    }
    // 若空调关，则温度不变（或可模拟回归环境温度）
  } catch (e: any) {
    // 可忽略或提示
  }
}

async function fetchMode() {
  try {
    // const res = await axios.post('/api/room/mode', {
    //   roomNumber: props.roomNumber
    // });
    // mode.value = res.data.mode;
    mode.value = 'Cold'; // 测试数据
  } catch (e: any) {
    // 可忽略或提示
  }
}

onMounted(() => {
  const randomTemp = Math.random() * (32 - 20) + 20;
  roomTemperature.value = Number(randomTemp.toFixed(1));
  fetchMode();
  fetchRoomTemperature();
  timer = window.setInterval(fetchRoomTemperature, 5000); // 每5秒
});

//移除定时器
onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>

<style scoped>
.room-page {
  max-width: 400px;
  margin: 40px auto;
  padding: 24px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #f6f8fa;
}
.info {
  margin-bottom: 24px;
}
.ac-control {
  margin-bottom: 24px;
}
label {
  display: inline-block;
  width: 60px;
}
button {
  margin: 0 4px;
  padding: 4px 12px;
  border: none;
  border-radius: 4px;
  background: #42b983;
  color: #fff;
  cursor: pointer;
}
button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
button.active {
  background: #1976d2;
}
.back-btn {
  background: #d32f2f;
}
.kettle-water {
  display: flex;
  gap: 32px;
  justify-content: center;
  margin-top: 12px;
}
.kettle, .water {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 15px;
  color: #555;
}
.kettle svg, .water svg {
  margin-bottom: 4px;
}
</style>
