/*
 * @generated-file-note
 * 文件：smart-tools-frontend/src/tools/registry.js
 * 用途：前端 JavaScript 源码文件
 * 归属：前端 tools
 * 依赖：vue、@/components/ChartToolBase.vue、@/components/MermaidEditorTool.vue、@/components/WordCloudTool.vue、@/components/GenericFileTool.vue
 * 交互：运行在浏览器端，注意输入校验、异常兜底与性能影响
 * 安全：避免把敏感数据写入 localStorage/URL/日志；避免 XSS 注入点
 */
/**
 * 工具注册中心（前端插件式架构）
 *
 * 设计目标：
 * 1) 每个工具独立目录，便于团队并行开发/按需扩展
 * 2) 通过约定（meta.js + index.vue）实现“零配置”接入
 * 3) 列表渲染、搜索、路由跳转完全依赖 meta 信息
 */
import { defineComponent, h, markRaw } from 'vue'
import ChartToolBase from '@/components/ChartToolBase.vue'
import MermaidEditorTool from '@/components/MermaidEditorTool.vue'
import WordCloudTool from '@/components/WordCloudTool.vue'
import GenericFileTool from '@/components/GenericFileTool.vue'

const metaContext = require.context('./', true, /meta\.js$/)
const viewContext = require.context('./', true, /index\.vue$/)

// 通用文件工具：用于未配置专用页面的文件类工具
const placeholderView = markRaw(GenericFileTool)

// 创建图表工具组件：统一挂载到 ChartToolBase
function createChartToolComponent(meta, chartConfig) {
  return markRaw(defineComponent({
    name: `ChartTool_${meta.slug}`,
    setup() {
      return () => h(ChartToolBase, {
        title: meta.name,
        description: meta.description,
        chartType: chartConfig.chartType,
        mode: chartConfig.mode,
        sample: chartConfig.sample
      })
    }
  }))
}

function createFileToolComponent(meta) {
  return markRaw(defineComponent({
    name: `FileTool_${meta.slug}`,
    setup() {
      return () => h(GenericFileTool, {
        title: meta.name,
        description: meta.description,
        mode: meta.slug
      })
    }
  }))
}

