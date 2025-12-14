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

        <!-- Loading state -->
        <div v-if="loading" class="w-full h-full flex items-center justify-center flex-col gap-3">
          <svg class="animate-spin h-10 w-10 text-secondary-text" xmlns="http://www.w3.org/2000/svg" fill="none"
            viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z">
            </path>
          </svg>
          <p class="text-secondary-text">Chargement des sessions de lecture...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="w-full h-full flex items-center justify-center flex-col gap-3">
          <Info class="stroke-secondary-text" :size="50" />
          <p class="text-secondary-text">Erreur lors du chargement</p>
          <p class="text-sm text-gray-400">{{ error }}</p>

          <button @click="loadReadingSessions()" type="button"
            class="bg-accent hover:bg-accent/90 rounded-2xl px-4 py-3 text-box font-normal cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 whitespace-nowrap">
            <RotateCw :size="20" />
            <span>Réassayer</span>
          </button>
        </div>

        <!-- Recent Sessions -->
        <div v-else class="flex flex-col items-start justify-start gap-3">
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
              <option v-for="book in userBooks" :key="book.id" :value="book.id">
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
import { PenLine, X, Info, RotateCw } from 'lucide-vue-next';
import { useReadingStore } from '@/stores/readingStore';
import { useAuthStore } from '@/stores/authStore';
import readingSessionService from '@/services/readingSessionService';
import userBookService from '@/services/userBookService';

export default {
  name: 'ReadingTrackerPage',
  components: {
    AppHeader,
    MenuBar,
    SessionCard,
    PenLine,
    X,
    Info,
    RotateCw,
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
      userBooks: [],
      readingSessions: [],
      loading: true,
      error: null,
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
      this.readingSessions.push(result)
      this.readingSessions = this.readingSessions.sort((a, b) => new Date(b.sessionDate) - new Date(a.sessionDate));
      
      if (result.success) {
        this.closeSessionEditor()
      }
    },
    async loadReadingSessions() {
      try {
        this.loading = true
        this.error = null

        const response = await readingSessionService.getSessions(this.currentUser.id)
        this.readingSessions = response.sort((a, b) => new Date(b.sessionDate) - new Date(a.sessionDate));

      } catch (error) {
        console.error('Erreur lors du chargement des sessions de lecture:', error)
        this.error = 'Impossible de charger les sessions de lecture. Vérifiez que le serveur est démarré.'
      } finally {
        this.loading = false
      }
    },
  },
  async mounted() {
    this.userBooks = await userBookService.getAllBooks(this.currentUser.id)
    this.loadReadingSessions()
  }
}
</script>

