import { fileURLToPath } from 'node:url'
import { mergeConfig, defineConfig } from 'vite'
import viteConfig from './vite.config'

export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      environment: 'happy-dom',
      exclude: ['**/node_modules/**', '**/dist/**'],
      globals: true,
    },
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    }
  })
)
