<template>
  <div class="room-page">
    <h2>房间信息</h2>
    <div class="info">
      <div>房间号：{{ roomNumber }}</div>
      <div>WiFi 密码：{{ wifiPassword }}</div>
      <div>初始室温：{{ ambient?.toFixed(1) }} ℃</div>
    </div>
    <div class="ac-control">
      <h3>空调控制</h3>
      <div style="margin-bottom: 8px;">
        空调资源状态：
        <span :style="{color: acResourceAvailable ? 'green' : 'red'}">
          {{ acResourceAvailable ? '可用' : '不可用' }}
        </span>
      </div>
      <div>
        当前模式：
        <span>{{ mode === 'Cold' ? '制冷' : '制热' }}</span>
        <button @click="toggleMode" style="margin-left:12px;">
          切换为{{ mode === 'Cold' ? '制热' : '制冷' }}
        </button>
      </div>
      <div>房间实时温度：{{ roomTemperature }} ℃</div>
      <div>
        <label>开关：</label>
        <button @click="togglePower">{{ acOn ? '关闭' : '开启' }}</button>
      </div>
        <div>
          <label>目标温度：</label>
          <input
            type="number"
            v-model="targetTempInput"
            step="0.1"
            :min="getTempRange().min"
            :max="getTempRange().max"
            :disabled="!acOn"
            @blur="handleTempInput"
            @keydown.enter="handleTempInput"
            style="width: 80px; padding: 4px;"
          />
          ℃
        </div>
      <div>
        <label>风速：</label>
        <button
          v-for="speed in ['LOW', 'MEDIUM', 'HIGH']"
          :key="speed"
          :class="{ active: fanSpeed === speed }"
          @click="setFanSpeed(speed as 'LOW' | 'MEDIUM' | 'HIGH')"
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
    <button class="back-btn" @click="handleCheckout">退房</button>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios';
import { defineProps, onMounted, onUnmounted, ref, watch } from 'vue';

const emit = defineEmits(['checkout']);


const props = defineProps<{
  roomNumber: string;
  wifiPassword: string;
}>();

const acOn = ref(false);
const targetTemperature = ref(22); // 空调目标温度（可调节）
const roomTemperature = ref();
const ambient = ref();
const mode = ref<'Cold' | 'Warm'>('Warm');
const fanSpeed = ref<'LOW' | 'MEDIUM' | 'HIGH'>('MEDIUM');
const acResourceAvailable = ref(false);
const reachstatus = ref(false);
const targetTempInput = ref(targetTemperature.value)

let eventSource: EventSource | null = null;
let pushTimer: number | undefined;

watch(targetTemperature, (val) => {
  targetTempInput.value = Number(val.toFixed(1))
})

onMounted(() => {

  // 初始化房间温度，这里是随机的，也可以指定固定数据或者向后端获取
  const randomTemp = Math.random() * (32 - 20) + 20;
  // roomTemperature.value = Number(randomTemp.toFixed(1));
  // ambient.value = Number(randomTemp.toFixed(1));
  roomTemperature.value = 14;
  ambient.value = 14;

  // 定时更新房间温度
  fetchRoomTemperature();
  timer = window.setInterval(fetchRoomTemperature, 1000); // 每0.5秒

  //SSE监听空调资源状态
  eventSource = new EventSource(`/api/room/service-status/stream/${props.roomNumber}`);
  eventSource.addEventListener('service-status', (event: MessageEvent) => {
    try {
      acResourceAvailable.value = event.data === 'true';
    } catch (e) {
      acResourceAvailable.value = false;
    }
  });
  eventSource.onerror = () => {
    acResourceAvailable.value = false;
  };

  pushTimer = window.setInterval(() => {
    axios.post(`/api/status/${props.roomNumber}`, {
      // roomId: props.roomNumber,
      mode: mode.value,
      currentRoomTemp: roomTemperature.value,
      targetTemperature: targetTemperature.value,
      fanSpeed: fanSpeed.value, // 直接传递字符串 'LOW', 'MEDIUM', or 'HIGH'
    }).catch(err => {
      console.error('推送失败：', err.message);
    });
  }, 1000); // 每秒发送一次
});
// 切换空调开关
function togglePower() {
  acOn.value = !acOn.value;
  if (acOn.value) {
  
    targetTemperature.value = 22;
      axios.post('/api/poweron', {
      roomId: props.roomNumber,
      currentRoomTemp: roomTemperature.value
    });
  }
  else {
    // 关机时通知后端
    fanSpeed.value = 'MEDIUM'; // 关机时重置风速
    axios.post('/api/poweroff', {
      roomId: props.roomNumber
    });
  }
};
async function handleCheckout() {
  // ✅ 1. 你自己的逻辑，比如确认弹窗、日志、状态清理等
  const confirmCheck = confirm('确认要退房吗？')
  if (!confirmCheck) return
  await axios.post('/api/poweroff', {
    roomId: props.roomNumber
  });
  await new Promise(resolve => setTimeout(resolve, 1000))
  emit('checkout')
}

