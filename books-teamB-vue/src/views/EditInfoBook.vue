<template>
  <div class="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 animate-fadeIn">
      <!-- En-tête avec logo -->
      <div class="text-center">
        <div class="flex justify-center mb-6">
          <img src="@/assets/BCO.png" alt="Logo" class="logo-lg" />
        </div>
        <h2 class="text-3xl font-bold text-gray-900 mb-2">Modifier</h2>
        <p class="text-gray-600">Modifier les informations du livre</p>
      </div>

      <!-- Formulaire -->
      <div class="card">
        <div class="card-body">
          <form @submit.prevent="handleLogin" class="space-y-6">
            <!-- Titre -->
            <div>
              <label for="title" class="block text-sm font-medium text-gray-700 mb-2">
                Titre
              </label>
              <input
                id="title"
                v-model="form.title"
                type="text"
                required
                class="input"
                :class="{ 'input-error': titleError }"
                placeholder="Titre du livre"
                autocomplete="current-title"
              />
              <p v-if="titleError" class="mt-1 text-sm text-red-600">{{ titleError }}</p>
            </div>

            <!-- isbn -->
            <div>
              <label for="isbn" class="block text-sm font-medium text-gray-700 mb-2">
                Isbn
              </label>
              <input
                id="isbn"
                v-model="form.isbn"
                type="text"
                inputmode="numeric"
                pattern="[0-9]*"
                required
                maxlength="13"
                class="input"
                :class="{ 'input-error': isbnError }"
                placeholder="isbn"
                autocomplete="current-isbn"
              />
              <p v-if="isbnError" class="mt-1 text-sm text-red-600">{{ isbnError }}</p>
            </div>

            <!-- Summary -->
            <div>
              <label for="summary" class="block text-sm font-medium text-gray-700 mb-2">
                Résumé
              </label>
              <textarea
                id="summary"
                v-model="form.summary"
                required
                rows="5"
                class="input"
                :class="{ 'input-error': summaryError }"
                placeholder="Résumé du livre"
                autocomplete="current-summary"
              ></textarea>

              <p v-if="summaryError" class="mt-1 text-sm text-red-600">
                {{ summaryError }}
              </p>
            </div>


            <!-- PublicationYear -->
            <div>
              <label for="publicationYear" class="block text-sm font-medium text-gray-700 mb-2">
                Année de publication
              </label>
              <input
                id="publicationYear"
                v-model="form.publicationYear"
                type="text"
                required
                readonly
                class="input bg-gray-100 cursor-not-allowed"
                :class="{ 'input-error': publicationYearError }"
                placeholder="form.publicationYear"
              />
              <p v-if="publicationYearError" class="mt-1 text-sm text-red-600">
                {{ publicationYearError }}
              </p>
            </div>


            <!-- CoverImage -->
            <div>
              <label for="coverImage" class="block text-sm font-medium text-gray-700 mb-2">
                Image de couverture du livre
              </label>

              <input
                id="coverImage"
                type="file"
                accept="image/*"
                required
                class="input"
                :class="{ 'input-error': coverImageUrlError }"
                @change="onCoverImageChange"
              />

              <!-- Aperçu de l'image (optionnel) -->
              <div v-if="form.coverImageUrl" class="mt-2">
                <img
                  :src="form.coverImageUrl"
                  alt="Aperçu de la couverture"
                  class="h-32 object-cover rounded"
                />
              </div>

              <p v-if="coverImageUrlError" class="mt-1 text-sm text-red-600">
                {{ coverImageUrlError }}
              </p>
            </div>


            <!-- Message d'erreur global -->
            <div v-if="authStore.getError" class="alert alert-error">
              {{ authStore.getError }}
            </div>

            <!-- Bouton de connexion -->
            <button
              type="submit"
              :disabled="authStore.edit"
              class="btn btn-primary w-full"
            >
            Modifier
            </button>
          </form>

        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'

export default {
  name: 'EditInfoBookPage',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()

    return {
      authStore,
      router
    }
  },
  data() {
    return {
      form: {
        title: '',
        isbn: '',
        summary: '',
        publicationYear: '',
        coverImageFile: null,
        coverImageUrl: null,
      },
      titleError: '',
      isbnError: '',
      summaryError: '',
      publicationYearError: '',
      coverImageUrlError: '',
    }
  },
  methods: {
    onCoverImageChange(event) {
      const file = event.target.files[0];

      if (!file) {
        this.form.coverImageFile = null;
        this.form.coverImageUrl = null;
        return;
      }

      this.coverImageUrlError = '';
      this.form.coverImageFile = file;

      // Pour afficher un aperçu
      this.form.coverImageUrl = URL.createObjectURL(file);
    },

    validateForm() {
      this.titleError = ''
      this.isbnError = ''
      this.summaryError = ''
      this.publicationYearError = ''
      this.coverImageUrlError = ''


      if (!this.form.title) {
        this.titleError = 'Le titre est requis'
        return false
      }


      if (!this.form.isbn) {
        this.passwordError = "L'isbn est requis"
        return false
      }

      if (!this.form.summary) {
        this.summaryError = "Le résumé est requis"
        return false
      }

      if (!this.form.publicationYear) {
        this.publicationYearError = "L'année de publication est requise"
        return false
      }

      if (!this.form.coverImageUrl) {
        this.coverImageUrlError = "L'image de couverture est requise"
        return false
      }

      return true
    },
  },

  mounted() {
    // Initialiser l'authentification au montage
    this.authStore.initializeAuth()

    // Rediriger si pas connecté       A REMETTRE APRES LES TESTS !!!!!!
    //if (!this.authStore.isAuthenticated) {
    //  this.router.push('/login')
    //}

    const now = new Date();

    const pad = (n) => n.toString().padStart(2, '0');

    const fullDate =
      now.getFullYear() + '-' +
      pad(now.getMonth() + 1) + '-' +
      pad(now.getDate()) + ' ' +
      pad(now.getHours()) + ':' +
      pad(now.getMinutes()) + ':' +
      pad(now.getSeconds());

    this.form.publicationYear = fullDate;
  }
}
</script>
