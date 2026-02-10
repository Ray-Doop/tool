<!--
  @generated-file-note
  文件：smart-tools-frontend/src/components/ChartToolBase.vue
  用途：前端通用组件（可复用 UI 组件）
  归属：前端 components
  依赖：vue、element-plus、echarts、xlsx
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<!--
  ChartToolBase：通用图表工具组件
  - 支持表格/XY/JSON/函数/词云/雷达等输入模式
  - 统一生成 ECharts 预览，并提供导出能力
-->
<template>
  <div class="grid gap-4">
    <el-card>
      <div class="flex flex-wrap items-start justify-between gap-3">
        <div class="min-w-0">
          <div class="truncate text-lg font-semibold">{{ title }}</div>
          <div class="text-sm text-slate-500">{{ description }}</div>
        </div>
        <div class="flex flex-wrap items-center gap-2">
          <el-button size="small" @click="resetData">重置数据</el-button>
          <el-button size="small" type="primary" @click="downloadImage">导出 PNG</el-button>
        </div>
      </div>

      <div class="mt-4 grid gap-3">
        <template v-if="mode === 'excel'">
          <div class="flex flex-wrap items-center gap-2">
            <el-button size="small" @click="triggerExcelUpload">上传 Excel</el-button>
            <el-button size="small" @click="addExcelRow">添加一行</el-button>
            <el-button size="small" @click="clearExcelRows">清空数据</el-button>
            <input ref="excelInput" type="file" class="hidden" accept=".xlsx,.xls,.csv" @change="onExcelUpload" />
          </div>
          <el-table :data="excelRows" height="360" border>
            <el-table-column type="index" width="60" />
            <el-table-column v-for="col in excelColumns" :key="col.key" :label="col.label">
              <template #default="{ row }">
                <el-input-number
                  v-if="col.type === 'number'"
                  v-model="row[col.key]"
                  :controls="false"
                  class="w-full"
                />
                <el-input v-else v-model="row[col.key]" />
              </template>
            </el-table-column>
            <el-table-column width="80">
              <template #default="{ $index }">
                <el-button size="small" @click="removeExcelRow($index)">删</el-button>
              </template>
            </el-table-column>
          </el-table>
        </template>

        <template v-else-if="mode === 'table'">
          <div class="text-sm text-slate-500">数据表</div>
          <div class="grid gap-2">
            <div v-for="(row, idx) in tableRows" :key="idx" class="grid grid-cols-12 gap-2">
              <el-input v-model="row.label" class="col-span-7" placeholder="名称" />
              <el-input-number v-model="row.value" class="col-span-4 w-full" :controls="false" placeholder="数值" />
              <el-button class="col-span-1" @click="removeTableRow(idx)">删</el-button>
            </div>
          </div>
          <div class="flex flex-wrap items-center gap-2">
            <el-button size="small" @click="addTableRow">添加一行</el-button>
          </div>
        </template>

        <template v-else-if="mode === 'xy'">
          <div class="text-sm text-slate-500">散点数据</div>
          <div class="grid gap-2">
            <div v-for="(row, idx) in xyRows" :key="idx" class="grid grid-cols-12 gap-2">
              <el-input-number v-model="row.x" class="col-span-5 w-full" :controls="false" placeholder="X" />
              <el-input-number v-model="row.y" class="col-span-5 w-full" :controls="false" placeholder="Y" />
              <el-button class="col-span-2" @click="removeXyRow(idx)">删</el-button>
            </div>
          </div>
          <el-button size="small" @click="addXyRow">添加一行</el-button>
        </template>

        <template v-else-if="mode === 'radar'">
          <div class="text-sm text-slate-500">雷达指标</div>
          <div class="grid gap-2">
            <div v-for="(row, idx) in radarRows" :key="idx" class="grid grid-cols-12 gap-2">
              <el-input v-model="row.name" class="col-span-5" placeholder="指标" />
              <el-input-number v-model="row.max" class="col-span-3 w-full" :controls="false" placeholder="最大值" />
              <el-input-number v-model="row.value" class="col-span-3 w-full" :controls="false" placeholder="当前值" />
              <el-button class="col-span-1" @click="removeRadarRow(idx)">删</el-button>
            </div>
          </div>
          <el-button size="small" @click="addRadarRow">添加一行</el-button>
        </template>

        <template v-else-if="mode === 'json'">
          <div class="text-sm text-slate-500">JSON 数据</div>
          <el-input v-model="jsonText" type="textarea" :rows="8" placeholder="输入 JSON 数据" />
        </template>

        <template v-else-if="mode === 'single'">
          <div class="text-sm text-slate-500">当前值</div>
          <el-input-number v-model="singleValue" class="w-full" :controls="false" placeholder="数值" />
        </template>

        <template v-else-if="mode === 'function'">
          <div class="text-sm text-slate-500">函数表达式</div>
          <el-input v-model="functionExpression" placeholder="例如：Math.sin(x)" />
          <div class="grid grid-cols-3 gap-2">
            <el-input-number v-model="xMin" :controls="false" placeholder="X 最小值" />
            <el-input-number v-model="xMax" :controls="false" placeholder="X 最大值" />
            <el-input-number v-model="step" :controls="false" placeholder="步长" />
          </div>
        </template>
      </div>
    </el-card>

    <el-card>
      <div class="text-sm text-slate-500">图表预览</div>
      <div ref="chartEl" class="mt-3 h-[420px] w-full" />
      <div v-if="errorMessage" class="mt-2 text-sm text-rose-500">{{ errorMessage }}</div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import * as XLSX from 'xlsx'

