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
          <form @submit.prevent="handleEdit" class="space-y-6">
            <!-- Titre -->
            <div>
              <label for="title" class="block text-sm font-medium text-gray-700 mb-2">
                Titre
              </label>
              <input
                id="title"
                v-model="form.title"
                type="text"
                class="input"
                :class="{ 'input-error': titleError }"
                placeholder="Titre du livre"
                autocomplete="off"
              />
              <p v-if="titleError" class="mt-1 text-sm text-red-600">
                {{ titleError }}
              </p>
            </div>

            <!-- Auteurs -->
            <div>
              <label for="authors" class="block text-sm font-medium text-gray-700 mb-2">
                Auteurs
              </label>
              <select
                id="authors"
                v-model="form.authorIds"
                multiple
                class="input"
              >
                <option
                  v-for="author in authors"
                  :key="author.id"
                  :value="author.id"
                >
                  {{ author.firstName }} {{ author.lastName }}
                </option>
              </select>
              <p class="mt-1 text-xs text-gray-500">
                Maintenez Ctrl/Cmd pour sélectionner plusieurs auteurs
              </p>
            </div>

            <!-- ISBN -->
            <div>
              <label for="isbn" class="block text-sm font-medium text-gray-700 mb-2">
                ISBN
              </label>
              <input
                id="isbn"
                v-model="form.isbn"
                type="text"
                inputmode="numeric"
                maxlength="13"
                class="input"
                :class="{ 'input-error': isbnError }"
                placeholder="ISBN"
                autocomplete="off"
              />
              <p v-if="isbnError" class="mt-1 text-sm text-red-600">
                {{ isbnError }}
              </p>
            </div>

            <!-- Résumé -->
            <div>
              <label for="summary" class="block text-sm font-medium text-gray-700 mb-2">
                Résumé
              </label>
              <textarea
                id="summary"
                v-model="form.summary"
                rows="5"
                class="input"
                :class="{ 'input-error': summaryError }"
                placeholder="Résumé du livre"
              ></textarea>
              <p v-if="summaryError" class="mt-1 text-sm text-red-600">
                {{ summaryError }}
              </p>
            </div>

            <!-- Série -->
            <div>
              <label for="serie" class="block text-sm font-medium text-gray-700 mb-2">
                Série
              </label>
              <select
                id="serie"
                v-model="form.serieId"
                class="input"
                :class="{ 'input-error': serieIdError }"
              >
                <option disabled value="">Sélectionner une série</option>
                <option
                  v-for="serie in series"
                  :key="serie.id"
                  :value="serie.id"
                >
                  {{ serie.text }}
                </option>
              </select>
              <p v-if="serieIdError" class="mt-1 text-sm text-red-600">
                {{ serieIdError }}
              </p>
            </div>

            <!-- Position dans la série -->
            <div>
              <label for="seriePos" class="block text-sm font-medium text-gray-700 mb-2">
                Position dans la série
              </label>
              <input
                id="seriePos"
                v-model="form.seriePos"
                type="text"
                maxlength="2"
                class="input"
                :class="{ 'input-error': seriePosError }"
                placeholder="1, 2, 10..."
              />
              <p v-if="seriePosError" class="mt-1 text-sm text-red-600">
                {{ seriePosError }}
              </p>
            </div>

            <!-- Nombre de pages (Edition.pages + Book.totalPages) -->
            <div>
              <label for="pageCount" class="block text-sm font-medium text-gray-700 mb-2">
                Nombre de pages
              </label>
              <input
                id="pageCount"
                v-model="form.pageCount"
                type="text"
                inputmode="numeric"
                maxlength="4"
                class="input"
                :class="{ 'input-error': pageCountError }"
                placeholder="Ex: 352"
              />
              <p v-if="pageCountError" class="mt-1 text-sm text-red-600">
                {{ pageCountError }}
              </p>
            </div>

            <!-- Sujet -->
            <div>
              <label for="subject" class="block text-sm font-medium text-gray-700 mb-2">
                Sujet
              </label>
              <select
                id="subject"
                v-model="form.subjectId"
                class="input"
                :class="{ 'input-error': subjectError }"
              >
                <option disabled value="">Sélectionner un sujet</option>
                <option
                  v-for="subject in subjects"
                  :key="subject.id"
                  :value="subject.id"
                >
                  {{ subject.text }}
                </option>
              </select>
              <p v-if="subjectError" class="mt-1 text-sm text-red-600">
                {{ subjectError }}
              </p>
            </div>

            <!-- Date de publication (Edition.publish_date) -->
            <div>
              <label for="publishDate" class="block text-sm font-medium text-gray-700 mb-2">
                Date de publication
              </label>
              <input
                id="publishDate"
                v-model="form.publishDate"
                type="date"
                class="input"
                :class="{ 'input-error': publishDateError }"
              />
              <p v-if="publishDateError" class="mt-1 text-sm text-red-600">
                {{ publishDateError }}
              </p>
            </div>

            <!-- Image de couverture (Book.cover + Edition.cover) -->
            <div>
              <label for="coverImage" class="block text-sm font-medium text-gray-700 mb-2">
                Image de couverture du livre
              </label>
              <input
                id="coverImage"
                type="file"
                accept="image/*"
                class="input"
                :class="{ 'input-error': coverImageUrlError }"
                @change="onCoverImageChange"
              />
              <!-- Aperçu de l'image -->
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
            <div v-if="globalError" class="alert alert-error">
              {{ globalError }}
            </div>

            <!-- Bouton -->
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
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

