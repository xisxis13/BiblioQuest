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
          <div class="flex flex-col gap-2 py-2">
            <p class="text-sm text-secondary-text">{{ user?.email }}</p>
            <span class="w-full text-5xl font-bold text-primary-text">{{ user?.username }}</span>
            <p class="text-sm text-secondary-text mt-1">Dernière activité {{ formatDate(user?.lastActivityAt) }}</p>
          </div>
        </div>

        <!-- Content -->
        <div class="flex-1 h-full flex flex-col gap-6 p-6">
          <!-- Stats Grid -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="bg-white border border-border rounded-3xl p-4 h-fit flex items-center justify-start gap-4">
              <div class="bg-blue-100 w-12 h-12 flex items-center justify-center rounded-xl">
                <Library class="stroke-blue-600" />
              </div>
              <div class="flex flex-col items-start justify-center">
                <span class="text-secondary-text font-normal text-sm">Livres</span>
                <span class="text-primary-text font-semibold text-2xl">{{ stats.booksOwn }}</span>
              </div>
            </div>

            <div class="bg-white border border-border rounded-3xl p-4 h-fit flex items-center justify-start gap-4">
              <div class="bg-green-100 w-12 h-12 flex items-center justify-center rounded-xl">
                <Check class="stroke-green-600" />
              </div>
              <div class="flex flex-col items-start justify-center">
                <span class="text-secondary-text font-normal text-sm">Livres lus</span>
                <span class="text-primary-text font-semibold text-2xl">{{ stats.booksRead }}</span>
              </div>
            </div>

            <div class="bg-white border border-border rounded-3xl p-4 h-fit flex items-center justify-start gap-4">
              <div class="bg-purple-100 w-12 h-12 flex items-center justify-center rounded-xl">
                <Users class="stroke-purple-600" />
              </div>
              <div class="flex flex-col items-start justify-center">
                <span class="text-secondary-text font-normal text-sm">Abonnés</span>
                <span class="text-primary-text font-semibold text-2xl">{{ stats.followers }}</span>
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
                :post="post"
              />
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useUserStore } from '@/stores/userStore'
import AppHeader from '@/components/AppHeader.vue';
import MenuBar from '@/components/MenuBar.vue';
import PostCard from '@/components/PostCard.vue';
import { Check, Library, Users } from 'lucide-vue-next';
import postService from '@/services/postService';

export default {
  name: 'ProfilePage',
  components: {
    AppHeader,
    MenuBar,
    PostCard,
    Check,
    Library,
    Users,
  },
  setup() {
    const route = useRoute()
    const authStore = useAuthStore()
    const userStore = useUserStore()

    const user = ref(null)

    const stats = computed(() => ({
      booksRead: 0, // TODO: récupérer depuis UserBook
      booksReading: 0, // TODO: récupérer depuis UserBook
      booksOwn: 0, // TODO: récupérer depuis UserBook
      followers: user.value?.followersCount || 0
    }))

    const userPosts = ref([])

    const loading = ref(false)
    const error = ref(null)
    const isFollowing = ref(false)
    const recentBooks = ref([])

    const isOwnProfile = computed(() => {
      const userId = route.params.id
      return userId === authStore.getUser().id
    })

    const loadUserData = async () => {
      loading.value = true
      error.value = null

      try {
        const userId = Number(route.params.id)
        await userStore.getById(userId).then(async (data) => {
          user.value = data
          await postService.getUserPosts(data.id).then((posts) => {
            userPosts.value = posts.data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
          })
        })

      } catch (err) {
        console.error('Error loading user data:', err)
        error.value = err.message || 'Failed to load user data'
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      loadUserData()
    })


    const getUserInitials = () => {
      if (user.value && user.value.username) {
        return user.value.username.substring(0, 1).toUpperCase()
      }
      return 'U'
    }

    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'
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
      loadUserData
    }
  }
}
</script>
