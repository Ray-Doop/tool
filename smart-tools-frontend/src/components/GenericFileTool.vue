<!--
  @generated-file-note
  文件：smart-tools-frontend/src/components/GenericFileTool.vue
  用途：前端通用组件（可复用 UI 组件）
  归属：前端 components
  交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
  安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
-->
<template>
  <div class="max-w-5xl mx-auto space-y-8 animate-slide-up">
    <!-- Main Interaction Card -->
    <div class="relative overflow-hidden rounded-3xl bg-white shadow-xl shadow-slate-200/50 border border-slate-100 transition-all duration-500 hover:shadow-2xl hover:shadow-indigo-100/20">
      <div class="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 opacity-80"></div>
      
      <div class="p-6 md:p-10 space-y-8">
        
        <!-- File Upload Zone -->
        <div 
          v-if="isUploadMode"
          class="group relative flex flex-col items-center justify-center rounded-3xl border-2 border-dashed border-slate-200 bg-slate-50/50 px-8 py-16 transition-all duration-300 hover:border-indigo-500 hover:bg-indigo-50/30 hover:shadow-inner overflow-hidden"
          @dragover.prevent
          @drop.prevent="onDrop"
        >
          <div class="absolute inset-0 bg-slate-100/50 opacity-0 group-hover:opacity-100 transition-opacity duration-500 pointer-events-none"></div>
          <div class="relative z-10 text-center">
            <div class="mx-auto flex h-20 w-20 items-center justify-center rounded-3xl bg-indigo-50 text-indigo-500 shadow-sm mb-6 group-hover:scale-110 group-hover:rotate-3 transition-transform duration-300">
              <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
              </svg>
            </div>
            <div class="mt-6 flex flex-col items-center text-slate-600">
              <label class="relative cursor-pointer rounded-xl bg-indigo-600 px-8 py-3 text-sm font-semibold text-white shadow-lg shadow-indigo-200 transition-all duration-300 hover:bg-indigo-500 hover:shadow-xl hover:shadow-indigo-300 hover:-translate-y-0.5 active:translate-y-0">
                <span>选择文件</span>
                <input 
                  type="file" 
                  class="sr-only" 
                  :multiple="['pdf-merge', 'images-to-pdf', 'zip-compress', 'file-merge', 'batch-rename'].includes(mode)"
                  :accept="getAcceptAttr()"
                  @change="['pdf-merge', 'images-to-pdf', 'zip-compress', 'file-merge', 'batch-rename'].includes(mode) ? onFilesChange($event) : onFileChange($event)" 
                />
              </label>
              <p class="mt-4 text-sm font-medium text-slate-500">或直接将文件拖拽到此处</p>
            </div>
            <p class="mt-2 text-xs text-slate-400 font-medium bg-slate-100/80 px-3 py-1 rounded-full">支持 {{ getAcceptedFormats() }} 格式</p>
          </div>

          <!-- 已选文件列表 -->
          <div v-if="files.length > 0" class="mt-6 w-full max-w-lg">
            <div class="text-sm font-medium text-slate-700 mb-2">已选文件 ({{ files.length }})</div>
            <ul class="divide-y divide-slate-100 rounded-lg border border-slate-200 bg-white">
              <li v-for="(f, index) in files" :key="index" class="flex items-center justify-between p-3 text-sm">
                <div class="flex items-center gap-2 overflow-hidden">
                  <svg class="h-4 w-4 shrink-0 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                  <span class="truncate text-slate-600">{{ f.name }}</span>
                </div>
                <span class="text-xs text-slate-400 whitespace-nowrap">{{ formatSize(f.size) }}</span>
              </li>
            </ul>
          </div>
          
          <div v-if="file" class="mt-6 w-full max-w-lg">
            <div class="flex items-center justify-between rounded-lg border border-slate-200 bg-white p-3 text-sm">
              <div class="flex items-center gap-2 overflow-hidden">
                <svg class="h-4 w-4 shrink-0 text-indigo-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <span class="truncate font-medium text-slate-700">{{ file.name }}</span>
              </div>
              <span class="text-xs text-slate-400 whitespace-nowrap">{{ formatSize(file.size) }}</span>
            </div>
          </div>
        </div>

        <!-- 文本输入区 -->
        <div v-else class="grid gap-2">
          <el-input 
            v-model="textInput" 
            type="textarea" 
            :rows="10" 
            :placeholder="textPlaceholder" 
            class="w-full"
          />
        </div>

        <!-- Settings Section -->
        <div v-if="files.length || file || textInput" class="rounded-2xl bg-slate-50/50 p-6 md:p-8 border border-slate-100 transition-all duration-300 hover:bg-slate-50 hover:shadow-sm">
          <h3 class="text-base font-bold text-slate-900 mb-6 flex items-center gap-2">
            <span class="w-1.5 h-5 bg-indigo-500 rounded-full"></span>
            配置参数
          </h3>
          <div class="grid gap-6">
             <!-- PDF Split -->
             <div v-if="mode === 'pdf-split'" class="grid gap-2">
               <el-radio-group v-model="splitMode" size="small">
                 <el-radio-button label="each">单页拆分</el-radio-button>
                 <el-radio-button label="range">按页码范围</el-radio-button>
               </el-radio-group>
               <el-input v-if="splitMode === 'range'" v-model="splitPages" placeholder="例如：1-3,5,8-10" />
             </div>
             
             <!-- PDF Watermark -->
             <div v-if="mode === 'pdf-watermark'" class="grid gap-2">
               <el-input v-model="watermarkText" placeholder="水印文字" />
               <div class="grid grid-cols-3 gap-2">
                 <el-input-number v-model="watermarkFontSize" :controls="false" placeholder="字体大小" />
                 <el-input-number v-model="watermarkOpacity" :controls="false" placeholder="透明度" />
                 <el-input-number v-model="watermarkRotation" :controls="false" placeholder="旋转角度" />
               </div>
             </div>
             
             <!-- File Stamp -->
             <div v-if="mode === 'file-stamp'" class="grid gap-2">
               <el-input v-model="watermarkText" placeholder="盖章文字" />
               <div class="grid grid-cols-3 gap-2">
                 <el-input-number v-model="watermarkFontSize" :controls="false" placeholder="字体大小" />
                 <el-input-number v-model="watermarkOpacity" :controls="false" placeholder="透明度" />
                 <el-input-number v-model="watermarkRotation" :controls="false" placeholder="旋转角度" />
               </div>
             </div>

             <!-- PDF Page Number -->
             <div v-if="mode === 'pdf-page-number'" class="grid grid-cols-2 gap-2">
                <el-input-number v-model="pdfPageStart" :controls="false" placeholder="起始页码" />
                <el-input-number v-model="pdfPageFontSize" :controls="false" placeholder="字体大小" />
             </div>

             <!-- PDF Edit -->
             <div v-if="mode === 'pdf-edit'" class="grid gap-2">
               <el-input v-model="splitPages" placeholder="保留页码范围，例如：1-3,5" />
             </div>

             <!-- PDF Encrypt -->
             <div v-if="mode === 'pdf-encrypt'" class="grid gap-2">
               <el-input v-model="pdfPassword" placeholder="打开密码" />
               <el-input v-model="pdfOwnerPassword" placeholder="管理密码（可选）" />
             </div>

             <!-- PDF Decrypt -->
             <div v-if="mode === 'pdf-decrypt'" class="grid gap-2">
               <el-input v-model="pdfPassword" placeholder="PDF 密码" />
             </div>

             <!-- PDF Sign -->
             <div v-if="mode === 'pdf-sign'" class="grid gap-2">
               <el-input v-model="pdfSignName" placeholder="签名人" />
               <el-input v-model="pdfSignReason" placeholder="签名原因" />
               <el-input v-model="pdfSignLocation" placeholder="签名位置" />
             </div>

             <!-- PDF Metadata -->
             <div v-if="mode === 'pdf-metadata'" class="grid gap-2">
               <el-input v-model="pdfMetaTitle" placeholder="标题" />
               <el-input v-model="pdfMetaAuthor" placeholder="作者" />
               <el-input v-model="pdfMetaSubject" placeholder="主题" />
               <el-input v-model="pdfMetaKeywords" placeholder="关键词" />
               <el-input v-model="pdfPassword" placeholder="PDF 密码（可选）" />
             </div>

             <!-- PDF to Images -->
             <div v-if="mode === 'pdf-to-images'" class="grid grid-cols-2 gap-2">
               <el-input-number v-model="pdfRenderScale" :controls="false" placeholder="渲染缩放" />
               <el-select v-model="imageFormat" placeholder="输出格式">
                 <el-option label="PNG" value="png" />
                 <el-option label="JPEG" value="jpeg" />
               </el-select>
             </div>

             <!-- PDF OCR -->
             <div v-if="mode === 'pdf-ocr'" class="grid gap-2">
               <el-switch v-model="enableOcr" active-text="启用 OCR" inactive-text="仅提取可选文本" />
               <el-input v-model="ocrLang" placeholder="OCR 语言，例如：chi_sim+eng" />
             </div>

             <!-- Image Options -->
             <div v-if="mode === 'image-compress' || mode === 'image-format-convert'" class="grid grid-cols-2 gap-2">
               <el-select v-model="imageOutputFormat" placeholder="输出格式">
                 <el-option label="JPEG" value="image/jpeg" />
                 <el-option label="PNG" value="image/png" />
                 <el-option label="WebP" value="image/webp" />
               </el-select>
               <el-input-number v-model="imageQuality" :controls="false" placeholder="质量 0.1-1" />
             </div>

             <div v-if="mode === 'image-resize'" class="grid grid-cols-3 gap-2">
               <el-input-number v-model="resizeWidth" :controls="false" placeholder="宽度" />
               <el-input-number v-model="resizeHeight" :controls="false" placeholder="高度" />
               <el-switch v-model="keepAspect" active-text="保持比例" />
             </div>

             <div v-if="mode === 'image-crop'" class="grid grid-cols-4 gap-2">
               <el-input-number v-model="cropX" :controls="false" placeholder="X" />
               <el-input-number v-model="cropY" :controls="false" placeholder="Y" />
               <el-input-number v-model="cropWidth" :controls="false" placeholder="宽度" />
               <el-input-number v-model="cropHeight" :controls="false" placeholder="高度" />
             </div>

             <div v-if="mode === 'image-to-icon'" class="grid gap-2">
               <el-input v-model="iconSizes" placeholder="图标尺寸，例如：16,32,64,128" />
             </div>

             <!-- Text Split -->
             <div v-if="mode === 'text-split'" class="grid grid-cols-3 gap-2">
               <el-select v-model="textSplitMode" placeholder="拆分方式">
                 <el-option label="按行数" value="lines" />
                 <el-option label="按字节" value="bytes" />
               </el-select>
               <el-input-number v-model="textSplitSize" :controls="false" placeholder="大小" />
             </div>

             <!-- File Split -->
             <div v-if="mode === 'file-split'" class="grid gap-2">
               <el-input-number v-model="fileSplitSize" :controls="false" placeholder="拆分大小 KB" />
             </div>

             <!-- Batch Rename -->
             <div v-if="mode === 'batch-rename'" class="grid gap-2">
               <el-input v-model="batchRenamePrefix" placeholder="前缀" />
               <div class="grid grid-cols-2 gap-2">
                 <el-input-number v-model="batchRenameStart" :controls="false" placeholder="起始编号" />
                 <el-input v-model="batchRenameSuffix" placeholder="后缀" />
               </div>
               <el-switch v-model="batchRenameKeepExt" active-text="保留扩展名" />
             </div>

             <!-- Data Process -->
             <div v-if="mode === 'data-process'" class="grid grid-cols-2 gap-2">
               <el-input v-model="dataProcessColumn" placeholder="列名或列序号" />
               <el-select v-model="dataProcessOp" placeholder="统计方式">
                 <el-option label="求和" value="sum" />
                 <el-option label="平均值" value="avg" />
                 <el-option label="最小值" value="min" />
                 <el-option label="最大值" value="max" />
               </el-select>
             </div>

             <!-- Subtitle Convert -->
             <div v-if="mode === 'subtitle-convert'" class="grid grid-cols-2 gap-2">
               <el-select v-model="subtitleTarget" placeholder="目标格式">
                 <el-option label="SRT" value="srt" />
                 <el-option label="VTT" value="vtt" />
               </el-select>
             </div>

             <!-- Encoding Convert -->
             <div v-if="mode === 'encoding-convert'" class="grid gap-2">
               <el-input v-model="targetEncoding" placeholder="目标编码，例如：utf-8、gbk" />
             </div>

             <!-- Media Options -->
             <div v-if="isMediaMode" class="grid gap-2">
                <div class="flex flex-wrap items-center gap-2">
                  <el-button size="small" :disabled="ffmpegLoading || ffmpegReady" @click="loadFfmpeg">加载引擎</el-button>
                  <span class="text-xs text-slate-500">{{ ffmpegStatus }}</span>
                </div>
                <div v-if="mode === 'audio-converter'" class="grid grid-cols-2 gap-2">
                  <el-select v-model="audioFormat" placeholder="输出格式">
                    <el-option label="MP3" value="mp3" />
                    <el-option label="WAV" value="wav" />
                    <el-option label="AAC" value="aac" />
                    <el-option label="FLAC" value="flac" />
                    <el-option label="OGG" value="ogg" />
                  </el-select>
                  <el-input-number v-model="audioBitrate" :controls="false" placeholder="码率 kbps" />
                </div>
                <div v-if="mode === 'video-compress'" class="grid grid-cols-3 gap-2">
                  <el-input-number v-model="videoCrf" :controls="false" placeholder="CRF 18-32" />
                  <el-input-number v-model="videoWidth" :controls="false" placeholder="宽度" />
                  <el-input-number v-model="videoFps" :controls="false" placeholder="帧率" />
                </div>
                <div v-if="mode === 'video-to-gif'" class="grid grid-cols-2 gap-2">
                  <el-input-number v-model="gifWidth" :controls="false" placeholder="宽度" />
                  <el-input-number v-model="gifFps" :controls="false" placeholder="帧率" />
                </div>
                <div v-if="mode === 'audio-extract'">
                  <el-select v-model="extractFormat" placeholder="输出格式">
                    <el-option label="MP3" value="mp3" />
                    <el-option label="WAV" value="wav" />
                    <el-option label="AAC" value="aac" />
                  </el-select>
                </div>
             </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="flex flex-wrap items-center gap-4 pt-6 border-t border-slate-100">
          <el-button type="primary" size="large" :loading="busy" :disabled="!canRun" @click="runTool">
            {{ actionLabel }}
          </el-button>
          <el-button size="large" :disabled="busy" @click="clearFiles" v-if="files.length || file">清空</el-button>
          <el-button size="large" :disabled="busy" @click="resetState">重置</el-button>
        </div>
      </div>
    </div>

    <!-- Result Card -->
    <div v-if="outputUrl || outputText" class="overflow-hidden rounded-3xl border border-indigo-100 bg-white shadow-xl shadow-indigo-100/50 transition-all duration-500 animate-fade-in-up">
      <div class="border-b border-slate-100 px-6 py-4 flex items-center justify-between bg-slate-50/50">
        <div class="text-sm font-semibold text-slate-700">处理结果</div>
        <el-button v-if="outputText" size="small" text bg @click="copyText">复制内容</el-button>
      </div>
      <div class="px-6 py-6">
        <div class="grid gap-4">
          <div v-if="outputUrl" class="flex flex-col gap-4">
            <div v-if="isImagePreview" class="flex justify-center rounded-xl border border-slate-100 bg-slate-50/50 p-4">
              <img :src="outputUrl" class="max-h-[400px] rounded-lg object-contain shadow-sm" />
            </div>
            <div class="flex justify-center">
              <el-button type="primary" size="large" class="w-full sm:w-auto px-8" @click="downloadOutput">
                <svg class="mr-2 h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                </svg>
                下载文件
              </el-button>
            </div>
          </div>
          <el-input v-if="outputText" v-model="outputText" type="textarea" :rows="12" class="font-mono text-sm" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { http } from '@/api/http'