const props = defineProps({
  title: { type: String, default: '' },
  description: { type: String, default: '' },
  chartType: { type: String, required: true },
  mode: { type: String, default: 'table' },
  sample: { type: Object, default: () => ({}) }
})

const mode = computed(() => props.mode)

// 图表容器与实例
const chartEl = ref(null)
const chartInstance = ref(null)
const errorMessage = ref('')

// 默认数据生成器
function defaultTableRows() {
  return [
    { label: '一月', value: 120 },
    { label: '二月', value: 200 },
    { label: '三月', value: 150 },
    { label: '四月', value: 80 }
  ]
}

function defaultXyRows() {
  return [
    { x: 5, y: 20 },
    { x: 10, y: 36 },
    { x: 15, y: 10 },
    { x: 20, y: 28 }
  ]
}

function defaultRadarRows() {
  return [
    { name: '性能', max: 100, value: 80 },
    { name: '体验', max: 100, value: 90 },
    { name: '稳定', max: 100, value: 70 },
    { name: '安全', max: 100, value: 85 }
  ]
}


function defaultJsonText(type) {
  if (type === 'sankey') {
    return JSON.stringify({
      nodes: [{ name: '访问' }, { name: '注册' }, { name: '转化' }],
      links: [
        { source: '访问', target: '注册', value: 120 },
        { source: '注册', target: '转化', value: 60 }
      ]
    }, null, 2)
  }
  if (type === 'graph') {
    return JSON.stringify({
      nodes: [
        { id: 'A', name: '人物 A', value: 10 },
        { id: 'B', name: '人物 B', value: 8 },
        { id: 'C', name: '人物 C', value: 6 }
      ],
      links: [
        { source: 'A', target: 'B' },
        { source: 'A', target: 'C' }
      ]
    }, null, 2)
  }
  return JSON.stringify({
    name: '总览',
    children: [
      { name: '模块 A', value: 120 },
      { name: '模块 B', value: 80 },
      { name: '模块 C', value: 60 }
    ]
  }, null, 2)
}

function cloneRows(rows) {
  return rows.map(row => ({ ...row }))
}

