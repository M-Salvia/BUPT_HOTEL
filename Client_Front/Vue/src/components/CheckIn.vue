<template>
  <div class="check-in">
    <h2>巴普特酒店入住登记</h2>
    <form @submit.prevent="handleCheckIn">
      <div>
        <label>身份证号：</label>
        <input v-model="idCard" maxlength="18" required />
      </div>
      <div>
        <label>房型：</label>
        <input v-model.number="type" type="number" min="1" required />
      </div>
      <button type="submit">办理入住</button>
    </form>
    <div v-if="error" class="error">{{ error }}</div>

    <div class="room-list">
      <h3>房型价格一览</h3>
      <ul>
        <li v-for="room in roomTypes" :key="room.id">
          房型 {{ room.id }}: {{ room.name }} - ￥{{ room.price }} / 晚
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios';
import qs from 'qs';
import { ref } from 'vue';
const idCard = ref('');
const type = ref(1);
const error = ref('');

const emit = defineEmits(['check-in-success']);
const roomTypes = [
  { id: 1, name: '房型一', price: 100 },
  { id: 2, name: '房型二', price: 125 },
  { id: 3, name: '房型三', price: 150 },
  { id: 4, name: '房型四', price: 200 },
  { id: 5, name: '房型五', price: 100 },
];

// 假设有一个后端API接口 /api/checkin,返回 { roomNumber}
async function handleCheckIn() {
  error.value = '';
  // if (!/^\d{17}[\dXx]$/.test(idCard.value)) {
  //   error.value = '请输入有效的18位身份证号';
  //   return;
  // }
    const typeNum = Number(type.value);
  if (!Number.isInteger(typeNum) || typeNum < 1 || typeNum > 5) {
    error.value = '房型只能是数字1到5';
    return;
  }
try {
    const res = await axios.post('/api/checkin', qs.stringify({
      idCard: idCard.value,
      type: type.value,
    }), {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    });
  // 根据状态码处理
  if (res.status === 201) {
    const data = res.data;  // 这里 data 是后端返回的 roomID 字符串
     const selectedRoom = roomTypes.find(r => r.id === type.value);
    emit('check-in-success', {
      idCard: idCard.value,
      roomNumber: data,  // 直接把字符串赋给 roomNumber
      wifiPassword: data + '88888',
      roomType: selectedRoom?.name,
      roomPrice: selectedRoom?.price
    });
    alert('入住成功！房间号：' + data);
  } else {
    error.value = '办理失败，请重试';
  }
} catch (e: any) {
  // 这里捕获HTTP错误，比如400等
  if (e.response) {
    if (e.response.status === 400) {
      error.value = '没有找到空闲房间，请更换房型';
    } else {
      error.value = e.response.data?.message || '办理失败，请重试';
    }
  } else {
    error.value = e.message || '办理失败，请重试';
  }
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