import { PDFDocument, rgb, StandardFonts } from 'pdf-lib'
import { getDocument, GlobalWorkerOptions } from 'pdfjs-dist/legacy/build/pdf'
import { Document, Packer, Paragraph } from 'docx'
import mammoth from 'mammoth'
import * as XLSX from 'xlsx'
import JSZip from 'jszip'
import { jsPDF } from 'jspdf'
import { format as formatSql } from 'sql-formatter'
import { createWorker } from 'tesseract.js'
import { FFmpeg } from '@ffmpeg/ffmpeg'
import { fetchFile } from '@ffmpeg/util'

GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.worker.min.js'

const props = defineProps({
  title: { type: String, default: '工具' },
  description: { type: String, default: '' },
  mode: { type: String, default: '' }
})

const file = ref(null)
const files = ref([])
const outputText = ref('')
const outputUrl = ref('')
const outputBlob = ref(null)
const outputName = ref('')
const busy = ref(false)
const splitMode = ref('each')
const splitPages = ref('')
const watermarkText = ref('示例水印')
const watermarkOpacity = ref(0.2)
const watermarkRotation = ref(45)
const watermarkFontSize = ref(36)
const pdfRenderScale = ref(1.5)
const imageFormat = ref('png')
const enableOcr = ref(false)
const ocrLang = ref('chi_sim+eng')
const imageOutputFormat = ref('image/jpeg')
const imageQuality = ref(0.8)
const resizeWidth = ref(800)
const resizeHeight = ref(600)
const keepAspect = ref(true)
const cropX = ref(0)
const cropY = ref(0)
const cropWidth = ref(300)
const cropHeight = ref(300)
const iconSizes = ref('16,32,64,128')
const textInput = ref('')
const ffmpegRef = ref(null)
const ffmpegReady = ref(false)
const ffmpegLoading = ref(false)
const audioFormat = ref('mp3')
const audioBitrate = ref(192)
const videoCrf = ref(24)
const videoWidth = ref(1280)
const videoFps = ref(30)
const gifWidth = ref(480)
const gifFps = ref(10)
const extractFormat = ref('mp3')
const textSplitMode = ref('lines')
const textSplitSize = ref(500)
const fileSplitSize = ref(1024)
const batchRenamePrefix = ref('file-')
const batchRenameStart = ref(1)
const batchRenameSuffix = ref('')
const batchRenameKeepExt = ref(true)
const dataProcessColumn = ref('')
const dataProcessOp = ref('sum')
const subtitleTarget = ref('srt')
const targetEncoding = ref('utf-8')
const pdfPassword = ref('')
const pdfOwnerPassword = ref('')
const pdfMetaTitle = ref('')
const pdfMetaAuthor = ref('')
const pdfMetaSubject = ref('')
const pdfMetaKeywords = ref('')
const pdfPageStart = ref(1)
const pdfPageFontSize = ref(12)
const pdfSignName = ref('')
const pdfSignReason = ref('')
const pdfSignLocation = ref('')

