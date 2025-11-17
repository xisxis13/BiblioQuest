<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <Header />
    <Sidebar />
    <div class="ml-44 pt-20 px-6 py-8">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-4xl font-bold text-foreground mb-2">Suivi de Lecture</h1>
        <p class="text-muted-foreground">Enregistrez vos sessions de lecture et vos réflexions</p>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Main Content -->
        <div class="lg:col-span-2 space-y-6">
          <!-- New Session Card -->
          <div class="bg-white border border-border rounded-2xl p-6">
            <h2 class="text-xl font-bold text-foreground mb-4">Nouvelle Session de Lecture</h2>
            
            <form @submit.prevent="createSession" class="space-y-4">
              <div>
                <label class="text-sm font-semibold text-foreground mb-2 block">Livre</label>
                <select
                  v-model="newSession.bookId"
                  required
                  class="w-full px-4 py-3 bg-input border border-border rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                >
                  <option value="">Sélectionner un livre</option>
                  <option v-for="book in currentlyReadingBooks" :key="book.id" :value="book.id">
                    {{ book.title }}
                  </option>
                </select>
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="text-sm font-semibold text-foreground mb-2 block">Page de début</label>
                  <input
                    v-model.number="newSession.startPage"
                    type="number"
                    required
                    min="0"
                    class="w-full px-4 py-3 bg-input border border-border rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                    placeholder="0"
                  />
                </div>
                <div>
                  <label class="text-sm font-semibold text-foreground mb-2 block">Page actuelle</label>
                  <input
                    v-model.number="newSession.currentPage"
                    type="number"
                    required
                    :min="newSession.startPage"
                    class="w-full px-4 py-3 bg-input border border-border rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                    placeholder="0"
                  />
                </div>
              </div>

              <div>
                <label class="text-sm font-semibold text-foreground mb-2 block">Durée (minutes)</label>
                <input
                  v-model.number="newSession.duration"
                  type="number"
                  required
                  min="1"
                  class="w-full px-4 py-3 bg-input border border-border rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                  placeholder="30"
                />
              </div>

              <div>
                <label class="text-sm font-semibold text-foreground mb-2 block">Notes (optionnel)</label>
                <textarea
                  v-model="newSession.notes"
                  rows="3"
                  class="w-full px-4 py-3 bg-input border border-border rounded-xl text-sm placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent resize-none"
                  placeholder="Vos impressions sur cette session..."
                ></textarea>
              </div>

              <button
                type="submit"
                :disabled="!newSession.bookId"
                class="w-full bg-primary hover:bg-primary/90 text-primary-foreground font-semibold py-3 rounded-xl transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Enregistrer la session
              </button>
            </form>
          </div>

          <!-- Recent Sessions -->
          <div class="bg-white border border-border rounded-2xl p-6">
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-xl font-bold text-foreground">Sessions Récentes</h2>
              <button class="text-sm text-primary hover:underline">Voir tout</button>
            </div>

            <div v-if="recentSessions.length > 0" class="space-y-4">
              <div
                v-for="session in recentSessions"
                :key="session.id"
                class="flex items-start gap-4 p-4 bg-background rounded-xl"
              >
                <div class="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center flex-shrink-0">
                  <svg class="w-6 h-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                  </svg>
                </div>
                <div class="flex-1">
                  <h3 class="font-semibold text-foreground mb-1">{{ session.bookTitle }}</h3>
                  <p class="text-sm text-muted-foreground mb-2">
                    Pages {{ session.startPage }} - {{ session.currentPage }} • {{ session.pagesRead }} pages lues
                  </p>
                  <div class="flex items-center gap-4 text-xs text-muted-foreground">
                    <span class="flex items-center gap-1">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                      </svg>
                      {{ session.duration }} min
                    </span>
                    <span>{{ formatDate(session.createdAt) }}</span>
                  </div>
                  <p v-if="session.notes" class="text-sm text-muted-foreground mt-2 italic">
                    "{{ session.notes }}"
                  </p>
                </div>
              </div>
            </div>

            <div v-else class="text-center py-12">
              <svg class="w-12 h-12 mx-auto text-muted-foreground mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
              </svg>
              <p class="text-muted-foreground">Aucune session enregistrée</p>
            </div>
          </div>
        </div>

        <!-- Sidebar -->
        <div class="space-y-6">
          <!-- Today's Stats -->
          <div class="bg-white border border-border rounded-2xl p-6">
            <h3 class="text-lg font-bold text-foreground mb-4">Aujourd'hui</h3>
            <div class="space-y-4">
              <div>
                <div class="flex items-center justify-between mb-2">
                  <span class="text-sm text-muted-foreground">Pages lues</span>
                  <span class="text-lg font-bold text-primary">{{ todayStats.pagesRead }}</span>
                </div>
                <ProgressBar
                  :current-value="todayStats.pagesRead"
                  :max-value="todayStats.dailyGoal"
                  :show-details="false"
                  label=""
                />
              </div>
              <div class="flex items-center justify-between py-2 border-t border-border">
                <span class="text-sm text-muted-foreground">Temps de lecture</span>
                <span class="font-semibold text-foreground">{{ todayStats.readingTime }} min</span>
              </div>
              <div class="flex items-center justify-between py-2 border-t border-border">
                <span class="text-sm text-muted-foreground">Sessions</span>
                <span class="font-semibold text-foreground">{{ todayStats.sessions }}</span>
              </div>
            </div>
          </div>

          <!-- Reading Goals -->
          <div class="bg-white border border-border rounded-2xl p-6">
            <h3 class="text-lg font-bold text-foreground mb-4">Objectifs</h3>
            <div class="space-y-4">
              <div v-for="goal in activeGoals" :key="goal.id">
                <div class="flex items-center justify-between mb-2">
                  <span class="text-sm font-medium text-foreground">{{ goal.title }}</span>
                  <span class="text-xs text-muted-foreground">{{ goal.progress }}/{{ goal.target }}</span>
                </div>
                <ProgressBar
                  :current-value="goal.progress"
                  :max-value="goal.target"
                  :show-details="false"
                  label=""
                  :color="goal.progress >= goal.target ? 'success' : 'primary'"
                />
              </div>
              
              <button class="w-full mt-4 text-sm text-primary hover:underline">
                + Ajouter un objectif
              </button>
            </div>
          </div>

          <!-- Streak -->
          <div class="bg-gradient-to-br from-primary/10 to-accent/10 border border-primary/20 rounded-2xl p-6">
            <div class="text-center">
              <div class="w-16 h-16 bg-primary rounded-full flex items-center justify-center mx-auto mb-3">
                <svg class="w-8 h-8 text-primary-foreground" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" />
                </svg>
              </div>
              <h3 class="text-2xl font-bold text-foreground mb-1">{{ streak }} jours</h3>
              <p class="text-sm text-muted-foreground">Série de lecture</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ProgressBar from '@/components/ProgressBar.vue'