//根据模式获取目标温度范围
function getTempRange() {
  return mode.value === 'Cold'
    ? { min: 10, max: 40 }
    : { min: 10, max: 40 };
}

// 调整目标温度
function handleTempInput() {
  if (!acOn.value) return

  const input = Number(Number(targetTempInput.value).toFixed(1))
  const { min, max } = getTempRange()

  if (isNaN(input) || input < min || input > max) {
    alert(`请输入 ${min} 到 ${max} 之间的温度`)
    targetTempInput.value = targetTemperature.value // 回滚
    return
  }

  if (input !== targetTemperature.value) {
    targetTemperature.value = input

    axios.post('/api/changetemp', {
      roomId: props.roomNumber,
      targetTemp: input
    })
  }
}



// 调整风速
function setFanSpeed(speed: 'LOW' | 'MEDIUM' | 'HIGH') {
  fanSpeed.value = speed;
  axios.post('/api/changespeed', {
    roomId: props.roomNumber,
    fanSpeed: speed  // 直接传递字符串 'LOW', 'MEDIUM', or 'HIGH'
  });
}

function toggleMode() {
  mode.value = mode.value === 'Cold' ? 'Warm' : 'Cold';
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
let tick2 = 0;
async function fetchRoomTemperature() {
  try {
    if (acOn.value && acResourceAvailable.value) {
      tick++;
      let shouldChange = false;
      if (fanSpeed.value === 'HIGH') {
        shouldChange = true; // 每1秒变化
      } else if (fanSpeed.value === 'MEDIUM' && tick % 2 === 0) {
        shouldChange = true; // 每2秒变化
      } else if (fanSpeed.value === 'LOW' && tick % 3 === 0) {
        shouldChange = true; // 每3秒变化
      }
      if (shouldChange) {
        const diff = targetTemperature.value - roomTemperature.value;
        let step = 0.1;
        if (Math.abs(diff) < step && !reachstatus.value) {
          // 如果温差小于步长且之前已经到达目标温度，则认为已达到目标温度
          roomTemperature.value = Number(targetTemperature.value.toFixed(1));
          axios.post('/api/temp-reached', {
            roomId: props.roomNumber
          });
          reachstatus.value = true;
        } else {
          roomTemperature.value = Number((roomTemperature.value + (diff > 0 ? step : -step)).toFixed(1));
        }
      }
      if (tick >= 6) tick = 0; // 防止tick无限增大
    }
      else if (!acResourceAvailable.value) {
        tick2++;
        let shouldChange1 = false;
        const maxTemp = ambient.value; 
         // 设置温度上限，最大允许温度（例如30度）
         if(tick2 % 2 ===0 ){shouldChange1 = true;}

        if(shouldChange1) {
          // 空调资源不可用时，逐步回温到室温（ambient）
          const diff0 = roomTemperature.value - targetTemperature.value;
          const diff1 = ambient.value - roomTemperature.value;
          let step = 0.1;
          if (Math.abs(diff1) < step) {
            roomTemperature.value = Number(ambient.value.toFixed(1));
          } else {
            roomTemperature.value = Number((roomTemperature.value + (diff1 > 0 ? step : -step)).toFixed(1));
          }
          // 检查温度差是否超过 1 度，且已经到达目标温度
          if (Math.abs(diff0) >= 1 && reachstatus.value) {
            console.log('当前温度与目标温度的差值diff0:', Math.abs(diff0));
            axios.post('/api/temp-rising', {
              roomId: props.roomNumber,
              targetTemp: targetTemperature.value,
              currentRoomTemp: roomTemperature.value,
              fanSpeed: fanSpeed.value  // 直接传递字符串，如 "LOW", "MEDIUM", "HIGH"
            });
            reachstatus.value = false;
          }
          }
          if (tick2 >= 6) tick2 = 0; // 重置tick2
        }
      }

    // 若空调关，则温度不变（或可模拟回归环境温度）
   catch (e: any) {
    // 可忽略或提示
    console.error(e);
  }
}


onUnmounted(() => {
  if (pushTimer) clearInterval(pushTimer);
  if (timer) clearInterval(timer);
  if (eventSource) {
    eventSource.close();
    eventSource = null;
  }
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
