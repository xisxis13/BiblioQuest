<template>
  <div class="grid grid-cols-12 gap-6">
    <aside class="col-span-12 lg:col-span-3">
      <div class="bg-card border border-border rounded-2xl p-4 space-y-4">
        <div class="relative">
          <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd"/>
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Rechercher un livre..."
            class="w-full h-10 rounded-2xl border border-input bg-background pl-10 pr-3 text-sm placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
          />
        </div>

        <div>
          <h3 class="text-sm font-semibold mb-2">Statut</h3>
          <div class="space-y-2">
            <button
              v-for="status in statusFilters"
              :key="status.value"
              @click="selectedStatus = status.value"
              :class="[
                'w-full inline-flex items-center justify-center rounded-xl text-sm font-medium h-9 px-3 transition-colors',
                selectedStatus === status.value
                  ? 'bg-primary text-primary-foreground hover:bg-primary/90'
                  : 'border border-input bg-background hover:bg-accent hover:text-accent-foreground'
              ]"
            >
              {{ status.label }}
            </button>
          </div>
        </div>

        <button @click="showAddModal = true" class="w-full bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-4 rounded-2xl font-medium">
          Ajouter un livre
        </button>
      </div>
    </aside>

    <main class="col-span-12 lg:col-span-9">
      <h1 class="text-2xl font-semibold mb-4">Ma Bibliothèque</h1>
      <div v-if="filteredBooks.length === 0" class="text-center py-12">
        <svg class="w-12 h-12 text-muted-foreground mx-auto mb-4" fill="currentColor" viewBox="0 0 20 20">
          <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 715.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z"/>
        </svg>
        <p class="text-muted-foreground">Aucun livre trouvé</p>
      </div>

      <div v-else class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 xl:grid-cols-5 gap-4">
        <BookCard
          v-for="book in filteredBooks"
          :key="book.id"
          :book="book"
          @edit="editBook"
          @delete="deleteBook"
          @update-status="updateBookStatus"
          @start-reading="startReading"
        />
      </div>
    </main>

    <BookModal v-if="showAddModal || showEditModal" :book="selectedBook" :is-editing="showEditModal" @close="closeModals" @save="saveBook" />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBooksStore } from '@/stores/booksStore'
import BookCard from '@/components/BookCard.vue'
import BookModal from '@/components/BookModal.vue'

export default {
  name: 'LibraryPage',
  components: { BookCard, BookModal },
  setup() {
    const router = useRouter()
    const booksStore = useBooksStore()

    const searchQuery = ref('')
    const selectedStatus = ref('all')
    const showAddModal = ref(false)
    const showEditModal = ref(false)
    const selectedBook = ref(null)
    const isLoading = ref(true)

    const statusFilters = [
      { value: 'all', label: 'Tous' },
      { value: 'TO_READ', label: 'À lire' },
      { value: 'IN_PROGRESS', label: 'En cours' },
      { value: 'COMPLETED', label: 'Terminés' }
    ]

    const filteredBooks = computed(() => {
      let books = booksStore.books
      if (selectedStatus.value !== 'all') {
        books = books.filter(book => book.status === selectedStatus.value)
      }
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        books = books.filter(book => book.title.toLowerCase().includes(query) || book.author.toLowerCase().includes(query))
      }
      return books
    })

    const loadBooks = async () => {
      try {
        isLoading.value = true
        await booksStore.fetchUserBooks()
      } catch (error) {
        console.error('Erreur chargement livres:', error)
      } finally {
        isLoading.value = false
      }
    }

    const editBook = (book) => {
      selectedBook.value = book
      showEditModal.value = true
    }

    const deleteBook = async (bookId) => {
      if (confirm('Supprimer ce livre ?')) {
        await booksStore.deleteBook(bookId)
      }
    }

    const updateBookStatus = async (bookId, newStatus) => {
      await booksStore.updateBookStatus(bookId, newStatus)
    }

    const startReading = (book) => {
      router.push(`/reading/${book.id}`)
    }

    const saveBook = async (bookData) => {
      if (showEditModal.value) {
        await booksStore.updateBook(selectedBook.value.id, bookData)
      } else {
        await booksStore.addBook(bookData)
      }
      closeModals()
    }

    const closeModals = () => {
      showAddModal.value = false
      showEditModal.value = false
      selectedBook.value = null
    }

    onMounted(() => {
      loadBooks()
    })

    return {
      searchQuery,
      selectedStatus,
      statusFilters,
      showAddModal,
      showEditModal,
      selectedBook,
      isLoading,
      filteredBooks,
      editBook,
      deleteBook,
      updateBookStatus,
      startReading,
      saveBook,
      closeModals
    }
  }
}
</script>