const defaultState = {
  tableRows: cloneRows(props.sample.tableRows || defaultTableRows()),
  xyRows: cloneRows(props.sample.xyRows || defaultXyRows()),
  radarRows: cloneRows(props.sample.radarRows || defaultRadarRows()),
  jsonText: props.sample.jsonText || defaultJsonText(props.chartType),
  singleValue: typeof props.sample.singleValue === 'number' ? props.sample.singleValue : 65,
  functionExpression: props.sample.expression || 'Math.sin(x)',
  xMin: typeof props.sample.xMin === 'number' ? props.sample.xMin : -10,
  xMax: typeof props.sample.xMax === 'number' ? props.sample.xMax : 10,
  step: typeof props.sample.step === 'number' ? props.sample.step : 0.5
}

// 各类输入数据
const tableRows = ref(cloneRows(defaultState.tableRows))
const xyRows = ref(cloneRows(defaultState.xyRows))
const radarRows = ref(cloneRows(defaultState.radarRows))
const jsonText = ref(defaultState.jsonText)
const singleValue = ref(defaultState.singleValue)
const functionExpression = ref(defaultState.functionExpression)
const xMin = ref(defaultState.xMin)
const xMax = ref(defaultState.xMax)
const step = ref(defaultState.step)
const excelInput = ref(null)

const excelColumns = computed(() => {
  if (props.chartType === 'scatter') {
    return [
      { key: 'x', label: 'X', type: 'number' },
      { key: 'y', label: 'Y', type: 'number' }
    ]
  }
  if (props.chartType === 'radar') {
    return [
      { key: 'name', label: '指标' },
      { key: 'max', label: '最大值', type: 'number' },
      { key: 'value', label: '当前值', type: 'number' }
    ]
  }
  if (props.chartType === 'sunburst' || props.chartType === 'tree') {
    return [
      { key: 'level1', label: '一级分类' },
      { key: 'level2', label: '二级分类' },
      { key: 'level3', label: '三级分类' },
      { key: 'value', label: '数值', type: 'number' }
    ]
  }
  return [
    { key: 'label', label: '名称' },
    { key: 'value', label: '数值', type: 'number' }
  ]
})

function defaultExcelRows() {
  if (props.sample.excelRows?.length) {
    return cloneRows(props.sample.excelRows)
  }
  if (props.chartType === 'scatter') {
    return cloneRows(defaultState.xyRows).map(row => ({ x: row.x, y: row.y }))
  }
  if (props.chartType === 'radar') {
    return cloneRows(defaultState.radarRows).map(row => ({ name: row.name, max: row.max, value: row.value }))
  }
  if (props.chartType === 'sunburst' || props.chartType === 'tree') {
    return [
      { level1: '商品A', level2: '球类', level3: '篮球', value: 17 },
      { level1: '商品A', level2: '球类', level3: '足球', value: 84 },
      { level1: '商品A', level2: '球类', level3: '乒乓球', value: 8 },
      { level1: '商品A', level2: '球类', level3: '排球', value: 50 },
      { level1: '商品A', level2: '跑步类', level3: '跑步机', value: 13 },
      { level1: '商品A', level2: '跑步类', level3: '椭圆仪', value: 81 },
      { level1: '商品A', level2: '跑步类', level3: '动感单车', value: 29 },
      { level1: '商品B', level2: '球类', level3: '篮球', value: 38 },
      { level1: '商品B', level2: '球类', level3: '足球', value: 28 },
      { level1: '商品B', level2: '球类', level3: '乒乓球', value: 58 },
      { level1: '商品B', level2: '球类', level3: '排球', value: 44 },
      { level1: '商品B', level2: '跑步类', level3: '跑步机', value: 15 },
      { level1: '商品B', level2: '跑步类', level3: '椭圆仪', value: 5 },
      { level1: '商品B', level2: '跑步类', level3: '动感单车', value: 98 }
    ]
  }
  return cloneRows(defaultState.tableRows).map(row => ({ label: row.label, value: row.value }))
}

const excelRows = ref(defaultExcelRows())

function createEmptyExcelRow() {
  const row = {}
  for (const col of excelColumns.value) {
    row[col.key] = col.type === 'number' ? 0 : ''
  }
  return row
}