const isUploadMode = computed(() => !['csv-to-json', 'json-to-csv', 'sql-formatter'].includes(props.mode))

// 媒体类工具需要 FFmpeg 支持
const isMediaMode = computed(() => ['video-compress', 'audio-converter', 'video-to-gif', 'audio-extract', 'video-to-mp3'].includes(props.mode))
const canRun = computed(() => {
  const multiFileModes = ['pdf-merge', 'images-to-pdf', 'zip-compress', 'file-merge', 'batch-rename']
  if (multiFileModes.includes(props.mode)) return files.value.length > 0
  const fileModes = [
    'pdf-split', 'pdf-compress', 'pdf-watermark', 'pdf-to-images', 'pdf-to-word', 'word-to-pdf',
    'pdf-ocr', 'excel-to-pdf', 'ppt-to-pdf', 'image-compress', 'image-resize', 'image-crop',
    'image-format-convert', 'image-to-icon', 'video-compress', 'audio-converter', 'video-to-gif',
    'audio-extract', 'video-to-mp3', 'zip-extract', 'text-split', 'file-stamp', 'pdf-page-number',
    'pdf-edit', 'pdf-encrypt', 'pdf-decrypt', 'pdf-sign', 'pdf-metadata', 'file-split', 'data-process',
    'subtitle-convert', 'encoding-convert', 'file-type-detect'
  ]
  if (fileModes.includes(props.mode)) return !!file.value
  return !!textInput.value
})
// 根据工具类型动态给出按钮文案
const actionLabel = computed(() => {
  const map = {
    'pdf-to-word': '生成 Word',
    'word-to-pdf': '生成 PDF',
    'pdf-merge': '合并并下载',
    'pdf-split': '拆分并下载',
    'pdf-compress': '压缩并下载',
    'pdf-watermark': '添加水印',
    'pdf-to-images': '生成图片',
    'images-to-pdf': '生成 PDF',
    'pdf-ocr': '识别文本',
    'excel-to-pdf': '生成 PDF',
    'ppt-to-pdf': '生成 PDF',
    'image-compress': '压缩并下载',
    'image-resize': '调整尺寸',
    'image-crop': '裁剪并下载',
    'image-format-convert': '转换并下载',
    'image-to-icon': '生成图标包',
    'csv-to-json': '生成 JSON',
    'json-to-csv': '生成 CSV',
    'sql-formatter': '格式化',
    'video-compress': '压缩视频',
    'audio-converter': '转换音频',
    'video-to-gif': '生成 GIF',
    'audio-extract': '提取音频',
    'video-to-mp3': '生成 MP3',
    'zip-compress': '压缩并下载',
    'zip-extract': '解压并下载',
    'text-split': '拆分并下载',
    'file-stamp': '盖章并下载',
    'pdf-page-number': '添加页码',
    'pdf-edit': '编辑并下载',
    'pdf-encrypt': '加密并下载',
    'pdf-decrypt': '解密并下载',
    'pdf-sign': '签名并下载',
    'pdf-metadata': '修改并下载',
    'batch-rename': '重命名并下载',
    'file-merge': '合并并下载',
    'file-split': '拆分并下载',
    'data-process': '处理并下载',
    'subtitle-convert': '转换并下载',
    'encoding-convert': '转换并下载',
    'file-type-detect': '检测类型'
  }
  return map[props.mode] || '开始处理'
})

