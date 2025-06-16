<template>
  <div class="checkout-page">
    <h2>结算账单</h2>

    <div class="bill-section">
      <div>订单号：{{ orderId }}</div>
      <div>入住时间：{{ checkInDate }}</div>
      <div>退房时间：{{ checkOutDate }}</div>
    </div>

    <div class="bill-section">
      <h3>住宿费账单</h3>
      <div>房间号：{{ roomId }}</div>
      <div>房型：{{ props.roomType }}</div>
      <div>单价：{{ props.roomPrice }} 元/晚</div>
      <div>住宿费用总计：<b>{{ stayFee.toFixed(2) }} 元</b></div>
    </div>

    <div class="bill-section">
      <h3>空调账单</h3>
      <div>单价：1 元/度</div>
      <div>空调费用总计：<b>{{ acFee.toFixed(2) }} 元</b></div>
    </div>

    <div class="bill-section" v-if="detailRecords.length > 0">
      <h3>空调详单</h3>
      <table class="record-table">
        <thead>
          <tr>
            <th>序号</th>
            <th>风速</th>
            <th>开始时间</th>
            <th>结束时间</th>
            <th>服务时长(ms)</th>
            <th>费用(元)</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(record, index) in detailRecords" :key="record.detailRecordId">
            <td>{{ index + 1 }}</td>
            <td>{{ translateFanSpeed(record.fanSpeed) }}</td>
            <td>{{ record.serviceStartTime }}</td>
            <td>{{ record.serviceEndTime }}</td>
            <td>{{ record.serviceDuration }}</td>
            <td>￥{{ record.currentFee.toFixed(2) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="total">
      <h3>
        总计应交金额：
        <span :class="{ red: totalFee > 0, green: totalFee <= 0 }">
          {{ totalFee > 0 ? totalFee.toFixed(1) + ' 元' : '0 元' }}
        </span>
      </h3>
    </div>

    <button @click="printReceipt">打印收费凭据</button>
    <button @click="exportPDF">导出PDF</button>
    <button @click="exportExcel">导出Excel</button>
    <button @click="$emit('backToCheckIn')">退房成功</button>
  </div>
</template>

<script setup lang="ts">
import axios from 'axios';
import { saveAs } from 'file-saver';
import { defineProps, onMounted, ref } from 'vue';
import * as XLSX from 'xlsx';

const props = defineProps<{
  roomNumber: string;
  roomType: string;
  roomPrice: number;
  // deposit: number;
}>();

const orderId = ref('');//订单号
const roomId = ref('');//房间号
const stayFee = ref(0);//住宿费
const acFee = ref(0);//空调费
const totalFee = ref(0);//应付总金额


const checkInDate = ref('');//入住时间
const checkOutDate = ref('');//退房时间

const detailRecords = ref<any[]>([])

onMounted(async () => {
  try {
    const res = await axios.post('/api/checkout', {
      roomId: props.roomNumber,
    })

    const order = res.data.order

    // 排序详单列表：按开始时间升序
    detailRecords.value = res.data.detailRecords.sort(
      (a, b) => new Date(a.serviceStartTime).getTime() - new Date(b.serviceStartTime).getTime()
    )

    // 提取字段
    orderId.value = order.orderId
    roomId.value = order.roomId
    checkInDate.value = order.checkInDate
    checkOutDate.value = order.checkOutDate
    alert(props.roomPrice + '元/晚')
    stayFee.value = order.day * props.roomPrice
    acFee.value = order.acfee || 0
    totalFee.value = stayFee.value + acFee.value
  } catch (err: any) {
    alert('获取账单失败：' + (err.response?.data?.message || err.message))
  }
})
function translateFanSpeed(speed: string): string {
  switch (speed) {
    case 'LOW': return '低风'
    case 'MEDIUM': return '中风'
    case 'HIGH': return '高风'
    default: return speed
  }

function formatDate(input: string): string {
  if (!input) return ''
  return new Date(input).toLocaleString()
}
}function printReceipt() {
  window.print();
}

// 导出 PDF
async function exportPDF() {
  const [{ default: jsPDF }, html2canvas] = await Promise.all([
    import('jspdf'),
    import('html2canvas')
  ]);

  const el = document.querySelector('.checkout-page') as HTMLElement;
  if (!el) return;

  const canvas = await html2canvas.default(el, {
    scale: 2, // 提高分辨率
    useCORS: true
  });

  const imgData = canvas.toDataURL('image/png');

  const pdf = new jsPDF({ unit: 'px', format: 'a4' });
  const pageWidth = pdf.internal.pageSize.getWidth();
  const pageHeight = pdf.internal.pageSize.getHeight();

  const imgWidth = pageWidth;
  const imgHeight = (canvas.height * pageWidth) / canvas.width;

  let position = 0;

  if (imgHeight <= pageHeight) {
    // 一页能放下，直接添加
    pdf.addImage(imgData, 'PNG', 0, 0, imgWidth, imgHeight);
  } else {
    // 多页处理
    let remainingHeight = imgHeight;
    let canvasPosition = 0;

    const pageCanvas = document.createElement('canvas');
    const context = pageCanvas.getContext('2d')!;
    const pixelRatio = 2; // 跟 html2canvas scale 保持一致
    const pageHeightPx = (pageHeight * canvas.width) / pageWidth;

    pageCanvas.width = canvas.width;
    pageCanvas.height = pageHeightPx;

    while (remainingHeight > 0) {
      context.clearRect(0, 0, pageCanvas.width, pageCanvas.height);
      context.drawImage(
        canvas,
        0,
        canvasPosition,
        canvas.width,
        pageHeightPx,
        0,
        0,
        canvas.width,
        pageHeightPx
      );

      const pageImgData = pageCanvas.toDataURL('image/png');
      if (position === 0) {
        pdf.addImage(pageImgData, 'PNG', 0, 0, pageWidth, pageHeight);
      } else {
        pdf.addPage();
        pdf.addImage(pageImgData, 'PNG', 0, 0, pageWidth, pageHeight);
      }

      remainingHeight -= pageHeightPx;
      canvasPosition += pageHeightPx;
      position++;
    }
  }

  pdf.save('收费凭据.pdf');
}


function exportExcel() {
  if (detailRecords.value.length === 0) {
    alert('暂无空调详单可导出')
    return
  }

  // 1. 构造表格数据
  const excelData = detailRecords.value.map((record, index) => ({
    房间号: roomId.value,
    序号: index + 1,
    风速: translateFanSpeed(record.fanSpeed),
    开始时间: record.serviceStartTime,
    结束时间: record.serviceEndTime,
    服务时长_毫秒: record.serviceDuration,
    当前费用_元: record.currentFee.toFixed(2),
    费率: 1,
  }))

  // 2. 创建工作表
  const worksheet = XLSX.utils.json_to_sheet(excelData)

  // 3. 创建工作簿
  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, '空调详单')

  // 4. 生成并下载文件
  const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' })
  const blob = new Blob([excelBuffer], { type: 'application/octet-stream' })
  saveAs(blob, `空调详单-${roomId.value}.xlsx`)
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
.record-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
  font-size: 14px;
}
.record-table th,
.record-table td {
  border: 1px solid #ddd;
  padding: 6px 8px;
  text-align: center;
}
.record-table th {
  background-color: #f9f9f9;
  font-weight: bold;
}

</style>
