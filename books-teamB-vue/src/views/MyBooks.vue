<template>
  <div class="p-4 max-w-6xl mx-auto">
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-2xl font-semibold">Mes livres</h1>
      <button @click="fetchBooks" class="btn btn-sm">Rafraîchir</button>
    </div>

    <div v-if="loading" class="text-center py-8">Chargement des livres…</div>

    <div v-else>
      <div v-if="error" class="text-red-600 mb-4">{{ error }}</div>

      <Table :items="books" :columns="columns" :isLoading="loading" :error="error">
        <template #actions="{ item }">
          <button class="mr-2 px-3 py-1 text-sm bg-yellow-500 text-white rounded" disabled>Modifier</button>
          <button @click="confirmDelete(item.id)" class="px-3 py-1 text-sm bg-red-600 text-white rounded">Supprimer</button>
        </template>
      </Table>
    </div>
  </div>
</template>

<script>
import { bookService } from '@/services/bookService'
import Table from '@/components/Table.vue'

export default {
  name: 'MyBooks',
  components: { Table },
  data() {
    return {
      books: [],
      loading: false,
      error: null,
      columns: [
        { key: 'title', label: 'Titre' },
        { key: 'author', label: 'Auteur' },
        { key: 'subject', label: 'Sujet' },
        { key: 'year', label: 'Année' }
      ]
    }
  },
  created() {
    this.fetchBooks()
  },
  methods: {
    async fetchBooks() {
      this.loading = true
      this.error = null
      try {
        const res = await bookService.list()
        this.books = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        console.error('fetchBooks error', e)
        this.error = e.response?.data || e.message || 'Erreur lors de la récupération des livres.'
      } finally {
        this.loading = false
      }
    },

    confirmDelete(id) {
      if (!window.confirm('Confirmer la suppression de ce livre ?')) return
      this.deleteBook(id)
    },

    async deleteBook(id) {
      try {
        await bookService.remove(id)
        this.books = this.books.filter((b) => b.id !== id)
      } catch (e) {
        console.error('deleteBook error', e)
        const msg = e.response?.data || e.message || 'Erreur lors de la suppression.'
        window.alert(msg)
      }
    }
  }
}
</script>

<style scoped>
/* Simple button utilities if Tailwind classes not present in dev env */
.btn { display: inline-block; padding: 0.375rem 0.75rem; border-radius: 0.375rem; background: #e5e7eb; color: #111827 }
.btn-sm { padding: 0.25rem 0.5rem; font-size: 0.875rem }
</style>