// 单文件输入
function onFileChange(event) {
  const target = event.target
  if (!target?.files?.length) return
  file.value = target.files[0]
  outputText.value = ''
  outputUrl.value = ''
  outputBlob.value = null
}

// 多文件输入
function onFilesChange(event) {
  const target = event.target
  if (!target?.files?.length) return
  files.value = Array.from(target.files)
  outputText.value = ''
  outputUrl.value = ''
  outputBlob.value = null
}

// 清理输入文件
function clearFiles() {
  file.value = null
  files.value = []
}

// 清空输入与输出
function resetState() {
  clearFiles()
  outputText.value = ''
  outputUrl.value = ''
  outputBlob.value = null
  outputName.value = ''
}

// 输出图片时使用预览组件
const isImagePreview = computed(() => ['image-compress', 'image-resize', 'image-crop', 'image-format-convert'].includes(props.mode))
const textPlaceholder = computed(() => {
  if (props.mode === 'csv-to-json') return '输入 CSV 数据'
  if (props.mode === 'json-to-csv') return '输入 JSON 数据（数组）'
  if (props.mode === 'sql-formatter') return '输入 SQL 语句'
  return ''
})

// 拖拽上传
function onDrop(event) {
  const droppedFiles = event.dataTransfer?.files
  if (!droppedFiles?.length) return
  
  if (['pdf-merge', 'images-to-pdf', 'zip-compress', 'file-merge', 'batch-rename'].includes(props.mode)) {
    files.value = Array.from(droppedFiles)
  } else {
    file.value = droppedFiles[0]
  }
  outputText.value = ''
  outputUrl.value = ''
  outputBlob.value = null
}

// 格式化文件大小
function formatSize(bytes) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 获取 accept 属性
function getAcceptAttr() {
  if (props.mode.includes('pdf')) return '.pdf'
  if (props.mode.includes('image') || props.mode === 'images-to-pdf') return 'image/*'
  if (props.mode.includes('video')) return 'video/*'
  if (props.mode.includes('audio')) return 'audio/*'
  if (props.mode.includes('zip')) return '.zip,.rar,.7z'
  if (props.mode.includes('csv')) return '.csv'
  return '*'
}

// 获取接受的文件格式描述
function getAcceptedFormats() {
  if (props.mode.includes('pdf')) return 'PDF'
  if (props.mode.includes('image') || props.mode === 'images-to-pdf') return '图片'
  if (props.mode.includes('video')) return '视频'
  if (props.mode.includes('audio')) return '音频'
  if (props.mode.includes('zip')) return '压缩包'
  if (props.mode.includes('csv')) return 'CSV'
  return '所有'
}

function copyText() {
  navigator.clipboard.writeText(outputText.value).then(() => {
    ElMessage.success('复制成功')
  })
}

// 构建单文件请求体
function buildSingleFormData() {
  const fd = new FormData()
  fd.append('file', file.value)
  return fd
}

// 构建多文件请求体
function buildMultiFormData() {
  const fd = new FormData()
  files.value.forEach(f => fd.append('files', f))
  return fd
}

// 通过 Blob 触发下载
function downloadBlob(blob, filename) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

// 统一下载类接口的调用与保存
async function postBlob(url, formData, filename) {
  const res = await http.post(url, formData, { responseType: 'blob' })
  const blob = res?.data instanceof Blob ? res.data : new Blob([res.data])
  outputBlob.value = blob
  outputName.value = filename
  downloadBlob(blob, filename)
  return res
}

// 读取为 ArrayBuffer（前端处理时使用）
async function readFileAsArrayBuffer(input) {
  return await input.arrayBuffer()
}

// 读取图片为 Image 对象
async function loadImageFromFile(input) {
  return await new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => {
      const img = new Image()
      img.onload = () => resolve(img)
      img.onerror = reject
      img.src = reader.result
    }
    reader.onerror = reject
    reader.readAsDataURL(input)
  })
}

// 解析页码范围字符串
function parsePageRanges(value, total) {
  const pages = new Set()
  const parts = value.split(',').map(v => v.trim()).filter(Boolean)
  for (const part of parts) {
    if (part.includes('-')) {
      const [start, end] = part.split('-').map(v => Number(v))
      if (!Number.isFinite(start) || !Number.isFinite(end)) continue
      for (let i = start; i <= end; i += 1) {
        if (i >= 1 && i <= total) pages.add(i)
      }
    } else {
      const p = Number(part)
      if (Number.isFinite(p) && p >= 1 && p <= total) pages.add(p)
    }
  }
  return Array.from(pages).sort((a, b) => a - b)
}

// 前端处理：PDF 合并
async function handlePdfMerge() {
  const pdfDoc = await PDFDocument.create()
  for (const f of files.value) {
    const bytes = await readFileAsArrayBuffer(f)
    const src = await PDFDocument.load(bytes)
    const pages = await pdfDoc.copyPages(src, src.getPageIndices())
    pages.forEach(page => pdfDoc.addPage(page))
  }
  const merged = await pdfDoc.save({ useObjectStreams: true })
  outputBlob.value = new Blob([merged], { type: 'application/pdf' })
  outputName.value = 'merged.pdf'
  downloadBlob(outputBlob.value, outputName.value)
}

