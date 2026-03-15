import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

// Resolve directories relative to this script file
const root = path.dirname(fileURLToPath(new URL('.', import.meta.url)))
const distDir = path.join(root, 'dist')
const targetDir = path.join(root, '..', 'backend', 'src', 'main', 'resources', 'static')

console.log('frontend root:', root)
console.log('dist dir:', distDir)
console.log('target dir:', targetDir)

async function copyDist() {
  try {
    // Remove existing target folder to avoid stale files
    await fs.promises.rm(targetDir, { recursive: true, force: true })
    await fs.promises.mkdir(targetDir, { recursive: true })

    // Copy dist output into backend resources/static
    await fs.promises.cp(distDir, targetDir, { recursive: true })

    console.log(`Copied ${distDir} -> ${targetDir}`)
  } catch (err) {
    console.error('Failed to copy dist output:', err)
    process.exit(1)
  }
}

copyDist()
