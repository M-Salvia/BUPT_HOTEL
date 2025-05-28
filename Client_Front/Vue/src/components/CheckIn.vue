<template>
  <div class="check-in">
    <h2>巴普特酒店入住登记</h2>
    <form @submit.prevent="handleCheckIn">
      <div>
        <label>身份证号：</label>
        <input v-model="idCard" maxlength="18" required />
      </div>
      <div>
        <label>入住天数：</label>
        <input v-model.number="day" type="number" min="1" required />
      </div>
      <div>
        <label>押金(一晚200): </label>
        <span>{{ deposit }} 元</span>
      </div>
      <button type="submit">办理入住</button>
    </form>
    <div v-if="error" class="error">{{ error }}</div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import axios from 'axios';

const idCard = ref('');
const type = ref(1);
const day = ref(1);
const deposit = computed(() => day.value * 200);
const error = ref('');

const emit = defineEmits(['check-in-success']);

// 假设有一个后端API接口 /api/checkin,返回 { roomNumber}
async function handleCheckIn() {
  error.value = '';
  if (!/^\d{17}[\dXx]$/.test(idCard.value)) {
    error.value = '请输入有效的18位身份证号';
    return;
  }
  if (day.value < 1) {
    error.value = '入住天数需大于0';
    return;
  }
  //测试数据
    const data = {
    roomNumber: '402',
  };
    emit('check-in-success', {
        idCard: idCard.value,
        days: day.value,
        type: type.value,
        deposit: deposit.value,
        roomNumber: data.roomNumber,
        wifiPassword: data.roomNumber + '88888'
    });
try {
    // 使用axios发送请求到后端
    const res = await axios.post('/api/checkin', {
        idCard: idCard.value,
        day: day.value,
        type: type.value,
    });
    const data = res.data;
    emit('check-in-success', {
        idCard: idCard.value,
        day: day.value,
        deposit: deposit.value,
        roomNumber: data.roomNumber,
        wifiPassword: data.roomNumber + '88888'
    });
} catch (e: any) {
    error.value = e.response?.data?.message || e.message || '办理失败，请重试';
}
}
</script>
<style scoped>
.check-in {
  max-width: 400px;
  margin: 40px auto;
  padding: 24px;
  border: 1px solid #eee;
  border-radius: 8px;
  background: #fafbfc;
}
form > div {
  margin-bottom: 16px;
}
label {
  display: inline-block;
  width: 90px;
}
input {
  padding: 4px 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
button {
  padding: 6px 18px;
  background: #42b983;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.error {
  color: #d32f2f;
  margin-top: 10px;
}
</style>
