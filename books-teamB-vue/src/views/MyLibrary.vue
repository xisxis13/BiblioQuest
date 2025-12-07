<template>
  <div class="flex flex-col gap-3 p-3 bg-background w-full h-screen">
    <AppHeader />

    <main class="w-full flex-1 min-h-0 flex gap-3">
      <MenuBar />

     <div class="bg-box border border-border rounded-3xl flex-1 h-full overflow-y-auto flex flex-col gap-4 p-6">
        <!-- header -->
        <div class="w-full h-fit flex flex-col gap-1">
          <span class="text-primary-text font-semibold text-2xl">Ma bibliothèque</span>
          <span class="text-secondary-text font-normal text-l">Gérez vos livres et suivez votre progression de lecture</span>
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

          <div class="bg-white border border-border rounded-3xl p-4 h-fit flex items-center justify-start gap-4">
            <div class="bg-purple-100 w-12 h-12 flex items-center justify-center rounded-xl">
              <FileText class="stroke-purple-600" />
            </div>
            <div class="flex flex-col items-start justify-center">
              <span class="text-secondary-text font-normal text-sm">Pages lues</span>
              <span class="text-primary-text font-semibold text-2xl">{{ stats.totalPagesRead }}</span>
            </div>
          </div>
        </div>

        <!-- Tabs -->
        <div class="flex gap-4 border-b border-border">
          <button
            v-for="tab in tabs"
            :key="tab.value"
            @click="activeTab = tab.value"
            :class="[
              'px-6 py-3 font-medium transition-colors relative',
              activeTab === tab.value
                ? 'text-accent'
                : 'text-secondary-text hover:text-accent'
            ]"
          >
            {{ tab.label }}
            <div
              v-if="activeTab === tab.value"
              class="absolute bottom-0 left-0 right-0 h-0.5 bg-accent rounded-full"
            ></div>
          </button>
        </div>

        <!-- Books grid -->
        <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6 gap-6">
          <BookCard 
            v-for="book in filteredBooks"
            :key="book.id"
            :book="book"
            :status-class="getStatusBagdeClass(book.status)"
            :status-label="getStatusLabel(book.status)"
            :show-actions="true"
            @update-status="updateBookStatus"
          />
        </div>

        <!-- Empty state -->
        <div v-if="filteredBooks.length === 0" class="w-full h-full flex items-center justify-center flex-col gap-3">
          <BookOpen class="stroke-secondary-text" :size="50" />
          <p class="text-secondary-text">Aucun livre dans cette catégorie</p>
          <router-link
            to="/library"
            class="bg-accent text-white font-medium flex items-center px-5 py-3 gap-3 rounded-2xl transition-colors duration-200"
          >
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

export default {
  name: 'Home',
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
      activeTab: 'all',
      tabs: [
        { label: 'Tous', value: 'all' },
        { label: 'En cours', value: 'reading' },
        { label: 'À lire', value: 'to-read' },
        { label: 'Lus', value: 'read' },
      ],
      stats: {
        totalBooksRead: 0,
        booksInProgress: 0,
        booksToRead: 0,
        totalPagesRead: 0,
      },
      userBooks: [],
    };
  },
  computed: {
    filteredBooks() {
      if (this.activeTab === 'all') {
        return this.userBooks;
      }
      return this.userBooks.filter(book => book.status === this.activeTab);
    },
  },
  methods: {
    getStatusLabel(status) {
      const labels = {
        'reading': 'En cours',
        'to-read': 'À lire',
        'read': 'Lu',
      };
      return labels[status] || status;
    },
    getStatusBagdeClass(status) {
      const classes = {
        'reading': 'bg-accent text-box',
        'to-read': 'bg-yellow-100 text-yellow-700',
        'read': 'bg-green-100 text-green-700',
      };
      return classes[status] || 'bg-border text-primary-text';
    },
    async updateBookStatus(bookId, status) {
      const book = this.userBooks.find(b => b.id === bookId);
      if (book) {
        book.status = status;
        // Mise à jour des stats
        this.stats.booksInProgress = this.userBooks.filter(b => b.status === 'reading').length;
        this.stats.booksToRead = this.userBooks.filter(b => b.status === 'to-read').length;
        this.stats.totalBooksRead = this.userBooks.filter(b => b.status === 'read').length;
        // API call to update status
        // await axios.put(`/api/users/books/${bookId}`, { status })
      };
    },
    async loadUserBooks() {
      // Mock data - replace with API call
      this.userBooks = [
        {
          id: 1,
          title: 'In Other Lands',
          authors: 'Sarah Rees Brennan',
          status: 'reading',
          coverUrl: 'https://covers.openlibrary.org/a/id/12354250-L.jpg#',
          progress: {
            currentPage: 92,
            totalPages: 400
          }
        },
        {
          id: 2,
          title: 'The Gentleman\'s Guide to Vice and Virtue',
          authors: 'Mackenzi Lee',
          status: 'read',
          coverUrl: 'https://covers.openlibrary.org/a/id/8367802-L.jpg#',
          progress: {
            currentPage: 150,
            totalPages: 400
          }
        },
        {
          id: 3,
          title: 'Anger is a Gift',
          authors: 'Mark Oshiro',
          status: 'to-read',
          coverUrl: 'https://covers.openlibrary.org/a/id/12366752-L.jpg#',
          progress: {
            currentPage: 150,
            totalPages: 400
          }
        },
        {
          id: 4,
          title: 'The Lady\'s Guide to Petticoats and Piracy',
          authors: 'Mackenzi Lee',
          status: 'reading',
          coverUrl: 'https://covers.openlibrary.org/a/id/8754386-L.jpg#',
          progress: {
            currentPage: 321,
            totalPages: 400
          }
        },
        {
          id: 5,
          title: 'Zachary Ying and the Dragon Emperor',
          authors: 'Leigh Bardugo',
          status: 'read',
          coverUrl: '',
          progress: {
            currentPage: 321,
            totalPages: 410
          }
        },
      ];

      // Update stats
      this.stats.booksInProgress = this.userBooks.filter(b => b.status === 'reading').length;
      this.stats.booksToRead = this.userBooks.filter(b => b.status === 'to-read').length;
      this.stats.totalBooksRead = this.userBooks.filter(b => b.status === 'read').length;
    },
  },
  mounted() {
    this.loadUserBooks();
  },
};
</script>