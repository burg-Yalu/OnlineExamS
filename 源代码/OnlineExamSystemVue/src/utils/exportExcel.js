/**
 * 数据导出工具函数
 * 支持导出 Excel 和打印功能
 * PDF导出功能由后端Spring Boot实现，前端调用API下载
 */

import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'

/**
 * 导出表格数据为Excel
 * @param {Array} data - 表格数据数组
 * @param {Array} columns - 列配置 [{prop: '字段名', label: '列标题'}]
 * @param {String} filename - 文件名（不含扩展名）
 */
export function exportToExcel(data, columns, filename = '导出数据') {
  // 构建表头
  const header = columns.map(c => c.label)

  // 构建数据行
  const rows = data.map(row => {
    return columns.map(c => {
      const value = row[c.prop]
      // 处理特殊值
      if (value === null || value === undefined) return ''
      if (typeof value === 'object') return JSON.stringify(value)
      return value
    })
  })

  // 创建工作表
  const wsData = [header, ...rows]
  const ws = XLSX.utils.aoa_to_sheet(wsData)

  // 设置列宽
  ws['!cols'] = columns.map(c => ({ wch: Math.max(c.label.length * 2, 15) }))

  // 创建工作簿
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, 'Sheet1')

  // 导出文件
  const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
  saveAs(new Blob([wbout], { type: 'application/octet-stream' }), `${filename}.xlsx`)
}

/**
 * 导出Element UI表格数据为Excel（支持获取全部数据）
 * @param {Object} tableRef - el-table的ref引用
 * @param {Array} columns - 列配置
 * @param {String} filename - 文件名
 */
export function exportTableToExcel(tableRef, columns, filename = '导出数据') {
  // 获取表格数据
  const tableData = tableRef && tableRef.data ? tableRef.data : []
  exportToExcel(tableData, columns, filename)
}

/**
 * 打印页面
 * @param {String} title - 打印标题
 */
export function printPage(title) {
  // 设置打印标题
  if (title) {
    document.title = title
  }

  // 执行打印
  window.print()

  // 恢复标题
  if (title) {
    setTimeout(() => {
      document.title = '在线考试系统'
    }, 1000)
  }
}

/**
 * 打印指定表格
 * @param {String} tableId - 表格元素的id
 * @param {String} title - 打印标题
 */
export function printTable(tableId, title) {
  // 获取表格HTML
  const table = document.getElementById(tableId)
  if (!table) {
    console.error('未找到表格元素')
    return
  }

  // 创建打印窗口
  const printWindow = window.open('', '_blank')
  printWindow.document.write(`
    <!DOCTYPE html>
    <html>
    <head>
      <title>${title || '打印'}</title>
      <style>
        body { font-family: Arial, sans-serif; padding: 20px; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #333; padding: 8px; text-align: left; }
        th { background-color: #f5f5f5; }
        h2 { text-align: center; }
        @media print {
          body { padding: 0; }
        }
      </style>
    </head>
    <body>
      ${title ? `<h2>${title}</h2>` : ''}
      ${table.outerHTML}
    </body>
    </html>
  `)
  printWindow.document.close()
  printWindow.print()
}

/**
 * 打印Vue组件中的表格
 * @param {String} selector - 表格选择器
 * @param {String} title - 打印标题
 */
export function printVueTable(selector, title) {
  const table = document.querySelector(selector)
  if (!table) {
    console.error('未找到表格元素')
    return
  }

  printTable(table.id || 'table', title)
}