// 前端处理：PDF 拆分
async function handlePdfSplit() {
  const bytes = await readFileAsArrayBuffer(file.value)
  const src = await PDFDocument.load(bytes)
  const total = src.getPageCount()
  const zip = new JSZip()
  const pagesToSplit = splitMode.value === 'range'
    ? parsePageRanges(splitPages.value, total)
    : Array.from({ length: total }, (_, i) => i + 1)
  for (const index of pagesToSplit) {
    const doc = await PDFDocument.create()
    const [page] = await doc.copyPages(src, [index - 1])
    doc.addPage(page)
    const pdfBytes = await doc.save({ useObjectStreams: true })
    zip.file(`page-${index}.pdf`, pdfBytes)
  }
  const zipBlob = await zip.generateAsync({ type: 'blob' })
  outputBlob.value = zipBlob
  outputName.value = 'pdf-pages.zip'
  downloadBlob(zipBlob, outputName.value)
}

// 前端处理：PDF 压缩
async function handlePdfCompress() {
  const bytes = await readFileAsArrayBuffer(file.value)
  const src = await PDFDocument.load(bytes)
  const saved = await src.save({ useObjectStreams: true })
  outputBlob.value = new Blob([saved], { type: 'application/pdf' })
  outputName.value = 'compressed.pdf'
  downloadBlob(outputBlob.value, outputName.value)
}

// 前端处理：PDF 水印
async function handlePdfWatermark() {
  const bytes = await readFileAsArrayBuffer(file.value)
  const src = await PDFDocument.load(bytes)
  const font = await src.embedFont(StandardFonts.Helvetica)
  const pages = src.getPages()
  pages.forEach(page => {
    const { width, height } = page.getSize()
    page.drawText(watermarkText.value || '水印', {
      x: width / 2 - 100,
      y: height / 2,
      size: Number(watermarkFontSize.value) || 36,
      font,
      color: rgb(0.7, 0.7, 0.7),
      rotate: { type: 'degrees', angle: Number(watermarkRotation.value) || 0 },
      opacity: Math.min(1, Math.max(0, Number(watermarkOpacity.value) || 0.2))
    })
  })
  const saved = await src.save({ useObjectStreams: true })
  outputBlob.value = new Blob([saved], { type: 'application/pdf' })
  outputName.value = 'watermarked.pdf'
  downloadBlob(outputBlob.value, outputName.value)
}

// 前端处理：PDF 转图片
async function handlePdfToImages() {
  const bytes = await readFileAsArrayBuffer(file.value)
  const doc = await getDocument({ data: bytes }).promise
  const zip = new JSZip()
  for (let i = 1; i <= doc.numPages; i += 1) {
    const page = await doc.getPage(i)
    const viewport = page.getViewport({ scale: Number(pdfRenderScale.value) || 1.5 })
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')
    canvas.width = viewport.width
    canvas.height = viewport.height
    await page.render({ canvasContext: ctx, viewport }).promise
    const blob = await new Promise(resolve => canvas.toBlob(resolve, `image/${imageFormat.value}`, imageQuality.value))
    zip.file(`page-${i}.${imageFormat.value}`, blob)
  }
  const zipBlob = await zip.generateAsync({ type: 'blob' })
  outputBlob.value = zipBlob
  outputName.value = 'pdf-images.zip'
  downloadBlob(zipBlob, outputName.value)
}

// 前端处理：图片转 PDF
async function handleImagesToPdf() {
  const pdfDoc = await PDFDocument.create()
  for (const imgFile of files.value) {
    const bytes = await readFileAsArrayBuffer(imgFile)
    const isPng = imgFile.type.includes('png')
    const image = isPng ? await pdfDoc.embedPng(bytes) : await pdfDoc.embedJpg(bytes)
    const { width, height } = image
    const page = pdfDoc.addPage([width, height])
    page.drawImage(image, { x: 0, y: 0, width, height })
  }
  const saved = await pdfDoc.save({ useObjectStreams: true })
  outputBlob.value = new Blob([saved], { type: 'application/pdf' })
  outputName.value = 'images.pdf'
  downloadBlob(outputBlob.value, outputName.value)
}

// 解析 PDF 文本（OCR 关闭时使用）
async function extractPdfText(bytes) {
  const doc = await getDocument({ data: bytes }).promise
  const lines = []
  for (let i = 1; i <= doc.numPages; i += 1) {
    const page = await doc.getPage(i)
    const content = await page.getTextContent()
    const items = content.items
      .filter(item => item.str && item.str.trim())
      .map(item => ({
        text: item.str,
        x: item.transform?.[4] ?? 0,
        y: item.transform?.[5] ?? 0,
        width: item.width ?? 0
      }))
      .sort((a, b) => (b.y - a.y) || (a.x - b.x))
    const grouped = []
    let current = []
    let currentY = null
    const tolerance = 2
    for (const item of items) {
      if (currentY === null || Math.abs(item.y - currentY) <= tolerance) {
        current.push(item)
        if (currentY === null) currentY = item.y
      } else {
        grouped.push(current)
        current = [item]
        currentY = item.y
      }
    }
    if (current.length) grouped.push(current)
    for (const lineItems of grouped) {
      lineItems.sort((a, b) => a.x - b.x)
      let line = ''
      let lastX = null
      let lastWidth = 0
      for (const li of lineItems) {
        const text = li.text || ''
        if (!text) continue
        if (lastX !== null) {
          const gap = li.x - (lastX + lastWidth)
          const avgCharWidth = text.length ? (li.width / text.length) : 0
          const threshold = Math.max(2, avgCharWidth * 0.5)
          if (gap > threshold) line += ' '
        }
        line += text
        lastX = li.x
        lastWidth = li.width || (text.length ? (li.width || 0) : 0)
      }
      if (line.trim()) lines.push(line.trimEnd())
    }
    if (i < doc.numPages) lines.push('')
  }
  return lines.join('\n').trim()
}

// 前端处理：PDF 转 Word
async function handlePdfToWord() {
  const bytes = await readFileAsArrayBuffer(file.value)
  const text = await extractPdfText(bytes)
  const paragraphs = text.split('\n').map(line => new Paragraph(line))
  const doc = new Document({ sections: [{ children: paragraphs }] })
  const blob = await Packer.toBlob(doc)
  outputBlob.value = blob
  outputName.value = 'pdf.docx'
  downloadBlob(blob, outputName.value)
}

// 前端处理：Word 转 PDF
async function handleWordToPdf() {
  const arrayBuffer = await readFileAsArrayBuffer(file.value)
  const result = await mammoth.extractRawText({ arrayBuffer })
  const content = result.value || ''
  const pdf = new jsPDF()
  const lines = pdf.splitTextToSize(content, 180)
  pdf.text(lines, 10, 10)
  const blob = pdf.output('blob')
  outputBlob.value = blob
  outputName.value = 'document.pdf'
  downloadBlob(blob, outputName.value)
}

