<template>
  <div class="min-h-screen bg-[#F5F1ED]">
    <HeaderComponent />
    <Sidebar />

    <div class="ml-44 pt-20 px-6 py-8">
      <!-- Loading State -->
      <div v-if="loading" class="flex justify-center items-center py-12">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-[#8B4513]"></div>
      </div>

      <div v-else class="max-w-4xl mx-auto">
        <!-- Back Button -->
        <button
          @click="$router.go(-1)"
          class="flex items-center gap-2 mb-6 text-[#8B4513] hover:text-[#6B3410] transition-colors"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
          </svg>
          Retour
        </button>

        <!-- Card -->
        <div class="bg-white rounded-xl shadow-lg p-8">
          <!-- Header -->
          <div class="mb-6">
            <h1 class="text-3xl font-bold text-gray-900 mb-1">Ajouter un livre</h1>
            <p class="text-gray-600">
              {{ form.title || 'Informations du livre' }}
            </p>
          </div>

          <!-- Messages -->
          <div v-if="error" class="mb-4 p-3 rounded-lg bg-red-50 border border-red-200 text-sm text-red-700">
            {{ error }}
          </div>

          <div v-if="successMessage" class="mb-4 p-3 rounded-lg bg-green-50 border border-green-200 text-sm text-green-700">
            {{ successMessage }}
          </div>

          <!-- Form -->
          <form @submit.prevent="submitForm" class="space-y-6">
            <!-- Titre -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Titre <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.title"
                type="text"
                class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#8B4513] focus:border-transparent"
                placeholder="Titre du livre"
                required
              />
            </div>

            <!-- Auteurs -->
            <div>
              <div class="flex items-center justify-between mb-2">
                <label class="block text-sm font-medium text-gray-700">
                  Auteurs
                </label>
                <button
                  type="button"
                  @click="addAuthorField"
                  class="text-xs px-2 py-1 rounded-full bg-orange-100 text-orange-700 hover:bg-orange-200"
                >
                  + Ajouter un auteur
                </button>
              </div>

              <div class="space-y-3">
                <div
                  v-for="(author, index) in form.authors"
                  :key="index"
                  class="flex gap-2 items-center"
                >
                  <input
                    v-model="author.firstName"
                    type="text"
                    class="flex-1 rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#8B4513] focus:border-transparent"
                    placeholder="Prénom"
                  />
                  <input
                    v-model="author.lastName"
                    type="text"
                    class="flex-1 rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#8B4513] focus:border-transparent"
                    placeholder="Nom"
                  />
                  <button
                    v-if="form.authors.length > 1"
                    type="button"
                    @click="removeAuthorField(index)"
                    class="text-gray-400 hover:text-red-500"
                  >
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>

            <!-- Sujet / Genre -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Sujet / Genre
              </label>
              <input
                v-model="form.subject"
                type="text"
                class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#8B4513] focus:border-transparent"
                placeholder="ex. Fantasy, Young Adult..."
              />
            </div>

            <!-- Infos techniques -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Nombre de pages
                </label>
                <input
                  v-model.number="form.totalPages"
                  type="number"
                  min="1"
                  class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#8B4513] focus:border-transparent"
                  placeholder="ex. 320"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  ISBN
                </label>
                <input
                  v-model="form.isbn"
                  type="text"
                  class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#8B4513] focus:border-transparent"
                  placeholder="ex. 978-2-1234-5678-9"
                  maxlength="13"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Année de publication
                </label>
                <input
                  v-model="form.publishedDate"
                  type="text"
                  class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#8B4513] focus:border-transparent"
                  placeholder="ex. 2021"
                />
              </div>
            </div>

            <!-- URL de couverture -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                URL de la couverture
              </label>
              <input
                v-model="form.cover"
                type="text"
                class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#8B4513] focus:border-transparent"
                placeholder="https://..."
              />
              <p class="mt-1 text-xs text-gray-500">
                Si vide, une image par défaut pourra être utilisée.
              </p>
            </div>

            <!-- Description -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Description
              </label>
              <textarea
                v-model="form.desc_txt"
                rows="5"
                class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#8B4513] focus:border-transparent"
                placeholder="Résumé ou description du livre"
              ></textarea>
            </div>

            <!-- Actions -->
            <div class="flex items-center justify-between pt-4 border-t border-gray-100">
              <button
                type="button"
                @click="$router.go(-1)"
                class="px-4 py-2 rounded-lg border border-gray-300 text-sm text-gray-700 hover:bg-gray-50"
              >
                Annuler
              </button>

              <button
                type="submit"
                :disabled="saving"
                class="inline-flex items-center gap-2 px-6 py-2 rounded-lg bg-[#8B4513] text-white text-sm font-medium hover:bg-[#6B3410] disabled:opacity-60 disabled:cursor-not-allowed"
              >
                <svg
                  v-if="saving"
                  class="w-4 h-4 animate-spin"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke-width="4"></circle>
                  <path
                    class="opacity-75"
                    stroke-linecap="round"
                    stroke-width="4"
                    d="M4 12a8 8 0 018-8"
                  ></path>
                </svg>
                <span>Enregistrer</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import HeaderComponent from '@/components/HeaderComponent.vue'
import Sidebar from '@/components/Sidebar.vue'
import booksService from '@/services/booksService'

export default {
  name: 'BookAdd',
  components: {
    HeaderComponent,
    Sidebar
  },
  data() {
    return {
      saving: false,
      error: null,
      successMessage: null,
      book: null,
      form: {
        title: '',
        authors: [
          { firstName: '', lastName: '' }
        ],
        subject: '',
        totalPages: null,
        isbn: '',
        publishedDate: '',
        desc_txt: '',
        cover: ''
      }
    }
  },

  methods: {
    addAuthorField() {
      this.form.authors.push({ firstName: '', lastName: '' })
    },

    removeAuthorField(index) {
      this.form.authors.splice(index, 1)
      if (this.form.authors.length === 0) {
        this.form.authors.push({ firstName: '', lastName: '' })
      }
    },

    async submitForm() {
      if (!this.form.title || !this.form.title.trim()) {
        this.error = 'Le titre est obligatoire.'
        return
      }

      this.error = null
      this.successMessage = null
      this.saving = true

      try {
        const cleanedAuthors = this.form.authors
          .filter(a => (a.firstName && a.firstName.trim()) || (a.lastName && a.lastName.trim()))
          .map(a => ({
            firstName: a.firstName.trim(),
            lastName: a.lastName.trim()
          }))

        const createdBook = {
          ...this.book,
          title: this.form.title,
          authors: cleanedAuthors,
          subject: this.form.subject,
          totalPages: this.form.totalPages,
          isbn: this.form.isbn,
          publishedDate: this.form.publishedDate,
          desc_txt: this.form.desc_txt,
          cover: this.form.cover
        }

        // À adapter selon ton service (par ex. updateBook(id, payload))
        await booksService.createBook(createdBook)
        this.successMessage = 'Les informations du livre ont bien été enregistrées.'
      } catch (error) {
        console.error('Erreur lors de la création du livre :', error)
        this.error = "Impossible d'ajouter le livre. Vérifiez que le serveur est démarré."
      } finally {
        this.saving = false
      }
    }
  }
}
</script>
