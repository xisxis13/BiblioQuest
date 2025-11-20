<template>
  <div>
    <div v-if="isLoading" class="text-center py-8">Chargement…</div>

    <div v-else>
      <div v-if="items.length === 0" class="text-gray-600">Aucun élément à afficher.</div>

      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th v-for="col in columns" :key="col.key" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                {{ col.label }}
              </th>
              <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="item in items" :key="item.id">
              <td v-for="col in columns" :key="col.key" class="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                {{ renderValue(item, col.key) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-center">
                <slot name="actions" :item="item"></slot>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="error" class="mt-2 text-sm text-red-600">{{ error }}</div>
  </div>
</template>

<script>
export default {
  name: 'Table',
  props: {
    items: {
      type: Array,
      default: () => []
    },
    columns: {
      type: Array,
      default: () => [
        { key: 'title', label: 'Titre' },
        { key: 'author', label: 'Auteur' },
        { key: 'subject', label: 'Sujet' },
        { key: 'year', label: 'Année' }
      ]
    },
    isLoading: {
      type: Boolean,
      default: false
    },
    error: {
      type: [String, null],
      default: null
    }
  },
  methods: {
    // Support nested objects like 'author.name' and handle objects gracefully
    renderValue(item, key) {
      try {
        const parts = key.split('.')
        let value = item
        for (const p of parts) {
          value = value?.[p]
        }
        if (value == null) return '-'
        if (typeof value === 'object') {
          // Prefer 'name' property if present
          return value.name ?? JSON.stringify(value)
        }
        return value
      } catch (e) {
        return '-'
      }
    }
  }
}
</script>

<style scoped>
/* Minimal styling fallbacks if Tailwind isn't loaded */
table { width: 100%; border-collapse: collapse }
th, td { border-bottom: 1px solid #e5e7eb }
</style>