// 前端处理：Excel 转 PDF
async function handleExcelToPdf() {
  const arrayBuffer = await readFileAsArrayBuffer(file.value)
  const workbook = XLSX.read(arrayBuffer, { type: 'array' })
  const sheet = workbook.Sheets[workbook.SheetNames[0]]
  const data = XLSX.utils.sheet_to_json(sheet, { header: 1 })
  const pdf = new jsPDF()
  let y = 10
  data.forEach((row, idx) => {
    const line = row.map(cell => String(cell ?? '')).join(' | ')
    const lines = pdf.splitTextToSize(line, 180)
    lines.forEach(textLine => {
      if (y > 280) {
        pdf.addPage()
        y = 10
      }
      pdf.text(textLine, 10, y)
      y += 8
    })
    if (idx < data.length - 1) y += 2
  })
  const blob = pdf.output('blob')
  outputBlob.value = blob
  outputName.value = 'excel.pdf'
  downloadBlob(blob, outputName.value)
}

// 前端处理：PPT 转 PDF
async function handlePptToPdf() {
  const arrayBuffer = await readFileAsArrayBuffer(file.value)
  const zip = await JSZip.loadAsync(arrayBuffer)
  const slideFiles = Object.keys(zip.files)
    .filter(name => name.startsWith('ppt/slides/slide') && name.endsWith('.xml'))
    .sort((a, b) => {
      const getNum = name => Number(name.match(/slide(\d+)\.xml/)?.[1] || 0)
      return getNum(a) - getNum(b)
    })
  const pdf = new jsPDF()
  for (let i = 0; i < slideFiles.length; i += 1) {
    if (i > 0) pdf.addPage()
    const xml = await zip.files[slideFiles[i]].async('string')
    const doc = new DOMParser().parseFromString(xml, 'application/xml')
    const nodes = Array.from(doc.getElementsByTagName('a:t'))
    const lines = nodes.map(node => node.textContent || '').filter(Boolean)
    let y = 10
    lines.forEach(line => {
      const parts = pdf.splitTextToSize(line, 180)
      parts.forEach(part => {
        if (y > 280) {
          pdf.addPage()
          y = 10
        }
        pdf.text(part, 10, y)
        y += 8
      })
    })
  }
  const blob = pdf.output('blob')
  outputBlob.value = blob
  outputName.value = 'slides.pdf'
  downloadBlob(blob, outputName.value)
}

// 前端处理：PDF OCR
async function handlePdfOcr() {
  const bytes = await readFileAsArrayBuffer(file.value)
  if (!enableOcr.value) {
    outputText.value = await extractPdfText(bytes)
    return
  }
  const worker = await createWorker()
  await worker.loadLanguage(ocrLang.value)
  await worker.initialize(ocrLang.value)
  const doc = await getDocument({ data: bytes }).promise
  let text = ''
  for (let i = 1; i <= doc.numPages; i += 1) {
    const page = await doc.getPage(i)
    const viewport = page.getViewport({ scale: 2 })
    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')
    canvas.width = viewport.width
    canvas.height = viewport.height
    await page.render({ canvasContext: ctx, viewport }).promise
    const { data } = await worker.recognize(canvas)
    text += `${data.text}\n`
  }
  await worker.terminate()
  outputText.value = text.trim()
}

// 后端文件工具调用区
async function handleZipCompress() {
  if (!files.value.length) throw new Error('请先选择文件')
  await postBlob('/api/tools/file/zip/compress', buildMultiFormData(), 'files.zip')
}

async function handleZipExtract() {
  await postBlob('/api/tools/file/zip/extract', buildSingleFormData(), 'extracted.zip')
}

async function handleTextSplit() {
  const fd = buildSingleFormData()
  fd.append('mode', textSplitMode.value)
  fd.append('size', String(textSplitSize.value))
  await postBlob('/api/tools/file/text/split', fd, 'text-parts.zip')
}

async function handleFileStamp() {
  const fd = buildSingleFormData()
  fd.append('text', watermarkText.value || '盖章')
  fd.append('fontSize', String(watermarkFontSize.value))
  fd.append('opacity', String(watermarkOpacity.value))
  fd.append('rotation', String(watermarkRotation.value))
  await postBlob('/api/tools/pdf/stamp', fd, 'stamp.pdf')
}

async function handlePdfPageNumber() {
  const fd = buildSingleFormData()
  fd.append('start', String(pdfPageStart.value))
  fd.append('fontSize', String(pdfPageFontSize.value))
  await postBlob('/api/tools/pdf/page-number', fd, 'page-number.pdf')
}

async function handlePdfEdit() {
  const fd = buildSingleFormData()
  fd.append('pages', splitPages.value || '')
  await postBlob('/api/tools/pdf/edit', fd, 'edited.pdf')
}

async function handlePdfEncrypt() {
  const fd = buildSingleFormData()
  fd.append('userPassword', pdfPassword.value || '')
  if (pdfOwnerPassword.value) fd.append('ownerPassword', pdfOwnerPassword.value)
  await postBlob('/api/tools/pdf/encrypt', fd, 'encrypted.pdf')
}

async function handlePdfDecrypt() {
  const fd = buildSingleFormData()
  fd.append('password', pdfPassword.value || '')
  await postBlob('/api/tools/pdf/decrypt', fd, 'decrypted.pdf')
}

async function handlePdfSign() {
  const fd = buildSingleFormData()
  if (pdfSignName.value) fd.append('signer', pdfSignName.value)
  if (pdfSignReason.value) fd.append('reason', pdfSignReason.value)
  if (pdfSignLocation.value) fd.append('location', pdfSignLocation.value)
  await postBlob('/api/tools/pdf/sign', fd, 'signed.pdf')
}

async function handlePdfMetadata() {
  const hasUpdate = [pdfMetaTitle.value, pdfMetaAuthor.value, pdfMetaSubject.value, pdfMetaKeywords.value]
    .some(v => v && String(v).trim())
  if (!hasUpdate) {
    const fd = buildSingleFormData()
    if (pdfPassword.value) fd.append('password', pdfPassword.value)
    const { data } = await http.post('/api/tools/pdf/metadata/read', fd)
    if (data?.success) {
      outputText.value = JSON.stringify(data.data, null, 2)
    } else {
      throw new Error(data?.error?.message || '读取失败')
    }
    return
  }
  const fd = buildSingleFormData()
  if (pdfPassword.value) fd.append('password', pdfPassword.value)
  if (pdfMetaTitle.value) fd.append('title', pdfMetaTitle.value)
  if (pdfMetaAuthor.value) fd.append('author', pdfMetaAuthor.value)
  if (pdfMetaSubject.value) fd.append('subject', pdfMetaSubject.value)
  if (pdfMetaKeywords.value) fd.append('keywords', pdfMetaKeywords.value)
  await postBlob('/api/tools/pdf/metadata/update', fd, 'metadata.pdf')
}

