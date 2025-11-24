<template>
  <div class="flex flex-col gap-3 p-3 bg-background w-full h-screen">
    <AppHeader />

    <main class="w-full flex-1 min-h-0 flex gap-3">
      <MenuBar />

     <div class="bg-box border border-border rounded-2xl flex-1 h-full overflow-y-auto flex flex-col gap-4 p-6">
        <!-- Header -->
        <div class="w-full h-fit flex flex-col gap-1">
          <span class="text-primary-text font-semibold text-2xl">Catalogue</span>
          <span class="text-secondary-text font-normal text-l">Explorez notre catalogue de plus de ? livres</span>
        </div>

        <div class="w-full flex gap-3">
          <div class="relative w-full bg-background border border-border rounded-full">
            <Search class="absolute top-1/2 -translate-y-1/2 left-3 z-10 text-secondary-text" :size="24" />
            <input type="text" name="search" id="search" placeholder="ex. : Harry Potter" class="px-4 py-2 pl-11 w-full rounded-full font-normal">
          </div>

          <!-- Filters button -->
          <button
            @click="showFilters = !showFilters"
            class="bg-background hover:bg-box transition-colors border border-border rounded-full px-2 aspect-square cursor-pointer"
          >
            <Filter class="text-secondary-text" :size="22" />
          </button>
        </div>

        <!-- Books grid -->
        <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6 gap-6">
          <BookCard 
            v-for="book in filteredBooks"
            :key="book.id"
            :book="book"
            :show-actions="false"
          />
        </div>

        <!-- Empty state -->
        <div v-if="filteredBooks.length === 0" class="w-full h-full flex items-center justify-center flex-col gap-3">
          <BookOpen class="stroke-secondary-text" :size="50" />
          <p class="text-secondary-text">Aucun livre ne correspond à votre recherche</p>
        </div>
      </div>

      <!-- Filters sidebar -->
      <div
        v-if="showFilters"
        class="bg-box border border-border rounded-2xl w-56 h-full p-4 flex flex-col items-start justify-start gap-3"
      >
        <span class="w-full pb-3 border-b border-border text-primary-text font-medium text-lg">Filtres</span>

        <!-- Genre filter -->
        <div class="w-full py-2 flex flex-col gap-3">
          <span class="text-primary-text text-base font-medium">Genre</span>
          <div class="flex flex-wrap gap-2">
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedGenre" value="tous" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">Tous</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedGenre" value="Fantasy" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">Fantasy</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedGenre" value="Young Adult" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">Young Adult</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedGenre" value="Aventure" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">Aventure</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedGenre" value="Romance" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">Romance</span>
            </label>
          </div>
        </div>

        <!-- Year filter -->
        <div class="w-full py-2 flex flex-col gap-3">
          <span class="text-primary-text text-base font-medium">Année de publication</span>
          <div class="flex flex-wrap gap-2">
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedYear" value="toutes" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">Toutes</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedYear" value="2025-2020" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">2025-2020</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedYear" value="2019-2010" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">2019-2010</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedYear" value="2009-2000" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">2009-2000</span>
            </label>
            <label class="relative flex items-center">
              <input type="radio" v-model="selectedYear" value="avant 2000" class="sr-only peer">
              <span class="cursor-pointer px-3 py-1 rounded-full border border-border text-sm text-primary-text peer-checked:bg-accent peer-checked:border-accent peer-checked:text-white transition-colors">avant 2000</span>
            </label>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import AppHeader from '@/components/AppHeader.vue';
import MenuBar from '@/components/MenuBar.vue';
import BookCard from '@/components/BookCard.vue';
import { Search, Filter, BookOpen } from 'lucide-vue-next';