const chartTools = [
  {
    slug: 'standard-line-chart',
    name: '在线制作标准折线图',
    category: '图表',
    description: '使用表格数据生成标准折线图并导出 PNG',
    keywords: ['chart', 'line', '折线', '图表'],
    chart: {
      chartType: 'line',
      mode: 'excel',
      sample: {
        tableRows: [
          { label: '一月', value: 120 },
          { label: '二月', value: 200 },
          { label: '三月', value: 150 },
          { label: '四月', value: 80 }
        ]
      }
    }
  },
  {
    slug: 'standard-pie-chart',
    name: '在线制作标准饼图',
    category: '图表',
    description: '使用表格数据生成饼图并支持导出',
    keywords: ['chart', 'pie', '饼图'],
    chart: {
      chartType: 'pie',
      mode: 'excel',
      sample: {
        tableRows: [
          { label: '访问', value: 45 },
          { label: '注册', value: 30 },
          { label: '转化', value: 25 }
        ]
      }
    }
  },
  {
    slug: 'tree-chart',
    name: '在线制作树形图',
    category: '图表',
    description: '使用表格数据生成树形结构图',
    keywords: ['chart', 'tree', '树形图'],
    chart: {
      chartType: 'tree',
      mode: 'excel',
      sample: {
        jsonText: JSON.stringify({
          name: '产品',
          children: [
            { name: '设计', value: 60 },
            { name: '研发', value: 120 },
            { name: '运营', value: 40 }
          ]
        }, null, 2)
      }
    }
  },
  {
    slug: 'standard-bar-chart',
    name: '在线制作标准柱状图',
    category: '图表',
    description: '使用表格数据生成柱状图',
    keywords: ['chart', 'bar', '柱状图'],
    chart: {
      chartType: 'bar',
      mode: 'excel',
      sample: {
        tableRows: [
          { label: '一季度', value: 320 },
          { label: '二季度', value: 280 },
          { label: '三季度', value: 400 },
          { label: '四季度', value: 360 }
        ]
      }
    }
  },
  {
    slug: 'sankey-chart',
    name: '在线制作桑基图',
    category: '图表',
    description: '通过节点与流向数据生成桑基图',
    keywords: ['chart', 'sankey', '桑基图'],
    chart: {
      chartType: 'sankey',
      mode: 'json',
      sample: {
        jsonText: JSON.stringify({
          nodes: [{ name: '访问' }, { name: '注册' }, { name: '转化' }, { name: '留存' }],
          links: [
            { source: '访问', target: '注册', value: 120 },
            { source: '注册', target: '转化', value: 60 },
            { source: '转化', target: '留存', value: 40 }
          ]
        }, null, 2)
      }
    }
  },
  {
    slug: 'radar-chart',
    name: '在线制作雷达图',
    category: '图表',
    description: '使用表格数据生成雷达图',
    keywords: ['chart', 'radar', '雷达图'],
    chart: {
      chartType: 'radar',
      mode: 'excel',
      sample: {
        radarRows: [
          { name: '质量', max: 100, value: 85 },
          { name: '效率', max: 100, value: 78 },
          { name: '成本', max: 100, value: 62 },
          { name: '满意度', max: 100, value: 90 }
        ]
      }
    }
  },
  {
    slug: 'sunburst-chart',
    name: '在线制作旭日图',
    category: '图表',
    description: '使用表格层级数据生成旭日图',
    keywords: ['chart', 'sunburst', '旭日图'],
    chart: {
      chartType: 'sunburst',
      mode: 'excel',
      sample: {
        jsonText: JSON.stringify({
          name: '销售',
          children: [
            { name: '线上', children: [{ name: '电商', value: 120 }, { name: '小程序', value: 80 }] },
            { name: '线下', children: [{ name: '门店', value: 90 }, { name: '渠道', value: 70 }] }
          ]
        }, null, 2)
      }
    }
  },
  {
    slug: 'scatter-chart',
    name: '在线制作散点图',
    category: '图表',
    description: '使用表格数据生成散点图',
    keywords: ['chart', 'scatter', '散点图'],
    chart: {
      chartType: 'scatter',
      mode: 'excel',
      sample: {
        xyRows: [
          { x: 5, y: 20 },
          { x: 10, y: 36 },
          { x: 15, y: 10 },
          { x: 20, y: 28 },
          { x: 25, y: 32 }
        ]
      }
    }
  },
  {
    slug: 'rose-chart',
    name: '在线制作南丁格尔玫瑰图',
    category: '图表',
    description: '使用表格数据生成玫瑰图',
    keywords: ['chart', 'rose', '玫瑰图'],
    chart: {
      chartType: 'rose',
      mode: 'excel',
      sample: {
        tableRows: [
          { label: 'A', value: 40 },
          { label: 'B', value: 32 },
          { label: 'C', value: 28 },
          { label: 'D', value: 20 },
          { label: 'E', value: 18 }
        ]
      }
    }
  },
  {
    slug: 'donut-chart',
    name: '在线制作环形图',
    category: '图表',
    description: '使用表格数据生成环形图',
    keywords: ['chart', 'donut', '环形图'],
    chart: {
      chartType: 'donut',
      mode: 'excel',
      sample: {
        tableRows: [
          { label: '移动端', value: 55 },
          { label: '桌面端', value: 35 },
          { label: '其他', value: 10 }
        ]
      }
    }
  },
  {
    slug: 'funnel-chart',
    name: '在线制作漏斗图',
    category: '图表',
    description: '使用表格数据生成漏斗图',
    keywords: ['chart', 'funnel', '漏斗图'],
    chart: {
      chartType: 'funnel',
      mode: 'excel',
      sample: {
        tableRows: [
          { label: '访问', value: 1200 },
          { label: '浏览', value: 800 },
          { label: '下单', value: 320 },
          { label: '成交', value: 180 }
        ]
      }
    }
  },
  {
    slug: 'gauge-chart',
    name: '在线制作仪表盘图',
    category: '图表',
    description: '设置指标数值生成仪表盘图',
    keywords: ['chart', 'gauge', '仪表盘'],
    chart: {
      chartType: 'gauge',
      mode: 'single',
      sample: { singleValue: 72 }
    }
  },
  {
    slug: 'formula-chart',
    name: '在线数学公式绘制图表',
    category: '图表',
    description: '输入函数表达式生成曲线图',
    keywords: ['chart', 'formula', 'function', '数学公式'],
    chart: {
      chartType: 'function',
      mode: 'function',
      sample: { expression: 'Math.sin(x)', xMin: -6.28, xMax: 6.28, step: 0.1 }
    }
  },
  {
    slug: 'word-cloud-chart',
    name: '在线制作词云图',
    category: '图表',
    description: '输入词语与权重生成词云图',
    keywords: ['chart', 'wordcloud', '词云'],
    component: markRaw(defineComponent({
      name: 'WordCloudWrapper',
      setup() {
        return () => h(WordCloudTool, {
          title: '在线制作词云图',
          description: '输入词语与权重生成词云图',
          sample: {
            wordRows: [
              { word: '智能', weight: 120 },
              { word: '分析', weight: 90 },
              { word: '增长', weight: 70 },
              { word: '用户', weight: 60 },
              { word: '转化', weight: 50 }
            ]
          }
        })
      }
    }))
  },
  {
    slug: 'relationship-graph',
    name: '在线生成人物关系图',
    category: '图表',
    description: '通过节点与关系数据生成关系图',
    keywords: ['chart', 'graph', '关系图'],
    chart: {
      chartType: 'graph',
      mode: 'json',
      sample: {
        jsonText: JSON.stringify({
          nodes: [
            { id: 'A', name: '张三', value: 10 },
            { id: 'B', name: '李四', value: 8 },
            { id: 'C', name: '王五', value: 6 }
          ],
          links: [
            { source: 'A', target: 'B' },
            { source: 'A', target: 'C' }
          ]
        }, null, 2)
      }
    }
  },
  {
    slug: 'mermaid-editor',
    name: '在线 Mermaid 图表编辑器',
    category: '图表',
    description: '在线编辑 Mermaid 语法并生成图表',
    keywords: ['chart', 'mermaid', 'diagram', '流程图'],
    component: markRaw(defineComponent({
      name: 'MermaidEditorWrapper',
      setup() {
        return () => h(MermaidEditorTool, {
          title: '在线 Mermaid 图表编辑器',
          description: '在线编辑 Mermaid 语法并生成图表',
          defaultCode: 'sequenceDiagram\n  participant A as 用户\n  participant B as 系统\n  A->>B: 请求\n  B-->>A: 响应'
        })
      }
    }))
  }
]