async function handleBatchRename() {
  const fd = buildMultiFormData()
  fd.append('prefix', batchRenamePrefix.value || '')
  fd.append('start', String(batchRenameStart.value))
  fd.append('suffix', batchRenameSuffix.value || '')
  fd.append('keepExt', String(batchRenameKeepExt.value))
  await postBlob('/api/tools/file/batch-rename', fd, 'renamed.zip')
}

async function handleFileMerge() {
  await postBlob('/api/tools/file/merge', buildMultiFormData(), 'merged.bin')
}

async function handleFileSplit() {
  const fd = buildSingleFormData()
  fd.append('sizeKb', String(fileSplitSize.value))
  await postBlob('/api/tools/file/split', fd, 'file-parts.zip')
}

async function handleDataProcess() {
  if (!dataProcessColumn.value) throw new Error('请输入列名或列序号')
  const fd = buildSingleFormData()
  fd.append('column', dataProcessColumn.value)
  if (dataProcessOp.value) fd.append('op', dataProcessOp.value)
  await postBlob('/api/tools/file/data/process', fd, 'processed.csv')
}

async function handleSubtitleConvert() {
  const fd = buildSingleFormData()
  fd.append('target', subtitleTarget.value || 'srt')
  const name = subtitleTarget.value === 'vtt' ? 'subtitle.vtt' : 'subtitle.srt'
  await postBlob('/api/tools/file/subtitle/convert', fd, name)
}

async function handleEncodingConvert() {
  const fd = buildSingleFormData()
  if (targetEncoding.value) fd.append('target', targetEncoding.value)
  const res = await http.post('/api/tools/file/encoding/convert', fd, { responseType: 'blob' })
  const blob = res?.data instanceof Blob ? res.data : new Blob([res.data])
  outputBlob.value = blob
  outputName.value = 'converted.txt'
  const detected = res?.headers?.['x-detected-encoding']
  const target = res?.headers?.['x-target-encoding']
  if (detected || target) {
    outputText.value = `检测编码: ${detected || '-'}，目标编码: ${target || '-'}`
  }
  downloadBlob(blob, outputName.value)
}

async function handleFileTypeDetect() {
  const { data } = await http.post('/api/tools/file/type-detect', buildSingleFormData())
  if (data?.success) {
    outputText.value = JSON.stringify(data.data, null, 2)
  } else {
    throw new Error(data?.error?.message || '检测失败')
  }
}

// 前端处理：图片处理统一入口
async function handleImageTool() {
  const img = await loadImageFromFile(file.value)
  let targetWidth = img.width
  let targetHeight = img.height
  if (props.mode === 'image-resize') {
    targetWidth = Number(resizeWidth.value) || img.width
    targetHeight = Number(resizeHeight.value) || img.height
    if (keepAspect.value) {
      const ratio = img.width / img.height
      if (targetWidth) {
        targetHeight = Math.round(targetWidth / ratio)
      } else if (targetHeight) {
        targetWidth = Math.round(targetHeight * ratio)
      }
    }
  }
  if (props.mode === 'image-crop') {
    targetWidth = Number(cropWidth.value) || img.width
    targetHeight = Number(cropHeight.value) || img.height
  }
  const canvas = document.createElement('canvas')
  canvas.width = targetWidth
  canvas.height = targetHeight
  const ctx = canvas.getContext('2d')
  if (props.mode === 'image-crop') {
    ctx.drawImage(img, Number(cropX.value) || 0, Number(cropY.value) || 0, targetWidth, targetHeight, 0, 0, targetWidth, targetHeight)
  } else {
    ctx.drawImage(img, 0, 0, targetWidth, targetHeight)
  }
  const format = props.mode === 'image-format-convert' || props.mode === 'image-compress' ? imageOutputFormat.value : file.value.type
  const blob = await new Promise(resolve => canvas.toBlob(resolve, format, imageQuality.value))
  outputBlob.value = blob
  outputUrl.value = URL.createObjectURL(blob)
  const ext = format.includes('png') ? 'png' : format.includes('webp') ? 'webp' : 'jpg'
  outputName.value = `image.${ext}`
}

// 前端处理：生成图标包
async function handleImageToIcon() {
  const img = await loadImageFromFile(file.value)
  const sizes = iconSizes.value.split(',').map(v => Number(v.trim())).filter(v => Number.isFinite(v) && v > 0)
  const zip = new JSZip()
  for (const size of sizes) {
    const canvas = document.createElement('canvas')
    canvas.width = size
    canvas.height = size
    const ctx = canvas.getContext('2d')
    ctx.drawImage(img, 0, 0, size, size)
    const blob = await new Promise(resolve => canvas.toBlob(resolve, 'image/png'))
    zip.file(`icon-${size}.png`, blob)
  }
  const zipBlob = await zip.generateAsync({ type: 'blob' })
  outputBlob.value = zipBlob
  outputName.value = 'icons.zip'
  downloadBlob(zipBlob, outputName.value)
}

// 解析 CSV（简单实现，兼容双引号）
function parseCsv(text) {
  const rows = []
  let row = []
  let current = ''
  let inQuotes = false
  for (let i = 0; i < text.length; i += 1) {
    const char = text[i]
    const next = text[i + 1]
    if (char === '"' && next === '"') {
      current += '"'
      i += 1
      continue
    }
    if (char === '"') {
      inQuotes = !inQuotes
      continue
    }
    if (char === ',' && !inQuotes) {
      row.push(current)
      current = ''
      continue
    }
    if ((char === '\n' || char === '\r') && !inQuotes) {
      if (current || row.length) {
        row.push(current)
        rows.push(row)
      }
      row = []
      current = ''
      continue
    }
    current += char
  }
  if (current || row.length) {
    row.push(current)
    rows.push(row)
  }
  return rows
}

// CSV 转 JSON
function csvToJson(text) {
  const rows = parseCsv(text.trim())
  if (!rows.length) return []
  const headers = rows[0]
  return rows.slice(1).map(row => {
    const obj = {}
    headers.forEach((h, idx) => {
      obj[h] = row[idx] ?? ''
    })
    return obj
  })
}

