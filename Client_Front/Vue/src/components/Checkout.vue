<template>
  <div class="checkout-page">
    <h2>结算账单</h2>
    <div class="bill-section">
      <h3>住宿费账单</h3>
      <div>房间号：{{ roomNumber }}</div>
      <div>入住天数：{{ days }} 天</div>
      <div>单价:500 元/天</div>
      <div>合计：<b>{{ stayFee }} 元</b></div>
    </div>
    <div class="bill-section">
      <h3>空调账单</h3>
      <div>用电量：{{ acUsage }} 度</div>
      <div>单价：1.2 元/度</div>
      <div>合计：<b>{{ acFee }} 元</b></div>
    </div>
    <div class="bill-section">
      <h3>押金结算</h3>
      <div>已缴押金：{{ deposit }} 元</div>
      <div>应退押金：{{ refund.toFixed(1) }} 元</div>
    </div>
    <div class="total">
      <h3>总计应交金额：<span :class="{red: total > 0, green: total <= 0}">{{ total > 0 ? total.toFixed(1) + ' 元' : '0 元' }}</span></h3>
    </div>
    <button @click="printReceipt">打印收费凭据</button>
    <button @click="exportPDF">导出PDF</button>
    <button @click="$emit('backToCheckIn')">退房成功</button>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios';
import { defineProps, onMounted, ref } from 'vue';

const props = defineProps<{
  roomNumber: string;
  days: number;
  deposit: number;
}>();

const stayFee = ref(0);//住宿费
const acUsage = ref(0);//空调用电量
const acFee = ref(0);//空调费
const refund = ref(0);//应退押金
const total = ref(0);//应付总金额

onMounted(async () => {
  try {
    // 向后端请求账单数据
    const res = await axios.post('/api/checkout', {
      roomNumber: props.roomNumber,
    });
    const data = res.data;

    // const data = {
    //   stayFee: 1500,
    //   acUsage: 12.5,
    //   acFee: 15,
    //   deposit: props.deposit,
    //   refund: 0,
    //   total: 15
    // };
    stayFee.value = data.stayFee;
    acUsage.value = data.acUsage;
    acFee.value = data.acFee;
    refund.value = data.refund;
    total.value = data.total;
  } catch (e: any) {
    alert('获取账单失败: ' + (e.response?.data?.message || e.message));
  }
});

function printReceipt() {
  window.print();
}

async function exportPDF() {
  const [{ default: jsPDF }, html2canvas] = await Promise.all([
    import('jspdf'),
    import('html2canvas')
  ]);
  const el = document.querySelector('.checkout-page') as HTMLElement;
  if (!el) return;
  const canvas = await html2canvas.default(el);
  const imgData = canvas.toDataURL('image/png');
  const pdf = new jsPDF({ unit: 'px', format: 'a4' });
  const pageWidth = pdf.internal.pageSize.getWidth();
  const pageHeight = pdf.internal.pageSize.getHeight();
  const imgWidth = pageWidth - 40;
  const imgHeight = canvas.height * (imgWidth / canvas.width);
  pdf.addImage(imgData, 'PNG', 20, 20, imgWidth, imgHeight);
  pdf.save('收费凭据.pdf');
}
</script>

<style scoped>
.checkout-page {
  max-width: 420px;
  margin: 40px auto;
  padding: 28px;
  border: 1px solid #eee;
  border-radius: 10px;
  background: #fff;
  font-size: 16px;
}
.bill-section {
  margin-bottom: 18px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #ccc;
}
.total {
  margin: 18px 0;
  font-size: 20px;
}
.red {
  color: #d32f2f;
}
.green {
  color: #388e3c;
}
button {
  margin: 8px 10px 0 0;
  padding: 6px 18px;
  background: #42b983;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:last-child {
  background: #888;
}
</style>