const extraTools = [
  {
    slug: 'pdf-to-word',
    name: 'PDF 转 Word',
    category: '文档处理',
    description: '将 PDF 转换为可编辑的 Word 文档',
    keywords: ['pdf', 'word', 'docx', 'convert']
  },
  {
    slug: 'word-to-pdf',
    name: 'Word 转 PDF',
    category: '文档处理',
    description: '将 Word 文档转换为 PDF 文件',
    keywords: ['word', 'pdf', 'docx', 'convert']
  },
  {
    slug: 'pdf-merge',
    name: 'PDF 合并',
    category: '文档处理',
    description: '合并多个 PDF 为单一文件',
    keywords: ['pdf', 'merge', 'combine']
  },
  {
    slug: 'pdf-split',
    name: 'PDF 拆分',
    category: '文档处理',
    description: '按页拆分 PDF，快速输出单页文件',
    keywords: ['pdf', 'split', 'pages']
  },
  {
    slug: 'pdf-compress',
    name: 'PDF 压缩',
    category: '文档处理',
    description: '压缩 PDF 体积，便于传输分享',
    keywords: ['pdf', 'compress', 'optimize']
  },
  {
    slug: 'pdf-watermark',
    name: 'PDF 水印',
    category: '文档处理',
    description: '为 PDF 添加文字或图片水印',
    keywords: ['pdf', 'watermark']
  },
  {
    slug: 'pdf-to-images',
    name: 'PDF 转图片',
    category: '文档处理',
    description: '将 PDF 页面转换为图片',
    keywords: ['pdf', 'image', 'png', 'jpg']
  },
  {
    slug: 'images-to-pdf',
    name: '图片转 PDF',
    category: '文档处理',
    description: '将多张图片合成为 PDF',
    keywords: ['image', 'pdf', 'convert']
  },
  {
    slug: 'pdf-ocr',
    name: 'PDF OCR 识别',
    category: '文档处理',
    description: '识别 PDF 扫描件中的文字内容',
    keywords: ['pdf', 'ocr', 'text']
  },
  {
    slug: 'excel-to-pdf',
    name: 'Excel 转 PDF',
    category: '文档处理',
    description: '将 Excel 表格导出为 PDF',
    keywords: ['excel', 'pdf', 'xlsx']
  },
  {
    slug: 'ppt-to-pdf',
    name: 'PPT 转 PDF',
    category: '文档处理',
    description: '将 PPT 演示文稿导出为 PDF',
    keywords: ['ppt', 'pdf', 'slides']
  },
  {
    slug: 'zip-compress',
    name: '在线 ZIP 文件压缩',
    category: '文件',
    description: '将多个文件压缩为 ZIP',
    keywords: ['zip', 'compress', '文件']
  },
  {
    slug: 'zip-extract',
    name: '在线 ZIP 解压',
    category: '文件',
    description: '解压 ZIP 并输出文件包',
    keywords: ['zip', 'extract', '解压']
  },
  {
    slug: 'text-split',
    name: '在线文本文件拆分',
    category: '文件',
    description: '按行数或字节拆分文本文件',
    keywords: ['text', 'split', '文本']
  },
  {
    slug: 'file-stamp',
    name: '在线文件盖章',
    category: '文件',
    description: '为 PDF 添加盖章文字',
    keywords: ['stamp', 'pdf', '文件']
  },
  {
    slug: 'pdf-page-number',
    name: '在线 PDF 添加页码',
    category: '文件',
    description: '为 PDF 批量添加页码',
    keywords: ['pdf', 'page', 'number']
  },
  {
    slug: 'pdf-edit',
    name: '在线 PDF 编辑',
    category: '文件',
    description: '按页码范围保留页面',
    keywords: ['pdf', 'edit', 'pages']
  },
  {
    slug: 'pdf-encrypt',
    name: '在线 PDF 加密',
    category: '文件',
    description: '为 PDF 设置打开密码',
    keywords: ['pdf', 'encrypt', 'password']
  },
  {
    slug: 'pdf-decrypt',
    name: '在线 PDF 解密',
    category: '文件',
    description: '解除 PDF 打开密码',
    keywords: ['pdf', 'decrypt', 'password']
  },
  {
    slug: 'pdf-sign',
    name: '在线 PDF 签名',
    category: '文件',
    description: '为 PDF 添加签名文字',
    keywords: ['pdf', 'sign', '签名']
  },
  {
    slug: 'pdf-metadata',
    name: '在线修改 PDF 元数据',
    category: '文件',
    description: '查看或更新 PDF 元数据',
    keywords: ['pdf', 'metadata', '元数据']
  },
  {
    slug: 'batch-rename',
    name: '在线批量文件重命名',
    category: '文件',
    description: '按规则批量重命名文件',
    keywords: ['rename', 'batch', '文件']
  },
  {
    slug: 'file-merge',
    name: '在线文件合并',
    category: '文件',
    description: '按顺序合并多个文件',
    keywords: ['merge', '文件']
  },
  {
    slug: 'file-split',
    name: '在线文件拆分',
    category: '文件',
    description: '按大小拆分文件',
    keywords: ['split', '文件']
  },
  {
    slug: 'data-process',
    name: '在线数据处理',
    category: '文件',
    description: '对 CSV 指定列执行统计',
    keywords: ['csv', 'data', '统计']
  },
  {
    slug: 'subtitle-convert',
    name: '在线字幕格式转换',
    category: '文件',
    description: 'SRT/VTT 字幕互转',
    keywords: ['subtitle', 'srt', 'vtt']
  },
  {
    slug: 'encoding-convert',
    name: '在线文件编码检测和转换',
    category: '文件',
    description: '检测并转换文件编码',
    keywords: ['encoding', 'convert', '文件']
  },
  {
    slug: 'file-type-detect',
    name: '在线文件类型检测',
    category: '文件',
    description: '识别文件类型与 MIME',
    keywords: ['file', 'type', 'detect']
  },
  {
    slug: 'image-compress',
    name: '图片压缩',
    category: '图片处理',
    description: '压缩图片体积，保持清晰度',
    keywords: ['image', 'compress', 'jpg', 'png']
  },
  {
    slug: 'image-resize',
    name: '图片缩放',
    category: '图片处理',
    description: '批量调整图片尺寸',
    keywords: ['image', 'resize']
  },
  {
    slug: 'image-crop',
    name: '图片裁剪',
    category: '图片处理',
    description: '按比例裁剪图片区域',
    keywords: ['image', 'crop']
  },
  {
    slug: 'image-format-convert',
    name: '图片格式转换',
    category: '图片处理',
    description: 'JPG/PNG/WebP 等格式互转',
    keywords: ['image', 'format', 'convert']
  },
  {
    slug: 'image-to-icon',
    name: '图片转图标',
    category: '图片处理',
    description: '生成多尺寸 PNG 图标',
    keywords: ['image', 'icon', 'png']
  },
  {
    slug: 'video-compress',
    name: '视频压缩',
    category: '音视频',
    description: '压缩视频体积，降低码率',
    keywords: ['video', 'compress', 'mp4']
  },
  {
    slug: 'audio-converter',
    name: '音频转换',
    category: '音视频',
    description: 'MP3/WAV/FLAC 格式转换',
    keywords: ['audio', 'convert', 'mp3', 'wav']
  },
  {
    slug: 'video-to-gif',
    name: '视频转 GIF',
    category: '音视频',
    description: '截取视频片段并导出 GIF',
    keywords: ['video', 'gif', 'convert']
  },
  {
    slug: 'audio-extract',
    name: '视频提取音频',
    category: '音视频',
    description: '从视频中提取音频轨道',
    keywords: ['video', 'audio', 'extract']
  },
  {
    slug: 'video-to-mp3',
    name: '视频转 MP3',
    category: '音视频',
    description: '将视频音轨快速导出为 MP3',
    keywords: ['video', 'mp3', 'convert']
  },
  {
    slug: 'csv-to-json',
    name: 'CSV 转 JSON',
    category: '数据处理',
    description: '将表格 CSV 数据转换为 JSON',
    keywords: ['csv', 'json', 'convert']
  },
  {
    slug: 'json-to-csv',
    name: 'JSON 转 CSV',
    category: '数据处理',
    description: '将 JSON 数据导出为 CSV 表格',
    keywords: ['json', 'csv', 'convert']
  },
  {
    slug: 'sql-formatter',
    name: 'SQL 格式化',
    category: '数据处理',
    description: '格式化 SQL 语句并高亮关键字',
    keywords: ['sql', 'format']
  },
  ...chartTools.map(meta => ({
    ...meta,
    component: meta.component || createChartToolComponent(meta, meta.chart)
  }))
]