// JSON 转 CSV
function jsonToCsv(text) {
  const data = JSON.parse(text)
  if (!Array.isArray(data)) throw new Error('JSON 必须是数组')
  const headers = Array.from(new Set(data.flatMap(row => Object.keys(row || {}))))
  const lines = [headers.join(',')]
  data.forEach(row => {
    const line = headers.map(key => {
      const raw = row?.[key] ?? ''
      const cell = String(raw).replace(/"/g, '""')
      return `"${cell}"`
    }).join(',')
    lines.push(line)
  })
  return lines.join('\n')
}

// 按需加载 FFmpeg
async function loadFfmpeg() {
  if (ffmpegReady.value || ffmpegLoading.value) return
  ffmpegLoading.value = true
  const ffmpeg = new FFmpeg()
  await ffmpeg.load({
    coreURL: 'https://unpkg.com/@ffmpeg/core@0.12.6/dist/ffmpeg-core.js',
    wasmURL: 'https://unpkg.com/@ffmpeg/core@0.12.6/dist/ffmpeg-core.wasm'
  })
  ffmpegRef.value = ffmpeg
  ffmpegReady.value = true
  ffmpegLoading.value = false
}

// FFmpeg 加载状态
const ffmpegStatus = computed(() => {
  if (ffmpegLoading.value) return '加载中...'
  if (ffmpegReady.value) return '已就绪'
  return '未加载'
})

// FFmpeg 通用执行
async function runFfmpeg(args, outputName, mime) {
  if (!ffmpegReady.value) {
    ElMessage.warning('请先加载引擎')
    return
  }
  const ffmpeg = ffmpegRef.value
  const inputName = `input-${Date.now()}`
  await ffmpeg.writeFile(inputName, await fetchFile(file.value))
  await ffmpeg.exec(['-i', inputName, ...args, outputName])
  const data = await ffmpeg.readFile(outputName)
  await ffmpeg.deleteFile(inputName)
  await ffmpeg.deleteFile(outputName)
  const blob = new Blob([data.buffer], { type: mime })
  outputBlob.value = blob
  outputName.value = outputName
  downloadBlob(blob, outputName.value)
}

// 前端处理：音视频工具入口
async function handleMediaTool() {
  if (props.mode === 'audio-converter') {
    const outputName = `audio.${audioFormat.value}`
    await runFfmpeg(['-b:a', `${audioBitrate.value}k`], outputName, `audio/${audioFormat.value}`)
    return
  }
  if (props.mode === 'video-compress') {
    const outputName = 'compressed.mp4'
    const args = ['-c:v', 'libx264', '-crf', String(videoCrf.value)]
    if (videoWidth.value) args.push('-vf', `scale=${videoWidth.value}:-2`)
    if (videoFps.value) args.push('-r', String(videoFps.value))
    await runFfmpeg(args, outputName, 'video/mp4')
    return
  }
  if (props.mode === 'video-to-gif') {
    const outputName = 'output.gif'
    const args = ['-vf', `fps=${gifFps.value},scale=${gifWidth.value}:-1:flags=lanczos`]
    await runFfmpeg(args, outputName, 'image/gif')
    return
  }
  if (props.mode === 'audio-extract') {
    const outputName = `extract.${extractFormat.value}`
    await runFfmpeg(['-vn'], outputName, `audio/${extractFormat.value}`)
    return
  }
  if (props.mode === 'video-to-mp3') {
    const outputName = 'audio.mp3'
    await runFfmpeg(['-vn', '-b:a', '192k'], outputName, 'audio/mp3')
  }
}

// 前端处理：文本/数据工具入口
async function handleDataTool() {
  let sourceText = textInput.value
  if (!sourceText && file.value) {
    sourceText = await file.value.text()
  }
  if (props.mode === 'csv-to-json') {
    const json = csvToJson(sourceText || '')
    outputText.value = JSON.stringify(json, null, 2)
    return
  }
  if (props.mode === 'json-to-csv') {
    outputText.value = jsonToCsv(sourceText || '')
    return
  }
  if (props.mode === 'sql-formatter') {
    outputText.value = formatSql(sourceText || '')
  }
}

// 根据 mode 分发处理逻辑
async function runTool() {
  if (busy.value) return
  busy.value = true
  outputText.value = ''
  outputUrl.value = ''
  outputBlob.value = null
  outputName.value = ''
  try {
    if (['pdf-merge', 'pdf-split', 'pdf-compress', 'pdf-watermark', 'pdf-to-images', 'images-to-pdf', 'pdf-to-word', 'word-to-pdf', 'pdf-ocr', 'excel-to-pdf', 'ppt-to-pdf'].includes(props.mode)) {
      if (!file.value && props.mode !== 'pdf-merge' && props.mode !== 'images-to-pdf') throw new Error('请先选择文件')
      if (props.mode === 'pdf-merge') await handlePdfMerge()
      else if (props.mode === 'pdf-split') await handlePdfSplit()
      else if (props.mode === 'pdf-compress') await handlePdfCompress()
      else if (props.mode === 'pdf-watermark') await handlePdfWatermark()
      else if (props.mode === 'pdf-to-images') await handlePdfToImages()
      else if (props.mode === 'images-to-pdf') await handleImagesToPdf()
      else if (props.mode === 'pdf-to-word') await handlePdfToWord()
      else if (props.mode === 'word-to-pdf') await handleWordToPdf()
      else if (props.mode === 'pdf-ocr') await handlePdfOcr()
      else if (props.mode === 'excel-to-pdf') await handleExcelToPdf()
      else if (props.mode === 'ppt-to-pdf') await handlePptToPdf()
    } else if (props.mode === 'zip-compress') {
      await handleZipCompress()
    } else if (props.mode === 'zip-extract') {
      await handleZipExtract()
    } else if (props.mode === 'text-split') {
      await handleTextSplit()
    } else if (props.mode === 'file-stamp') {
      await handleFileStamp()
    } else if (props.mode === 'pdf-page-number') {
      await handlePdfPageNumber()
    } else if (props.mode === 'pdf-edit') {
      await handlePdfEdit()
    } else if (props.mode === 'pdf-encrypt') {
      await handlePdfEncrypt()
    } else if (props.mode === 'pdf-decrypt') {
      await handlePdfDecrypt()
    } else if (props.mode === 'pdf-sign') {
      await handlePdfSign()
    } else if (props.mode === 'pdf-metadata') {
      await handlePdfMetadata()
    } else if (props.mode === 'batch-rename') {
      await handleBatchRename()
    } else if (props.mode === 'file-merge') {
      await handleFileMerge()
    } else if (props.mode === 'file-split') {
      await handleFileSplit()
    } else if (props.mode === 'data-process') {
      await handleDataProcess()
    } else if (props.mode === 'subtitle-convert') {
      await handleSubtitleConvert()
    } else if (props.mode === 'encoding-convert') {
      await handleEncodingConvert()
    } else if (props.mode === 'file-type-detect') {
      await handleFileTypeDetect()
    } else if (['image-compress', 'image-resize', 'image-crop', 'image-format-convert'].includes(props.mode)) {
      await handleImageTool()
    } else if (props.mode === 'image-to-icon') {
      await handleImageToIcon()
    } else if (['csv-to-json', 'json-to-csv', 'sql-formatter'].includes(props.mode)) {
      await handleDataTool()
    } else if (isMediaMode.value) {
      await handleMediaTool()
    }
  } catch (err) {
    ElMessage.error(err?.message || '处理失败')
  } finally {
    busy.value = false
  }
}
</script>
