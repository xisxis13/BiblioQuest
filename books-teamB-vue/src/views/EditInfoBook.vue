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
            <!-- Titre (BOOK) -->
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

            <!-- ISBN (EDITION) -->
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
                maxlength="13"
                class="input"
                :class="{ 'input-error': isbnError }"
                placeholder="ISBN"
                autocomplete="off"
              />
              <p v-if="isbnError" class="mt-1 text-sm text-red-600">{{ isbnError }}</p>
            </div>

            <!-- Résumé (BOOK.desc_txt) -->
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

            <!-- Série (BOOK.serie_id, dropdown sur SERIES) -->
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

            <!-- Position dans la série (BOOK.serie_pos) -->
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

            <!-- Nombre de pages (EDITION.number_of_pages) -->
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

            <!-- Format physique (EDITION.physical_format) -->
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

            <!-- Sujet (BOOK.subject_id, dropdown sur SUBJECTS) -->
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

            <!-- Année / date de publication (EDITION.publish_date, readonly ou pas selon ton choix) -->
            <div>
              <label for="publicationYear" class="block text-sm font-medium text-gray-700 mb-2">
                Année de publication
              </label>
              <input
                id="publicationYear"
                v-model="form.publicationYear"
                type="text"
                class="input bg-gray-100 cursor-not-allowed"
                :class="{ 'input-error': publicationYearError }"
                readonly
              />
              <p v-if="publicationYearError" class="mt-1 text-sm text-red-600">
                {{ publicationYearError }}
              </p>
            </div>

            <!-- Image de couverture (on suppose stockée côté BOOK.cover) -->
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
        coverImageFile: null,
        coverImageUrl: null,

        // EDITION
        editionId: null,        // on la récupère pour le PUT
        isbn: '',
        pageCount: '',
        physicalFormat: '',
        publicationYear: '',
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

      series: [],    // pour le dropdown série
      subjects: [],  // pour le dropdown sujet

      // erreurs
      titleError: '',
      isbnError: '',
      summaryError: '',
      publicationYearError: '',
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
      this.publicationYearError = ''
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

      if (!this.form.publicationYear) {
        this.publicationYearError = "L'année de publication est requise"
        return false
      }

      return true
    },

    async fetchSubjects() {
      const res = await axios.get('/api/subjects')
      this.subjects = res.data
    },

    async fetchSeries() {
      const res = await axios.get('/api/series')
      this.series = res.data
    },

    async fetchBook() {
      const id = this.route.params.id
      const res = await axios.get(`/api/books/${id}`)
      const book = res.data

      // BOOK -> form
      this.form.title = book.title
      this.form.summary = book.desc_txt || ''
      this.form.serieId = book.serie_id || ''
      this.form.seriePos = book.serie_pos || ''
      this.form.subjectId = book.subject ? book.subject.id : ''

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
      this.form.pageCount = edition.number_of_pages ? edition.number_of_pages.toString() : ''
      this.form.physicalFormat = edition.physical_format || ''
      this.form.publicationYear = edition.publish_date || ''

      // si tu décides que la cover est sur Edition et pas Book :
      // if (!this.form.coverImageUrl && edition.cover) {
      //   this.form.coverImageUrl = edition.cover
      // }
    },

    async handleEdit() {
      if (!this.validateForm()) return

      try {
        const bookId = this.route.params.id

        // ---------- UPDATE BOOK ----------
        const bookFormData = new FormData()
        bookFormData.append('title', this.form.title)
        bookFormData.append('desc_txt', this.form.summary)
        bookFormData.append('serie_id', this.form.serieId)
        bookFormData.append('serie_pos', this.form.seriePos)
        bookFormData.append('subjectId', this.form.subjectId)

        if (this.form.coverImageFile) {
          bookFormData.append('coverFile', this.form.coverImageFile)
        }

        await axios.put(`/api/books/${bookId}`, bookFormData, {
          headers: {
            'Content-Type': 'multipart/form-data',
            Authorization: `Bearer ${this.authStore.token}`,
          },
        })

        // ---------- UPDATE EDITION ----------
        if (!this.form.editionId) {
          // au cas où aucune édition n'aurait été trouvée
          this.globalError = "Impossible de trouver l'édition associée à ce livre."
          return
        }

        const editionPayload = {
          isbn: this.form.isbn,
          number_of_pages: this.form.pageCount,
          physical_format: this.form.physicalFormat,
          publish_date: this.form.publicationYear,
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

    // Rediriger si pas connecté
    //if (!this.authStore.isAuthenticated) {
    //  this.router.push('/login')
    //  return
    //}

    // Correction de la condition: && pour "ni bibliothécaire ni admin"
    //if (
    //  this.authStore.getUser.role !== 'BIBLIOTHECAIRE' &&
    //  this.authStore.getUser.role !== 'ADMIN'
    //) {
    //  this.router.push('/login')
    //  return
    //}

    try {
      await Promise.all([
        this.fetchSubjects(),
        this.fetchSeries(),
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