function addExcelRow() {
  excelRows.value.push(createEmptyExcelRow())
}

function removeExcelRow(index) {
  excelRows.value.splice(index, 1)
}

function clearExcelRows() {
  excelRows.value = [createEmptyExcelRow()]
}

function triggerExcelUpload() {
  if (excelInput.value) {
    excelInput.value.value = ''
    excelInput.value.click()
  }
}

function normalizeExcelRows(rows) {
  const columns = excelColumns.value
  if (!rows.length) return []
  const headerRow = rows[0]
  const headerMatch = headerRow.some(cell => columns.some(col => String(cell).trim() === col.label))
  const startIndex = headerMatch ? 1 : 0
  const result = []
  for (let i = startIndex; i < rows.length; i += 1) {
    const source = rows[i]
    if (!source || source.every(cell => String(cell).trim() === '')) continue
    const item = {}
    columns.forEach((col, idx) => {
      const value = source[idx]
      if (col.type === 'number') {
        item[col.key] = Number(value) || 0
      } else {
        item[col.key] = value == null ? '' : String(value)
      }
    })
    result.push(item)
  }
  return result
}

async function onExcelUpload(event) {
  const target = event.target
  const file = target?.files?.[0]
  if (!file) return
  const buffer = await file.arrayBuffer()
  const workbook = XLSX.read(buffer, { type: 'array' })
  const sheet = workbook.Sheets[workbook.SheetNames[0]]
  const rows = XLSX.utils.sheet_to_json(sheet, { header: 1, raw: true, defval: '' })
  const normalized = normalizeExcelRows(rows)
  if (!normalized.length) {
    ElMessage.warning('未识别到有效数据')
    return
  }
  excelRows.value = normalized
}

function addTableRow() {
  tableRows.value.push({ label: '', value: 0 })
}

function removeTableRow(index) {
  tableRows.value.splice(index, 1)
}

function addXyRow() {
  xyRows.value.push({ x: 0, y: 0 })
}

function removeXyRow(index) {
  xyRows.value.splice(index, 1)
}

function addRadarRow() {
  radarRows.value.push({ name: '', max: 100, value: 0 })
}

function removeRadarRow(index) {
  radarRows.value.splice(index, 1)
}

function resetData() {
  tableRows.value = cloneRows(defaultState.tableRows)
  xyRows.value = cloneRows(defaultState.xyRows)
  radarRows.value = cloneRows(defaultState.radarRows)
  jsonText.value = defaultState.jsonText
  singleValue.value = defaultState.singleValue
  functionExpression.value = defaultState.functionExpression
  xMin.value = defaultState.xMin
  xMax.value = defaultState.xMax
  step.value = defaultState.step
  excelRows.value = defaultExcelRows()
}

// 解析 JSON 数据，失败时给出提示
function parseJsonValue(text) {
  try {
    const parsed = JSON.parse(text)
    errorMessage.value = ''
    return parsed
  } catch (err) {
    errorMessage.value = 'JSON 解析失败，请检查格式'
    return null
  }
}

