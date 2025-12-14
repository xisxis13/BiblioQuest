<template>
  <div class="flex flex-col gap-3 p-3 bg-background w-full h-screen">
    <AppHeader />
    
    <main class="w-full flex-1 min-h-0 flex gap-3">
      <MenuBar />

      <div class="bg-box border border-border rounded-3xl flex-1 h-full overflow-y-auto flex flex-col gap-4 p-6">
        <!-- Header -->
        <div class="w-full h-fit flex flex-col gap-1">
          <span class="text-primary-text font-semibold text-2xl">Suivi de lecture</span>
          <span class="text-secondary-text font-normal text-base">Enregistrez vos sessions de lecture et vos réflexions</span>
        </div>

        <!-- Add session button -->
        <button
          @click="showSessionEditor = true"
          class="w-full py-3 px-4 flex items-center justify-center gap-2 border-2 border-dashed border-secondary-text rounded-2xl hover:border-accent transition-colors duration-200 cursor-pointer"
        >
          <PenLine :size="16" class="stroke-primary-text" />
          <span class="text-primary-text font-normal">Ajouter une session</span>
        </button>

        <!-- Recent Sessions -->
        <div class="flex flex-col items-start justify-start gap-3">
          <h2 class="text-xl font-semibold text-primary-text">Sessions Récentes</h2>

          <div v-if="readingSessions.length > 0" class="w-full flex flex-col gap-3">
            <SessionCard
              v-for="session in readingSessions"
              :key="session.id"
              :session="session"
            />
          </div>

          <div v-else class="w-full text-center py-12">
            <svg class="w-12 h-12 mx-auto text-secondary-text mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
            <p class="text-secondary-text">Aucune session enregistrée</p>
          </div>
        </div>
      </div>

      <!-- New session editor -->
      <div
        v-if="showSessionEditor"
        class="bg-box border border-border rounded-3xl w-72 h-full p-5 flex flex-col items-start justify-start gap-4"
      >
        <div class="w-full pb-3 border-b border-border flex items-center justify-between">
          <span class="text-primary-text font-medium text-lg">Nouvelle session</span>
          <button
            @click="closeSessionEditor"
            class="text-secondary-text hover:text-primary-text transition-colors cursor-pointer"
            type="button"
          >
            <X :size="20" class="stroke-secondary-text hover:stroke-primary-text" />
          </button>
        </div>

        <form 
          @submit.prevent="addSession"
          class="w-full h-full px-0.5 flex flex-col items-start justify-start gap-4 overflow-y-scroll"
        >
          <div class="w-full flex flex-col gap-2">
            <label class="text-sm font-medium text-primary-text">
              Livre
            </label>
            <select
              v-model="newSession.bookId"
              required
              class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm text-primary-text placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent appearance-none"
            >
              <option value="">Sélectionner un livre</option>
              <option v-for="book in currentlyReadingBooks" :key="book.id" :value="book.id">
                {{ book.title }}
              </option>
            </select>
          </div>

          <div class="w-full flex flex-col gap-2">
            <label class="text-sm font-medium text-primary-text">
              Page de début
            </label>
            <input
              v-model.number="newSession.startPage"
              type="number"
              required
              min="0"
              class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
              placeholder="0"
            />
          </div>

          <div class="w-full flex flex-col gap-2">
            <label class="text-sm font-medium text-primary-text">
              Page actuelle
            </label>
            <input
              v-model.number="newSession.endPage"
              type="number"
              required
              :min="newSession.startPage"
              class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
              placeholder="0"
            />
          </div>

          <div class="w-full flex flex-col gap-2">
            <label class="text-sm font-medium text-primary-text">
              Durée (minute)
            </label>
            <input
              v-model.number="newSession.duration"
              type="number"
              required
              min="1"
              class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
              placeholder="30"
            />
          </div>

          <div class="w-full flex flex-col gap-2">
            <label class="text-sm font-medium text-primary-text">
              Notes (optionnel)
            </label>
            <textarea
              v-model="newSession.notes"
              rows="3"
              maxlength="500"
              placeholder="Vos impressions sur cette session..."
              class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm text-primary-text placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent resize-none"
            ></textarea>
            <div class="text-xs text-secondary-text">
              {{newSession.notes.length }}/500 caractères
            </div>
          </div>

          <div class="w-full flex items-center gap-2">
            <button 
              type="button"
              @click="closeSessionEditor"
              class="bg-box hover:bg-background border border-border rounded-2xl px-4 py-2 cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 whitespace-nowrap"
            >
              <span>Annuler</span>
            </button>
  
            <button 
              type="submit"
              :disabled="!newSession.bookId"
              class="bg-accent hover:bg-accent/90 rounded-2xl w-full px-4 py-2 text-box font-normal cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              <span>Ajouter</span>
            </button>
          </div>
        </form>
      </div>
    </main>
  </div>
