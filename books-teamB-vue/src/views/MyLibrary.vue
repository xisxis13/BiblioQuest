<template>
  <div class="flex flex-col gap-3 p-3 bg-background w-full h-screen">
    <AppHeader />

    <main class="w-full flex-1 min-h-0 flex gap-3">
      <MenuBar />

      <div class="bg-box border border-border rounded-3xl flex-1 h-full overflow-y-auto flex flex-col gap-4 p-6">
        <!-- header -->
        <div class="w-full h-fit flex flex-col gap-1">
          <span class="text-primary-text font-semibold text-2xl">Ma bibliothèque</span>
          <span class="text-secondary-text font-normal text-l">Gérez vos livres et suivez votre progression de
            lecture</span>
        </div>

        <!-- Stats card -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div class="bg-white border border-border rounded-3xl p-4 h-fit flex items-center justify-start gap-4">
            <div class="bg-green-100 w-12 h-12 flex items-center justify-center rounded-xl">
              <Check class="stroke-green-600" />
            </div>
            <div class="flex flex-col items-start justify-center">
              <span class="text-secondary-text font-normal text-sm">Livres lus</span>
              <span class="text-primary-text font-semibold text-2xl">{{ stats.totalBooksRead }}</span>
            </div>
          </div>

          <div class="bg-white border border-border rounded-3xl p-4 h-fit flex items-center justify-start gap-4">
            <div class="bg-orange-100 w-12 h-12 flex items-center justify-center rounded-xl">
              <BookOpen class="stroke-orange-600" />
            </div>
            <div class="flex flex-col items-start justify-center">
              <span class="text-secondary-text font-normal text-sm">En cours</span>
              <span class="text-primary-text font-semibold text-2xl">{{ stats.booksInProgress }}</span>
            </div>
          </div>

          <div class="bg-white border border-border rounded-3xl p-4 h-fit flex items-center justify-start gap-4">
            <div class="bg-yellow-100 w-12 h-12 flex items-center justify-center rounded-xl">
              <Bookmark class="stroke-yellow-600" />
            </div>
            <div class="flex flex-col items-start justify-center">
              <span class="text-secondary-text font-normal text-sm">À lire</span>
              <span class="text-primary-text font-semibold text-2xl">{{ stats.booksToRead }}</span>
            </div>
          </div>
        </div>

        <!-- Tabs -->
        <div class="flex gap-4 border-b border-border">
          <button v-for="tab in tabs" :key="tab.value" @click="activeTab = tab.value" :class="[
            'px-6 py-3 font-medium transition-colors relative',
            activeTab === tab.value
              ? 'text-accent'
              : 'text-secondary-text hover:text-accent'
          ]">
            {{ tab.label }}
            <div v-if="activeTab === tab.value" class="absolute bottom-0 left-0 right-0 h-0.5 bg-accent rounded-full">
            </div>
          </button>
        </div>

        <!-- Books grid -->
        <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6 gap-6">
          <BookCard v-for="book in filteredBooks" :key="book.id" :book="book"
            :status-class="getStatusBagdeClass(book.readingStatusId)"
            :status-label="getStatusLabel(book.readingStatusId)" :show-actions="true"
            @update-status="updateBookStatus" />
        </div>

        <!-- Loading state -->
        <div v-if="loading" class="w-full h-full flex items-center justify-center flex-col gap-3">
          <svg class="animate-spin h-10 w-10 text-secondary-text" xmlns="http://www.w3.org/2000/svg" fill="none"
            viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z">
            </path>
          </svg>
          <p class="text-secondary-text">Chargement des livres...</p>
        </div>

      <!-- Empty state -->
        <div v-if=" !loading && filteredBooks.length === 0" class="w-full h-full flex items-center justify-center flex-col gap-3">
          <BookOpen class="stroke-secondary-text" :size="50" />
          <p class="text-secondary-text">Aucun livre dans cette catégorie</p>
          <router-link to="/library"
            class="bg-accent text-white font-medium flex items-center px-5 py-3 gap-3 rounded-2xl transition-colors duration-200">
            Explorer la Bibliothèque
          </router-link>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import AppHeader from '@/components/AppHeader.vue';
import MenuBar from '@/components/MenuBar.vue';
import BookCard from '@/components/BookCard.vue';
import { Check, BookOpen, Bookmark, FileText } from 'lucide-vue-next';
import userBookService from '@/services/userBookService';
import { useAuthStore } from '@/stores/authStore';

export default {
  name: 'MyLibrary',
  components: {
    AppHeader,
    MenuBar,
    BookCard,
    Check,
    BookOpen,
    Bookmark,
    FileText,
  },
  data() {
    return {
      loading: true,
      activeTab: -1,
      tabs: [
        { label: 'Tous', value: -1 },
        { label: 'En cours', value: 1 },
        { label: 'À lire', value: 0 },
        { label: 'Lus', value: 2 },
      ],
      stats: {
        totalBooksRead: 0,
        booksInProgress: 0,
        booksToRead: 0,
        totalPagesRead: 0,
      },
      userBooks: [],
      authStore: null,
      currentUserId: -1,
      readingStatus: {
        0: 'À lire',
        1: 'En cours',
        2: 'Lu',
        3: 'Abandonné'
      }
    };
  },
  computed: {
    filteredBooks() {
      if (this.activeTab === -1) {
        return this.userBooks;
      }
      return this.userBooks.filter(book => book.readingStatusId === this.activeTab);
    },
  },
  methods: {
    getStatusLabel(statusId) {

      return this.readingStatus[statusId] || "Undefined";
    },
    getStatusBagdeClass(status) {
      const classes = {
        1: 'bg-accent text-box',
        0: 'bg-yellow-100 text-yellow-700',
        2: 'bg-green-100 text-green-700',
      };
      return classes[status] || 'bg-border text-primary-text';
    },
    async updateBookStatus(bookId, status) {
      const book = this.userBooks.find(b => b.id === bookId);
      if (book) {
        book.readingStatusId = status;
        // Mise à jour des stats
        this.stats.booksInProgress = this.userBooks.filter(b => b.readingStatusId === 1).length;
        this.stats.booksToRead = this.userBooks.filter(b => b.readingStatusId === 0).length;
        this.stats.totalBooksRead = this.userBooks.filter(b => b.readingStatusId === 2).length;
        await userBookService.updateReadingStatus(this.currentUserId, bookId, status)
      };
    },
    async loadUserBooks() {
      try {
        this.userBooks = await userBookService.getAllBooks(this.currentUserId)
      } catch (e) {
        console.log("error while fetching user Books", e)
      } finally {
        this.loading = false
      }
      // Update stats
      this.stats.booksInProgress = this.userBooks.filter(b => b.readingStatusId === 1).length;
      this.stats.booksToRead = this.userBooks.filter(b => b.readingStatusId === 0).length;
      this.stats.totalBooksRead = this.userBooks.filter(b => b.readingStatusId === 2).length;
    },
  },
  mounted() {
    const authStore = useAuthStore()
    this.currentUserId = authStore.getUser().id
    this.loadUserBooks();
  },
};
</script>