function buildOption() {
  errorMessage.value = ''
  if (props.mode === 'excel') {
    if (props.chartType === 'line' || props.chartType === 'bar') {
      const labels = excelRows.value.map(row => row.label || '')
      const values = excelRows.value.map(row => Number(row.value) || 0)
      return {
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: labels },
        yAxis: { type: 'value' },
        series: [{ type: props.chartType, data: values, smooth: props.chartType === 'line' }]
      }
    }
    if (props.chartType === 'pie' || props.chartType === 'donut' || props.chartType === 'rose') {
      const data = excelRows.value.map(row => ({ name: row.label || '未命名', value: Number(row.value) || 0 }))
      return {
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie',
          radius: props.chartType === 'donut' ? ['45%', '70%'] : '70%',
          roseType: props.chartType === 'rose' ? 'radius' : undefined,
          data
        }]
      }
    }
    if (props.chartType === 'funnel') {
      const data = excelRows.value.map(row => ({ name: row.label || '未命名', value: Number(row.value) || 0 }))
      return {
        tooltip: { trigger: 'item' },
        series: [{
          type: 'funnel',
          sort: 'descending',
          data
        }]
      }
    }
    if (props.chartType === 'scatter') {
      const data = excelRows.value.map(row => [Number(row.x) || 0, Number(row.y) || 0])
      return {
        tooltip: { trigger: 'item' },
        xAxis: { type: 'value' },
        yAxis: { type: 'value' },
        series: [{ type: 'scatter', data }]
      }
    }
    if (props.chartType === 'radar') {
      const indicator = excelRows.value.map(row => ({ name: row.name || '指标', max: Number(row.max) || 100 }))
      const values = excelRows.value.map(row => Number(row.value) || 0)
      return {
        tooltip: {},
        radar: { indicator },
        series: [{ type: 'radar', data: [{ value: values, name: '数据' }] }]
      }
    }
    if (props.chartType === 'sunburst' || props.chartType === 'tree') {
      const root = { name: '总览', children: [] }
      const ensureChild = (parent, name) => {
        const existing = parent.children.find(child => child.name === name)
        if (existing) return existing
        const child = { name, children: [] }
        parent.children.push(child)
        return child
      }
      for (const row of excelRows.value) {
        const l1 = row.level1?.trim?.() || row.level1 || ''
        const l2 = row.level2?.trim?.() || row.level2 || ''
        const l3 = row.level3?.trim?.() || row.level3 || ''
        const val = Number(row.value) || 0
        if (!l1) continue
        let current = ensureChild(root, l1)
        if (l2) current = ensureChild(current, l2)
        if (l3) current = ensureChild(current, l3)
        current.value = (current.value || 0) + val
      }
      const rollup = node => {
        if (!node.children || node.children.length === 0) return node.value || 0
        let sum = 0
        for (const child of node.children) {
          sum += rollup(child)
        }
        if (!node.value) node.value = sum
        return node.value
      }
      rollup(root)
      if (props.chartType === 'tree') {
        return {
          series: [{
            type: 'tree',
            data: [root],
            top: '5%',
            left: '15%',
            bottom: '5%',
            right: '20%',
            symbolSize: 10,
            label: { position: 'left', verticalAlign: 'middle', align: 'right' }
          }]
        }
      }
      return {
        series: [{
          type: 'sunburst',
          data: root.children || [],
          radius: [0, '90%'],
          label: { rotate: 'radial' }
        }]
      }
    }
  }
  if (props.chartType === 'line' || props.chartType === 'bar') {
    const labels = tableRows.value.map(row => row.label || '')
    const values = tableRows.value.map(row => Number(row.value) || 0)
    return {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: labels },
      yAxis: { type: 'value' },
      series: [{ type: props.chartType, data: values, smooth: props.chartType === 'line' }]
    }
  }
  if (props.chartType === 'pie' || props.chartType === 'donut' || props.chartType === 'rose') {
    const data = tableRows.value.map(row => ({ name: row.label || '未命名', value: Number(row.value) || 0 }))
    return {
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: props.chartType === 'donut' ? ['45%', '70%'] : '70%',
        roseType: props.chartType === 'rose' ? 'radius' : undefined,
        data
      }]
    }
  }
  if (props.chartType === 'funnel') {
    const data = tableRows.value.map(row => ({ name: row.label || '未命名', value: Number(row.value) || 0 }))
    return {
      tooltip: { trigger: 'item' },
      series: [{
        type: 'funnel',
        sort: 'descending',
        data
      }]
    }
  }
  if (props.chartType === 'scatter') {
    const data = xyRows.value.map(row => [Number(row.x) || 0, Number(row.y) || 0])
    return {
      tooltip: { trigger: 'item' },
      xAxis: { type: 'value' },
      yAxis: { type: 'value' },
      series: [{ type: 'scatter', data }]
    }
  }
  if (props.chartType === 'radar') {
    const indicator = radarRows.value.map(row => ({ name: row.name || '指标', max: Number(row.max) || 100 }))
    const values = radarRows.value.map(row => Number(row.value) || 0)
    return {
      tooltip: {},
      radar: { indicator },
      series: [{ type: 'radar', data: [{ value: values, name: '数据' }] }]
    }
  }
  if (props.chartType === 'gauge') {
    return {
      series: [{
        type: 'gauge',
        progress: { show: true },
        detail: { valueAnimation: true, formatter: '{value}%' },
        data: [{ value: Number(singleValue.value) || 0, name: '完成度' }]
      }]
    }
  }
  if (props.chartType === 'function') {
    const min = Number(xMin.value)
    const max = Number(xMax.value)
    const stepValue = Number(step.value)
    if (!Number.isFinite(min) || !Number.isFinite(max) || !Number.isFinite(stepValue) || stepValue <= 0 || min >= max) {
      errorMessage.value = '函数参数不合法，请检查范围与步长'
      return null
    }
    try {
      const fn = new Function('x', `return ${functionExpression.value}`)
      const points = []
      for (let x = min; x <= max; x += stepValue) {
        const y = Number(fn(x))
        if (Number.isFinite(y)) {
          points.push([x, y])
        }
      }
      return {
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'value' },
        yAxis: { type: 'value' },
        series: [{ type: 'line', smooth: true, data: points }]
      }
    } catch (err) {
      errorMessage.value = '函数表达式解析失败'
      return null
    }
  }

  const parsed = parseJsonValue(jsonText.value)
  if (!parsed) return null
  if (props.chartType === 'tree') {
    return {
      series: [{
        type: 'tree',
        data: [parsed],
        top: '5%',
        left: '15%',
        bottom: '5%',
        right: '20%',
        symbolSize: 10,
        label: { position: 'left', verticalAlign: 'middle', align: 'right' }
      }]
    }
  }
  if (props.chartType === 'sunburst') {
    return {
      series: [{
        type: 'sunburst',
        data: [parsed],
        radius: [0, '90%'],
        label: { rotate: 'radial' }
      }]
    }
  }
  if (props.chartType === 'sankey') {
    return {
      series: [{
        type: 'sankey',
        data: parsed.nodes || [],
        links: parsed.links || [],
        emphasis: { focus: 'adjacency' }
      }]
    }
  }
  if (props.chartType === 'graph') {
    return {
      tooltip: {},
      series: [{
        type: 'graph',
        layout: 'force',
        data: parsed.nodes || [],
        links: parsed.links || [],
        roam: true,
        label: { show: true },
        force: { repulsion: 100, edgeLength: 120 }
      }]
    }
  }
  return null
}

function updateChart() {
  if (!chartInstance.value) return
  const option = buildOption()
  if (!option) {
    chartInstance.value.clear()
    return
  }
  chartInstance.value.setOption(option, true)
}

function downloadImage() {
  if (!chartInstance.value) {
    ElMessage.warning('图表未初始化')
    return
  }
  const url = chartInstance.value.getDataURL({ type: 'png', pixelRatio: 2, backgroundColor: '#ffffff' })
  const link = document.createElement('a')
  link.href = url
  link.download = `${props.title || 'chart'}.png`
  link.click()
}

// 初始化图表实例
onMounted(() => {
  if (!chartEl.value) return
  chartInstance.value = echarts.init(chartEl.value)
  updateChart()
  window.addEventListener('resize', updateChart)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateChart)
  if (chartInstance.value) {
    chartInstance.value.dispose()
    chartInstance.value = null
  }
})

// 监听数据变化刷新图表
watch(tableRows, updateChart, { deep: true })
watch(excelRows, updateChart, { deep: true })
watch(xyRows, updateChart, { deep: true })
watch(radarRows, updateChart, { deep: true })
watch([jsonText, singleValue, functionExpression, xMin, xMax, step], updateChart)
</script>