export default {
  name: 'EditInfoBookPage',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    const route = useRoute()

    return {
      authStore,
      router,
      route,
    }
  },
  data() {
    return {
      // Formulaire = mélange BOOK + EDITION
      form: {
        // BOOK
        title: '',
        summary: '',
        serieId: '',
        seriePos: '',
        subjectId: '',
        authorIds: [],
        coverImageFile: null,
        coverImageUrl: null,
        // EDITION
        pageCount: '',
        publishDate: '',
        isbn: '',
      },
      // listes pour les selects
      series: [],   // TABLE Serie
      subjects: [], // TABLE Subject
      authors: [],  // TABLE Author
      // erreurs
      titleError: '',
      isbnError: '',
      summaryError: '',
      publishDateError: '',
      coverImageUrlError: '',
      serieIdError: '',
      seriePosError: '',
      pageCountError: '',
      subjectError: '',
      globalError: '',
    }
  },
  methods: {
    onCoverImageChange(event) {
      const file = event.target.files[0]

      if (!file) {
        this.form.coverImageFile = null
        this.form.coverImageUrl = null
        return
      }

      this.coverImageUrlError = ''
      this.form.coverImageFile = file
      this.form.coverImageUrl = URL.createObjectURL(file)
    },

    resetErrors() {
      this.titleError = ''
      this.isbnError = ''
      this.summaryError = ''
      this.publishDateError = ''
      this.coverImageUrlError = ''
      this.serieIdError = ''
      this.seriePosError = ''
      this.pageCountError = ''
      this.subjectError = ''
      this.globalError = ''
    },

    validateForm() {
      this.resetErrors()

      if (!this.form.title) {
        this.titleError = 'Le titre est requis'
        return false
      }

      if (!this.form.isbn) {
        this.isbnError = "L'ISBN est requis"
        return false
      }

      if (!this.form.summary) {
        this.summaryError = 'Le résumé est requis'
        return false
      }

      if (!this.form.serieId) {
        this.serieIdError = 'La série est requise'
        return false
      }

      if (!this.form.seriePos) {
        this.seriePosError = 'La position dans la série est requise'
        return false
      }

      if (!this.form.pageCount) {
        this.pageCountError = 'Le nombre de pages est requis'
        return false
      }

      if (!this.form.subjectId) {
        this.subjectError = 'Le sujet est requis'
        return false
      }

      if (!this.form.publishDate) {
        this.publishDateError = 'La date de publication est requise'
        return false
      }

      return true
    },

    async fetchSubjects() {
      const res = await axios.get('/api/subject')
      this.subjects = res.data
    },

    async fetchSeries() {
      const res = await axios.get('/api/serie')
      this.series = res.data
    },

    async fetchAuthors() {
      const res = await axios.get('/api/authors')
      this.authors = res.data
    },

    async fetchBook() {
      const id = this.route.params.id

      const res = await axios.get(`/api/books/${id}`, {
        headers: {
          'X-Username': this.authStore.getUser?.username || '',
        },
      })

      const book = res.data

      // Remplir le formulaire à partir du BOOK
      this.form.title = book.title
      this.form.summary = book.desc_txt || ''
      this.form.serieId = book.serie_id || ''
      this.form.seriePos = book.serie_pos || ''
      this.form.subjectId = book.subject ? book.subject.id : ''
      this.form.authorIds = book.authors ? book.authors.map(a => a.id) : []
      this.form.isbn = book.isbn || ''

      if (book.totalPages != null) {
        this.form.pageCount = String(book.totalPages)
      }

      if (book.cover) {
        this.form.coverImageUrl = book.cover
      }
    },

    async handleEdit() {
      if (!this.validateForm()) return

      try {
        const bookId = this.route.params.id

        // ---------- UPDATE BOOK via /api/books/{id} ----------
        const bookPayload = {
          id: Number(bookId),
          title: this.form.title,
          desc_txt: this.form.summary,
          serie_id: this.form.serieId,
          serie_pos: this.form.seriePos,
          cover: this.form.coverImageUrl,
          totalPages: this.form.pageCount ? Number(this.form.pageCount) : null,
          isbn: this.form.isbn,
          authors: this.form.authorIds.map(id => ({ id })),
          subject: this.form.subjectId ? { id: this.form.subjectId } : null,
        }

        await axios.put(`/api/books/${bookId}`, bookPayload, {
          headers: {
            'Content-Type': 'application/json',
            'X-Username': this.authStore.getUser?.username || '',
          },
        })

        // ---------- CREATE EDITION liée au livre ----------
        const editionPayload = {
          isbn: this.form.isbn,
          publish_date: this.form.publishDate,
          pages: Number(this.form.pageCount),
          cover: this.form.coverImageUrl,
          book: { id: Number(bookId) },
        }

        await axios.post('/api/editions', editionPayload, {
          headers: {
            'Content-Type': 'application/json',
            'X-Username': this.authStore.getUser?.username || '',
          },
        })

        // Redirection vers la page détail
        this.router.push(`/books/${bookId}`)
      } catch (e) {
        console.error(e)
        this.globalError = 'Erreur lors de la modification du livre et de la création de son édition.'
      }
    },
  },

  async mounted() {
    // Initialiser l'authentification au montage
    if (this.authStore && typeof this.authStore.initializeAuth === 'function') {
      this.authStore.initializeAuth()
    }

    // Vérifier le rôle ici si besoin (ADMIN / BIBLIOTHECAIRE)

    try {
      await Promise.all([
        this.fetchSubjects(),
        this.fetchSeries(),
        this.fetchAuthors(),
        this.fetchBook(),
      ])
    } catch (e) {
      console.error(e)
      this.globalError = 'Erreur lors du chargement des données du livre.'
    }
  },
}
</script>
