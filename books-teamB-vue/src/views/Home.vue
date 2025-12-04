<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <HeaderComponent />
    <Sidebar />

    <div class="ml-44 pt-20 px-8">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">

        <div class="lg:col-span-2 flex flex-col h-[calc(100vh-7rem)]">

          <div class="pb-4 bg-[#F5F1ED] sticky top-20 z-10">
            <h2 class="text-3xl font-semibold text-[#8B4513] text-left">
              Poste
            </h2>
          </div>

          <div class="flex-1 overflow-y-auto pr-2 space-y-4">

            <p v-if="loadingPosts" class="text-center text-gray-400">
              Chargement des posts...
            </p>

            <p v-else-if="posts.length === 0" class="text-center text-gray-500">
              les postes apparaîtront ici
            </p>

            <article
              v-else
              v-for="post in posts"
              :key="post.id"
              class="bg-white border border-gray-200 rounded-xl p-4 shadow-sm"
            >
              <p class="text-gray-800 whitespace-pre-line">
                {{ post.content }}
              </p>
              <p class="text-xs text-gray-400 mt-2">
                Publié le {{ formatDate(post.createdAt || post.created_at) }}
              </p>
            </article>

          </div>
        </div>


        <div class="bg-white rounded-2xl border border-gray-200 shadow-md p-6 h-fit mt-16">
          <h2 class="text-2xl font-semibold text-[#8B4513] mb-3 text-center">
            Créer un post
          </h2>

          <p class="text-sm text-gray-600 mb-4 text-center">
            Partagez votre avis ou expérience.
          </p>

          <form @submit.prevent="onSubmit" class="space-y-4">

            <input
              v-model="newPost.title"
              type="text"
              placeholder="Titre du post"
              class="w-full border border-gray-300 rounded-xl px-3 py-2 text-sm
                     focus:ring-2 focus:ring-orange-400 focus:border-orange-400"
            />

            <textarea
              v-model="newPost.content"
              placeholder="Écrivez votre post ici..."
              class="w-full h-40 border border-gray-300 rounded-xl px-3 py-2 text-sm
                     resize-none focus:ring-2 focus:ring-orange-400 focus:border-orange-400"
            ></textarea>

            <div class="text-right">
              <button
                type="submit"
                class="px-6 py-2 rounded-full text-sm font-semibold text-white bg-orange-500
                       hover:bg-orange-600 transition-all shadow-sm"
                :disabled="!newPost.content || saving"
              >
                Publier
              </button>
            </div>

          </form>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import Sidebar from '@/components/Sidebar.vue'
import HeaderComponent from '@/components/HeaderComponent.vue'
import postService from '@/services/postService'

export default {
  name: 'HomePage',
  components: {
    Sidebar,
    HeaderComponent
  },
  setup() {
    const authStore = useAuthStore()

    const posts = ref([])
    const loadingPosts = ref(false)
    const saving = ref(false)

    const newPost = ref({
      title: '',
      content: ''
    })

    const formatDate = (value) => {
      if (!value) return ''
      return new Date(value).toLocaleString('fr-FR', {
        dateStyle: 'short',
        timeStyle: 'short'
      })
    }

    const loadPosts = async () => {
      loadingPosts.value = true
      try {
        const res = await postService.list()
        posts.value = res.data
      } catch (err) {
        console.error('Erreur lors du chargement des posts :', err)
      } finally {
        loadingPosts.value = false
      }
    }

    const onSubmit = async () => {
      if (!newPost.value.content) return

      saving.value = true
      try {
        const user = authStore.getUser
        const userId = user && user.id ? user.id : null

        const payload = {
          content: newPost.value.title
            ? `${newPost.value.title} — ${newPost.value.content}`
            : newPost.value.content,
          userId
        }

        const res = await postService.add(payload)

        posts.value.unshift(res.data)

        newPost.value = {
          title: '',
          content: ''
        }
      } catch (err) {
        console.error("Erreur lors de l'envoi du post :", err)
      } finally {
        saving.value = false
      }
    }

    onMounted(() => {
      loadPosts()
    })

    return {
      posts,
      loadingPosts,
      saving,
      newPost,
      onSubmit,
      formatDate,
      authStore
    }
  }
}
</script>
