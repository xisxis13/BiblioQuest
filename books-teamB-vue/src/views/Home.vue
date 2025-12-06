<template>
  <div class="flex flex-col gap-3 p-3 bg-background w-full h-screen">
    <AppHeader />

    <main class="w-full flex-1 min-h-0 flex gap-3">
      <MenuBar />

     <div class="bg-box border border-border rounded-3xl flex-1 h-full overflow-y-auto flex flex-col gap-4 p-6">
        <!-- Header -->
        <div class="w-full h-fit flex flex-col gap-1">
          <span class="text-primary-text font-semibold text-2xl">Publications</span>
          <span class="text-secondary-text font-normal text-l">Découvrez ce que la communauté a à partager</span>
        </div>

        <!-- Loading state -->
        <div v-if="loadingPosts" class="w-full h-full flex items-center justify-center flex-col gap-3">
          <svg class="animate-spin h-10 w-10 text-secondary-text" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p class="text-secondary-text">Chargement des publications...</p>
        </div>

        <!-- Posts list -->
        <div 
          v-if="!loadingPosts"
          class="w-full h-full flex items-center justify-start flex-col gap-3 overflow-scroll"
        >
          <PostItem
            v-for="post in posts" 
            :key="post.id" 
            :post="post"
          />
        </div>

        <!-- Empty state -->
        <div v-if="!loadingPosts && posts.length === 0" class="w-full h-full flex items-center justify-center flex-col gap-3">
          <SquarePen class="stroke-secondary-text" :size="50" />
          <p class="text-secondary-text">Aucune publication pour le moment</p>
        </div>
      </div>

      <!-- Post editor sidebar -->
      <div
        class="bg-box border border-border rounded-3xl w-72 h-full p-5 flex flex-col items-start justify-start gap-4 overflow-hidden"
      >
        <span class="w-full pb-3 border-b border-border text-primary-text font-medium text-lg">Écrire une publication</span>

        <form @submit.prevent="onSubmit" class="w-full h-full flex flex-col items-start justify-start gap-4">
          
          <!-- add a select for book -->

          <textarea
            v-model="newPost.content"
            placeholder="Écrivez votre publication ici..."
            class="bg-background border border-border rounded-2xl w-full h-40 px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
          ></textarea>

          <button 
            type="submit"
            :disabled="saving"
            class="bg-accent hover:bg-accent/90 rounded-2xl w-full px-4 py-3 text-box font-medium cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            <template v-if="saving">
              <svg class="animate-spin h-4 w-4 text-box" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>Publication...</span>
            </template>
            
            <span v-else>Publier</span>
          </button>
        </form>
      </div>
    </main>
  </div>
</template>


<script>
import AppHeader from '@/components/AppHeader.vue';
import MenuBar from '@/components/MenuBar.vue';
import PostItem from '@/components/PostItem.vue';
import { SquarePen } from 'lucide-vue-next';
import { useAuthStore } from '@/stores/authStore';
import postService from '@/services/postService';
import { ref, onMounted } from 'vue';

export default {
  name: 'Home',
  components: {
    AppHeader,
    MenuBar,
    PostItem,
    SquarePen,
  },
  setup() {
    const authStore = useAuthStore();

    const posts = ref([]);
    const loadingPosts = ref(false);
    const saving = ref(false);

    const newPost = ref({
      // add linked book
      content: '',
    });

    const formatDate = (value) => {
      if (!value) return '';

      return new Date(value).toLocaleString('fr-FR', {
        dateStyle: 'short',
        timeStyle: 'short',
      });
    };

    const loadPosts = async () => {
      loadingPosts.value = true;

      try {
        const res = await postService.list();
        posts.value = res.data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      } catch (err) {
        console.error('Erreur lors du chargement des publications :', err);
      } finally {
        loadingPosts.value = false;
      }
    };

    const onSubmit = async () => {
      if (!newPost.value.content) return;

      saving.value = true;

      try {
        const user = authStore.getUser;
        // user.id n'existe pas !
        // authStore.getUser ne renvoie que 'role' et 'username'
        const userId = user && user.id ? user.id : null;

        const payload = {
          content: newPost.value.content,
          userId,
        };

        const res = await postService.add(payload);

        posts.value.unshift(res.data);

        newPost.value = {
          title: '',
          content: '',
        };
      } catch (err) {
        console.error("Erreur lors de l'envoi de la publications :", err);
      } finally {
        saving.value = false;
      }
    };

    onMounted(() => {
      loadPosts();
    });

    return {
      posts,
      loadingPosts,
      saving,
      newPost,
      onSubmit,
      formatDate,
      authStore,
    };
  },
};
</script>