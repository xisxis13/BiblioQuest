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
                required
                class="input"
                :class="{ 'input-error': titleError }"
                placeholder="Titre du livre"
                autocomplete="off"
              />
              <p v-if="titleError" class="mt-1 text-sm text-red-600">{{ titleError }}</p>
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

            <!-- ISBN (10) -->
            <div>
              <label for="isbn" class="block text-sm font-medium text-gray-700 mb-2">
                ISBN
              </label>
              <input
                id="isbn"
                v-model="form.isbn"
                type="text"
                inputmode="numeric"
                pattern="[0-9]*"
                maxlength="10"
                class="input"
                :class="{ 'input-error': isbnError }"
                placeholder="ISBN"
                autocomplete="off"
              />
              <p v-if="isbnError" class="mt-1 text-sm text-red-600">{{ isbnError }}</p>
            </div>

            <!-- ISBN-13 -->
            <div>
              <label for="isbn13" class="block text-sm font-medium text-gray-700 mb-2">
                ISBN-13
              </label>
              <input
                id="isbn13"
                v-model="form.isbn13"
                type="text"
                inputmode="numeric"
                maxlength="13"
                class="input"
                placeholder="ISBN-13"
                autocomplete="off"
              />
            </div>

            <!-- Résumé -->
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
                  {{ serie.name }}
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

            <!-- Nombre de pages -->
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

            <!-- Format physique -->
            <div>
              <label for="physicalFormat" class="block text-sm font-medium text-gray-700 mb-2">
                Format physique
              </label>
              <select
                id="physicalFormat"
                v-model="form.physicalFormat"
                class="input"
                :class="{ 'input-error': physicalFormatError }"
              >
                <option disabled value="">Sélectionner un format</option>
                <option
                  v-for="opt in physicalFormatOptions"
                  :key="opt"
                  :value="opt"
                >
                  {{ opt }}
                </option>
              </select>
              <p v-if="physicalFormatError" class="mt-1 text-sm text-red-600">
                {{ physicalFormatError }}
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

            <!-- Date de publication -->
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

            <!-- Image de couverture BOOK.cover-->
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

            <!-- Message d'erreur global éventuel -->
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
        authorIds: [],           // BOOK.authors
        coverImageFile: null,
        coverImageUrl: null,

        // EDITION
        editionId: null,
        isbn: '',
        isbn13: '',
        pageCount: '',
        physicalFormat: '',
        publishDate: '',
      },

      physicalFormatOptions: [
        'Hardcover',
        'Paperback',
        'Library binding',
        'Audio CD',
        'E-Book',
        'MP3 CD',
        'Audiobook MP3',
        'Audiobook CD',
        'Digital Audiobook',
      ],

      series: [],    // dropdown séries (TABLE Serie)
      subjects: [],  // dropdown sujets (TABLE Subject)
      authors: [],   // dropdown auteurs (TABLE Author)

      // erreurs
      titleError: '',
      isbnError: '',
      summaryError: '',
      publishDateError: '',
      coverImageUrlError: '',
      serieIdError: '',
      seriePosError: '',
      pageCountError: '',
      physicalFormatError: '',
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

    validateForm() {
      // reset errors
      this.titleError = ''
      this.isbnError = ''
      this.summaryError = ''
      this.publishDateError = ''
      this.coverImageUrlError = ''
      this.serieIdError = ''
      this.seriePosError = ''
      this.pageCountError = ''
      this.physicalFormatError = ''
      this.subjectError = ''
      this.globalError = ''

      if (!this.form.title) {
        this.titleError = 'Le titre est requis'
        return false
      }

      if (!this.form.summary) {
        this.summaryError = 'Le résumé est requis'
        return false
      }

      if (!this.form.isbn) {
        this.isbnError = "L'ISBN est requis"
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

      if (!this.form.physicalFormat) {
        this.physicalFormatError = 'Le format physique est requis'
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
      const res = await axios.get(`/api/book/${id}`)
      const book = res.data

      // Formulaire
      this.form.title = book.title
      this.form.summary = book.desc_txt || ''
      this.form.serieId = book.serie_id || ''
      this.form.seriePos = book.serie_pos || ''
      this.form.subjectId = book.subject ? book.subject.id : ''

      // auteurs
      this.form.authorIds = book.authors ? book.authors.map(a => a.id) : []

      // cover si présente
      if (book.cover) {
        this.form.coverImageUrl = book.cover
      }
    },

    async fetchEdition() {
      const bookId = this.route.params.id
      // endpoint à adapter: par ex /api/editions/by-book/{bookId}
      const res = await axios.get(`/api/editions/by-book/${bookId}`)
      const edition = res.data

      this.form.editionId = edition.id
      this.form.isbn = edition.isbn || ''
      this.form.isbn13 = edition.isbn13 || ''
      this.form.pageCount = edition.numberOfPages
        ? edition.numberOfPages.toString()
        : ''
      this.form.physicalFormat = edition.physicalFormat || ''
      this.form.publishDate = edition.publishDate || ''
    },

    async handleEdit() {
      if (!this.validateForm()) return

      try {
        const bookId = this.route.params.id

        //UPDATE BOOK
        const bookFormData = new FormData()
        bookFormData.append('title', this.form.title)
        bookFormData.append('desc_txt', this.form.summary)
        bookFormData.append('serie_id', this.form.serieId)
        bookFormData.append('serie_pos', this.form.seriePos)
        bookFormData.append('subjectId', this.form.subjectId)

        // auteurs -> ids envoyés au backend
        if (this.form.authorIds && this.form.authorIds.length > 0) {
          this.form.authorIds.forEach(id => {
            bookFormData.append('authorIds', id)
          })
        }

        if (this.form.coverImageFile) {
          bookFormData.append('coverFile', this.form.coverImageFile)
        }

        await axios.put(`/api/books/${bookId}`, bookFormData, {
          headers: {
            'Content-Type': 'multipart/form-data',
            Authorization: `Bearer ${this.authStore.token}`,
          },
        })

        //UPDATE EDITION
        if (!this.form.editionId) {
          this.globalError = "Impossible de trouver l'édition associée à ce livre."
          return
        }

        const editionPayload = {
          // Champs alignés sur l'entité Edition
          isbn: this.form.isbn,
          isbn13: this.form.isbn13,
          numberOfPages: this.form.pageCount,
          physicalFormat: this.form.physicalFormat,
          publishDate: this.form.publishDate,
          bookId: bookId,
        }

        await axios.put(`/api/editions/${this.form.editionId}`, editionPayload, {
          headers: {
            Authorization: `Bearer ${this.authStore.token}`,
          },
        })

        // Redirection vers la page de détail du livre
        this.router.push(`/books/${bookId}`)
      } catch (e) {
        console.error(e)
        this.globalError = 'Erreur lors de la modification du livre et de son édition.'
      }
    },
  },

  async mounted() {
    // Initialiser l'authentification au montage
    this.authStore.initializeAuth()

    // if (!this.authStore.isAuthenticated) {
    //   this.router.push('/login')
    //   return
    // }
    //
    // if (
    //   this.authStore.getUser.role !== 'BIBLIOTHECAIRE' &&
    //   this.authStore.getUser.role !== 'ADMIN'
    // ) {
    //   this.router.push('/login')
    //   return
    // }

    try {
      await Promise.all([
        this.fetchSubjects(),
        this.fetchSeries(),
        this.fetchAuthors(),
        this.fetchBook(),
        this.fetchEdition(),
      ])
    } catch (e) {
      console.error(e)
      this.globalError = 'Erreur lors du chargement des données du livre.'
    }
  },
}
</script>