function buildTools() {
  const tools = []

  for (const metaPath of metaContext.keys()) {
    // metaPath 形如：./uuid-generator/meta.js
    const slug = metaPath.split('/').slice(-2)[0]
    const viewPath = `./${slug}/index.vue`
    if (!viewContext.keys().includes(viewPath)) continue

    const meta = metaContext(metaPath).default
    const component = viewContext(viewPath).default || placeholderView

    if (!meta) continue
    tools.push({
      ...meta,
      // 默认启用工具；允许通过 meta.enabled=false 下线工具
      enabled: meta.enabled !== false,
      // sortOrder 用于同分类下排序，未配置时默认为 0
      sortOrder: typeof meta.sortOrder === 'number' ? meta.sortOrder : 0,
      component: markRaw(component)
    })
  }

  const existing = new Set(tools.map(t => t.slug))
  for (const meta of extraTools) {
    if (existing.has(meta.slug)) continue
    tools.push({
      ...meta,
      enabled: meta.enabled !== false,
      sortOrder: typeof meta.sortOrder === 'number' ? meta.sortOrder : 0,
      component: meta.component || createFileToolComponent(meta)
    })
  }

  return tools
    .filter(t => t && t.enabled && t.slug && t.component)
    .sort((a, b) => {
      // 排序：category -> sortOrder -> name
      const ax = `${a.category}|${String(a.sortOrder).padStart(6, '0')}|${a.name}`
      const bx = `${b.category}|${String(b.sortOrder).padStart(6, '0')}|${b.name}`
      return ax.localeCompare(bx)
    })
}

export const toolRegistry = {
  list() {
    return buildTools()
  }
}