import Sidebar from '@/components/Sidebar.vue'
import Header from '@/components/Header.vue'
import { useReadingStore } from '@/stores/readingStore'

export default {
  name: 'ReadingTrackerPage',
  components: {
    ProgressBar,
    Sidebar,
    Header
  },
  setup() {
    const readingStore = useReadingStore()
    return { readingStore }
  },
  data() {
    return {
      newSession: {
        bookId: '',
        startPage: 0,
        currentPage: 0,
        duration: 30,
        notes: ''
      },
      currentlyReadingBooks: [],
      recentSessions: [],
      todayStats: {
        pagesRead: 0,
        readingTime: 0,
        sessions: 0,
        dailyGoal: 50
      },
      activeGoals: [],
      streak: 0
    }
  },
  methods: {
    async createSession() {
      const pagesRead = this.newSession.currentPage - this.newSession.startPage
      
      const sessionData = {
        bookId: this.newSession.bookId,
        startPage: this.newSession.startPage,
        currentPage: this.newSession.currentPage,
        pagesRead,
        duration: this.newSession.duration,
        notes: this.newSession.notes,
        totalPages: this.currentlyReadingBooks.find(b => b.id === this.newSession.bookId)?.totalPages || 0
      }

      const result = await this.readingStore.createReadingSession(sessionData)
      
      if (result.success) {
        // Reset form
        this.newSession = {
          bookId: '',
          startPage: 0,
          currentPage: 0,
          duration: 30,
          notes: ''
        }
        
        // Reload sessions
        this.loadRecentSessions()
        this.updateTodayStats()
      }
    },
    loadRecentSessions() {
      // Mock data - replace with API call
      this.recentSessions = this.readingStore.readingSessions
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .slice(0, 5)
        .map(session => ({
          ...session,
          bookTitle: this.currentlyReadingBooks.find(b => b.id === session.bookId)?.title || 'Livre inconnu'
        }))
    },
    updateTodayStats() {
      const today = new Date().toDateString()
      const todaySessions = this.readingStore.readingSessions.filter(
        session => new Date(session.createdAt).toDateString() === today
      )
      
      this.todayStats.sessions = todaySessions.length
      this.todayStats.pagesRead = todaySessions.reduce((total, session) => total + session.pagesRead, 0)
      this.todayStats.readingTime = todaySessions.reduce((total, session) => total + session.duration, 0)
    },
    formatDate(dateString) {
      const date = new Date(dateString)
      const now = new Date()
      const diffTime = Math.abs(now - date)
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
      
      if (diffDays === 0) return "Aujourd'hui"
      if (diffDays === 1) return 'Hier'
      if (diffDays < 7) return `Il y a ${diffDays} jours`
      
      return date.toLocaleDateString('fr-FR')
    }
  },
  mounted() {
    // Mock data - replace with API calls
    this.currentlyReadingBooks = [
      { id: 1, title: 'In Other Lands', totalPages: 400 }
    ]
    this.activeGoals = [
      { id: 1, title: 'Lire 50 pages/jour', progress: 35, target: 50 },
      { id: 2, title: 'Lire 5 livres/mois', progress: 2, target: 5 }
    ]
    this.streak = 7
    
    this.loadRecentSessions()
    this.updateTodayStats()
  }
}
</script>
