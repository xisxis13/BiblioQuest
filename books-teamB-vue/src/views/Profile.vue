<template>
  <div class="flex flex-col gap-3 p-3 bg-background w-full h-screen">
    <AppHeader />

    <main class="w-full flex-1 min-h-0 flex gap-3">
      <MenuBar />

      <div class="bg-box border border-border rounded-3xl flex-1 h-full overflow-y-auto flex flex-col">
        <!-- User Info Header -->
        <div class="bg-gradient-to-b from-border to-transparent p-6 flex items-center gap-6">
          <div class="bg-accent aspect-square h-full rounded-full flex items-center justify-center flex-shrink-0 text-white text-5xl font-bold">
            {{ getUserInitials() }}
          </div>
          <div class="flex flex-col gap-1 py-2">
            <p class="text-secondary-text">{{ user?.email }}</p>
            <span class="w-full text-5xl font-bold text-primary-text">{{ user?.username }}</span>
            <p class="text-sm text-secondary-text mt-1">Membre depuis {{ formatDate(user?.createdAt) }}</p>
          </div>
        </div>

        <!-- Content -->
        <div class="flex-1 h-full flex flex-col gap-6 p-6">
          <!-- Stats Grid -->
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div class="bg-white border border-border rounded-2xl p-4 h-fit flex items-center justify-start gap-4">
              <div class="bg-green-100 w-12 h-12 flex items-center justify-center rounded-xl">
                <Check class="stroke-green-600" />
              </div>
              <div class="flex flex-col items-start justify-center">
                <span class="text-secondary-text font-normal text-sm">Livres lus</span>
                <span class="text-primary-text font-semibold text-2xl">{{ stats.booksRead }}</span>
              </div>
            </div>
  
            <div class="bg-white border border-border rounded-2xl p-4 h-fit flex items-center justify-start gap-4">
              <div class="bg-orange-100 w-12 h-12 flex items-center justify-center rounded-xl">
                <BookOpen class="stroke-orange-600" />
              </div>
              <div class="flex flex-col items-start justify-center">
                <span class="text-secondary-text font-normal text-sm">En cours</span>
                <span class="text-primary-text font-semibold text-2xl">{{ stats.booksReading }}</span>
              </div>
            </div>
  
            <div class="bg-white border border-border rounded-2xl p-4 h-fit flex items-center justify-start gap-4">
              <div class="bg-yellow-100 w-12 h-12 flex items-center justify-center rounded-xl">
                <Bookmark class="stroke-yellow-600" />
              </div>
              <div class="flex flex-col items-start justify-center">
                <span class="text-secondary-text font-normal text-sm">À lire</span>
                <span class="text-primary-text font-semibold text-2xl">{{ stats.booksToRead }}</span>
              </div>
            </div>
  
            <div class="bg-white border border-border rounded-2xl p-4 h-fit flex items-center justify-start gap-4">
              <div class="bg-purple-100 w-12 h-12 flex items-center justify-center rounded-xl">
                <FileText class="stroke-purple-600" />
              </div>
              <div class="flex flex-col items-start justify-center">
                <span class="text-secondary-text font-normal text-sm">Pages lues</span>
                <span class="text-primary-text font-semibold text-2xl">{{ stats.pagesRead }}</span>
              </div>
            </div>
          </div>
  
          <!-- Posts Section -->
          <div class="flex flex-col gap-4">
            <h2 class="text-xl font-semibold text-primary-text">Mes publications</h2>
            <div class="grid gap-4">
              <PostCard 
                v-for="post in userPosts" 
                :key="post.id"
                :post="{...post, user: user?.username}" 
              />
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import AppHeader from '@/components/AppHeader.vue';
import MenuBar from '@/components/MenuBar.vue';
import PostCard from '@/components/PostCard.vue';
import { Check, Bookmark, FileText, BookOpen } from 'lucide-vue-next';

export default {
  name: 'ProfilePage',
  components: {
    AppHeader,
    MenuBar,
    PostCard,
    Check,
    Bookmark,
    FileText, 
    BookOpen,
  },
  setup() {
    const route = useRoute()
    const authStore = useAuthStore()
    
    const user = ref({
      id: 1,
      username: 'Juste a user',
      email: 'justauser@example.com',
      createdAt: '2023-01-15T10:00:00Z'
    })

    const stats = ref({
      booksRead: 12,
      booksReading: 2,
      booksToRead: 8,
      pagesRead: 3450
    })

    const userPosts = ref([
      {
        id: 1,
        content: "Je viens de terminer le Comte de Monte-Cristo, quel chef-d'œuvre !",
        createdAt: '2023-11-05T15:30:00Z',
        likes: 15,
        comments: 4
      },
      {
        id: 2,
        content: "Lecture en cours : Les Trois Mousquetaires. D'Artagnan est impétueux !",
        createdAt: '2023-11-10T09:45:00Z',
        likes: 8,
        comments: 2
      },
      {
        id: 2,
        content: "Harry Potter quel kiffe !",
        createdAt: '2025-12-07T11:35:00Z',
        likes: 8,
        comments: 2
      }
    ])

    const loading = ref(false)
    const error = ref(null)
    const isFollowing = ref(false)
    const recentBooks = ref([
      { id: 1, title: 'Le Petit Prince', author: 'Antoine de Saint-Exupéry', cover: null },
      { id: 2, title: '1984', author: 'George Orwell', cover: null },
      { id: 3, title: 'L\'Étranger', author: 'Albert Camus', cover: null },
      { id: 4, title: 'Harry Potter', author: 'J.K. Rowling', cover: null },
      { id: 5, title: 'Le Seigneur des Anneaux', author: 'J.R.R. Tolkien', cover: null },
      { id: 6, title: 'Dune', author: 'Frank Herbert', cover: null }
    ])

    // Vérifier si c'est le profil de l'utilisateur connecté
    const isOwnProfile = computed(() => {
      const userId = route.params.id
      return !userId || userId === authStore.getUser?.id?.toString()
    })

    const getUserInitials = () => {
      if (user.value && user.value.username) {
        return user.value.username.substring(0, 1).toUpperCase()
      }
      return 'U'
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('fr-FR', { 
        year: 'numeric', 
        month: 'long' 
      })
    }

    return {
      user,
      stats,
      userPosts,
      loading,
      error,
      isFollowing,
      recentBooks,
      isOwnProfile,
      getUserInitials,
      formatDate,
    }
  }
}
</script>