</template>

<script>
import AppHeader from '@/components/AppHeader.vue';
import MenuBar from '@/components/MenuBar.vue';
import SessionCard from '@/components/SessionCard.vue';
import { PenLine, X } from 'lucide-vue-next';
import { useReadingStore } from '@/stores/readingStore';
import { useAuthStore } from '@/stores/authStore';
import readingSessionService from '@/services/readingSessionService';

export default {
  name: 'ReadingTrackerPage',
  components: {
    AppHeader,
    MenuBar,
    SessionCard,
    PenLine,
    X,
  },
  setup() {
    const readingStore = useReadingStore()
    const authStore = useAuthStore()

    const currentUser = authStore.getUser()

    return { readingStore, currentUser }
  },
  data() {
    return {
      newSession: {
        bookId: '',
        startPage: 0,
        endPage: 0,
        duration: 30,
        notes: ''
      },
      currentlyReadingBooks: [],
      readingSessions: [],
      todayStats: {
        pagesRead: 0,
        readingTime: 0,
        sessions: 0,
        dailyGoal: 50
      },
      activeGoals: [],
      streak: 0,
      comment: '',
      saving: false,
      showSessionEditor: false
    }
  },
  methods: {
    closeSessionEditor() {
      this.showSessionEditor = false
      this.newSession = {
        bookId: '',
        startPage: 0,
        endPage: 0,
        duration: 30,
        notes: ''
      }
    },
    async addSession() {
      console.log("submited")
      const sessionData = {
        userBookDTO: { userId: this.currentUser.id, bookId: this.newSession.bookId },
        duration: this.newSession.duration,
        sessionNotes: this.newSession.notes,
        startPage: this.newSession.startPage,
        endPage: this.newSession.endPage,
      }

      const result = await readingSessionService.addSession(sessionData)
      
      if (result.success) {
        this.closeSessionEditor()
      }
    },
    async loadReadingSessions() {
      try {
        const response = await readingSessionService.getSessions(this.currentUser.id)
        this.readingSessions = response.data
      } catch (error) {
        console.error("Y'a une erreur bg : ", error)
      }
    },
    loadMockSessions() {
      // Mock data for testing
      this.recentSessions = [
        {
          id: 1,
          bookId: 1,
          bookTitle: 'Harry Potter à l\'école des sorciers',
          startPage: 10,
          endPage: 46,
          pagesRead: 36,
          duration: 45,
          notes: 'Chapitre passionnant ! J\'adore découvrir le monde magique.',
          createdAt: new Date().toISOString()
        },
        {
          id: 2,
          bookId: 2,
          bookTitle: 'Le Seigneur des Anneaux',
          startPage: 120,
          endPage: 165,
          pagesRead: 45,
          duration: 60,
          notes: 'La bataille de Helm est incroyable.',
          createdAt: new Date(Date.now() - 86400000).toISOString() // Yesterday
        },
        {
          id: 3,
          bookId: 3,
          bookTitle: '1984',
          startPage: 50,
          endPage: 78,
          pagesRead: 28,
          duration: 35,
          notes: 'L\'atmosphère dystopique est vraiment oppressante.',
          createdAt: new Date(Date.now() - 2 * 86400000).toISOString() // 2 days ago
        },
        {
          id: 4,
          bookId: 1,
          bookTitle: 'Harry Potter à l\'école des sorciers',
          startPage: 1,
          endPage: 10,
          pagesRead: 10,
          duration: 20,
          notes: 'Début prometteur !',
          createdAt: new Date(Date.now() - 3 * 86400000).toISOString() // 3 days ago
        },
        {
          id: 5,
          bookId: 4,
          bookTitle: 'Les Misérables',
          startPage: 200,
          endPage: 245,
          pagesRead: 45,
          duration: 55,
          notes: 'Victor Hugo est un génie de la description.',
          createdAt: new Date(Date.now() - 5 * 86400000).toISOString() // 5 days ago
        }
      ]
    }
  },
  mounted() {
    this.currentlyReadingBooks = [
      { id: 1, title: 'Anger is a Gift', totalPages: 400 },
      { id: 3, title: 'In Other Lands', totalPages: 370 },
    ]
    

    // Load mock data for testing
    this.loadReadingSessions()
  }
}
</script>