export default {
  name: 'Catalog',
  components: {
    AppHeader,
    MenuBar,
    BookCard,
    Search,
    Filter,
    BookOpen,
  },
  data() {
    return {
      searchQuery: '',
      selectedGenre: 'tous',
      selectedYear: 'toutes',
      showFilters: false,
      books: [
        {
          id: 6,
          title: 'Bloodmarked',
          authors: [{ firstName: 'Tracy', lastName: 'Deonn' }],
          year: '2010',
          genre: 'Fantasy',
          subject: { text: 'Fantasy' },
          desc_txt: 'The Legendborn Cycle, Book 2. Bree wants to heal, but the war has just begun.',
          cover: 'https://covers.openlibrary.org/a/id/12617874-L.jpg',
          created_at: '2010-03-02T03:44:17',
        },
        { 
          id: 1, 
          title: 'In Other Lands', 
          authors: [{ firstName: 'Sarah', lastName: 'Rees Brennan' }],
          year: '2017', 
          genre: 'Young Adult',
          subject: { text: 'Young Adult' },
          desc_txt: 'The Borderlands aren\'t like anywhere else. Don\'t try to smuggle a phone... Elliot is thirteen years old. He\'s smart and just a tiny bit obnoxious.',
          cover: 'https://covers.openlibrary.org/a/id/12354250-L.jpg',
          created_at: '2017-10-20T01:27:09',
        },
        { 
          id: 7, 
          title: 'Strange the Dreamer', 
          authors: [{ firstName: 'Laini', lastName: 'Taylor' }, { firstName: 'Tracy', lastName: 'Deonn' }],
          year: '2018', 
          genre: 'Fantasy',
          subject: { text: 'Fantasy' },
          desc_txt: 'The dream chooses the dreamer, not the other way around. Lazlo Strange, war orphan and junior librarian, has always feared that his dream chose poorly.',
          cover: 'https://covers.openlibrary.org/a/id/8367802-L.jpg',
          created_at: '2018-05-15T01:36:29',
        },
        { 
          id: 8, 
          title: 'Muse of Nightmares', 
          authors: [{ firstName: 'Laini', lastName: 'Taylor' }],
          year: '2018', 
          genre: 'Fantasy',
          subject: { text: 'Fantasy' },
          desc_txt: 'In the wake of tragedy, neither Lazlo nor Sarai are who they were before. One a god, the other a ghost, they struggle to grasp the new boundaries of their selves.',
          cover: 'https://covers.openlibrary.org/a/id/12366752-L.jpg',
          created_at: '2018-10-18T01:40:34',
        },
        { 
          id: 2, 
          title: "The Gentleman's Guide to Vice and Virtue", 
          authors: [{ firstName: 'Mackenzi', lastName: 'Lee' }],
          year: '2018', 
          genre: 'Romance',
          subject: { text: 'Romance' },
          desc_txt: 'A young bisexual British lord embarks on an unforgettable Grand Tour of Europe with his best friend/secret crush.',
          cover: 'https://covers.openlibrary.org/b/id/8225261-L.jpg',
          created_at: '2018-01-15T10:20:30',
        },
        { 
          id: 9, 
          title: 'The Darkest Minds', 
          authors: [{ firstName: 'Alexandra', lastName: 'Bracken' }],
          year: '2012', 
          genre: 'Young Adult',
          subject: { text: 'Young Adult' },
          desc_txt: 'When Ruby woke up on her tenth birthday, something about her had changed. Something alarming enough to make her parents lock her in the garage.',
          cover: 'https://covers.openlibrary.org/b/id/7572080-L.jpg',
          created_at: '2012-12-18T14:30:45',
        },
      ],
    };
  },
  computed: {
    filteredBooks() {
      let filtered = this.books;

      // Filter by search query
      if (this.searchQuery) {
        filtered = filtered.filter(book =>
          book.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          (book.authors && book.authors.some(author => 
            `${author.firstName} ${author.lastName}`.toLowerCase().includes(this.searchQuery.toLowerCase())
          )) ||
          (book.desc_txt && book.desc_txt.toLowerCase().includes(this.searchQuery.toLowerCase()))
        );
      };

      // Filter by genre
      if (this.selectedGenre !== 'tous') {
        filtered = filtered.filter(book => book.subject && book.subject.text === this.selectedGenre)
      };

      // Filter by year
      if (this.selectedYear !== 'toutes') {
        filtered = filtered.filter(book => {
          if (!book.created_at) return false
          const year = new Date(book.created_at).getFullYear()
          switch (this.selectedYear) {
            case '2025-2020':
              return year >= 2020 && year <= 2025
            case '2019-2010':
              return year >= 2010 && year <= 2019
            case '2009-2000':
              return year >= 2000 && year <= 2009
            case 'avant 2000':
              return year < 2000
            default:
              return true
          }
        })
      };

      return filtered;
    },
  },
};
</script>