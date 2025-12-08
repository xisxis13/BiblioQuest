<template>
  <div class="flex flex-col gap-3 p-3 bg-background w-full h-screen">
    <AppHeader />

    <main class="w-full flex-1 min-h-0 flex gap-3">
      <MenuBar />

      <div class="bg-box border border-border rounded-3xl flex-1 h-full overflow-y-auto flex flex-col gap-4 p-6">
        <button
          @click="$router.go(-1)"
          class="w-fit flex items-center gap-2 text-secondary-text hover:text-primary-text transition-colors cursor-pointer"
        >
          <ChevronLeft :size="22" />
          Retour
        </button>

        <div class="w-full h-fit flex flex-col gap-1">
          <span class="text-primary-text font-semibold text-2xl">{{ isEditMode ? 'Modifier un livre' : 'Ajouter un livre' }}</span>
          <span class="text-secondary-text font-normal text-base">Informations du livre</span>
        </div>

        <!-- Error message -->
        <div
          v-if="error"
          class="bg-red-50 border border-red-300 rounded-2xl w-full p-3 flex items-center justify-start gap-2"
        >
          <Info class="text-red-600 " :size="20" />
          <span class="text-red-800 text-sm">{{ error }}</span>
        </div>

        <!-- Success message -->
        <div
          v-if="successMessage"
          class="bg-green-50 border border-green-400 rounded-2xl w-full p-3 flex items-center justify-start gap-2"
        >
          <CircleCheck class="text-green-600 " :size="20" />
          <span class="text-green-800 text-sm">{{ successMessage }}</span>
        </div>

        <form @submit.prevent="submitForm" class="w-full h-fit flex flex-col items-start justify-start gap-4">
          <!-- Title -->
          <div class="w-full flex flex-col gap-1.5 items-start justify-center">
            <label class="text-primary-text font-semibold text-lg text-center">Titre <span class="text-red-500">*</span></label>
            <input
              v-model="form.title"
              type="text"
              class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
              placeholder="Titre du livre"
              required
            />
          </div>

          <!-- Author -->
          <div class="w-full flex flex-col gap-1.5 items-start justify-center">
            <div class="w-full flex items-center justify-between">
              <label class="text-primary-text font-semibold text-lg text-center">Auteur</label>

              <button
                type="button"
                @click="addAuthorField"
                class="bg-accent hover:bg-accent/90 rounded-2xl w-fit px-3 py-2 text-box font-normal text-xs cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-1"
              >
                <Plus :size="16" />
                <span>Ajouter un auteur</span>
              </button>
            </div>
            <div
              v-for="(author, index) in form.authors"
              :key="index"
              class="w-full flex gap-3 items-center justify-center"
            >
              <input
                v-model="author.firstName"
                type="text"
                class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
                placeholder="Prénom"
              />
              <input
                v-model="author.lastName"
                type="text"
                class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
                placeholder="Nom"
              />
              <button
                v-if="form.authors.length > 1"
                type="button"
                @click="removeAuthorField(index)"
                class="text-secondary-text hover:text-red-500 cursor-pointer"
              >
                <X :size="22" />
              </button>
            </div>
          </div>

          <!-- Subject / Genre -->
          <div class="w-full flex flex-col gap-1.5 items-start justify-center">
            <label class="text-primary-text font-semibold text-lg text-center">Sujet / Genre</label>
            <input
              v-model="form.subject"
              type="text"
              class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
              placeholder="ex. Fantasy, Young Adult..."
            />
          </div>

          <!-- Technical informations -->
          <div class="w-full flex gap-3 items-center justify-center">
            <div class="w-full flex flex-col gap-1.5 items-start justify-center">
              <label class="text-primary-text font-semibold text-lg text-center">Nombre de pages</label>
              <input
                v-model="form.totalPages"
                type="number"
                min="1"
                class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
                placeholder="ex. 320"
              />
            </div>
            <div class="w-full flex flex-col gap-1.5 items-start justify-center">
              <label class="text-primary-text font-semibold text-lg text-center">ISBN</label>
              <input
                v-model="form.isbn"
                type="text"
                class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
                placeholder="ex. 978-2-1234-5678-9"
              />
            </div>
            <div class="w-full flex flex-col gap-1.5 items-start justify-center">
              <label class="text-primary-text font-semibold text-lg text-center">Année de publication</label>
              <input
                v-model="form.publishedDate"
                type="text"
                class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
                placeholder="ex. 2021"
              />
            </div>
          </div>

          <!-- Cover URL -->
          <div class="w-full flex flex-col gap-1.5 items-start justify-center">
            <label class="text-primary-text font-semibold text-lg text-center">URL de la couverture</label>
            <input
              v-model="form.cover"
              type="text"
              class="bg-background border border-border rounded-2xl w-full px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
              placeholder="https://..."
            />
            <span class="text-secondary-text font-normal text-sm">Si vide, une image par défaut pourra être utilisée.</span>
          </div>

          <!-- Description -->
          <div class="w-full flex flex-col gap-1.5 items-start justify-center">
            <label class="text-primary-text font-semibold text-lg text-center">Description</label>
            <textarea
              v-model="form.desc_txt"
              rows="5"
              class="bg-background border border-border rounded-2xl w-full h-40 px-4 py-3 text-sm placeholder:text-secondary-text focus:outline-none focus:ring-1 focus:ring-accent"
              placeholder="Résumé ou description du livre"
            ></textarea>
          </div>

          <!-- Action buttons -->
          <div class="w-full flex items-center justify-between">
            <button
              @click="$router.go(-1)"
              type="button"
              class="bg-box hover:bg-background border border-border rounded-2xl px-4 py-2 cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 whitespace-nowrap"
            >
              <span class="text-primary-text font-normal">Annuler</span>
            </button>

             <button
              type="submit"
              :disabled="saving"
              class="bg-accent hover:bg-accent/90 rounded-2xl px-4 py-2 text-box font-normal cursor-pointer transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              <template v-if="saving">
                <svg class="animate-spin h-4 w-4 text-box" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <span>Enregistrement...</span>
              </template>

              <span v-else>{{ isEditMode ? 'Modifier' : 'Enregistrer' }}</span>
            </button>
          </div>
        </form>
      </div>
    </main>
  </div>
</template>

<script>
import AppHeader from '@/components/AppHeader.vue'
import MenuBar from '@/components/MenuBar.vue'
import bookService from '@/services/bookService'
import { ChevronLeft, Info, CircleCheck, Plus, X } from 'lucide-vue-next'

export default {
  name: 'EditBook',
  components: {
    AppHeader,
    MenuBar,
    ChevronLeft,
    Info,
    CircleCheck,
    Plus,
    X,
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

  computed: {
    isEditMode() {
      return !!this.$route.params.id
    }
  },

  async mounted() {
    if (this.isEditMode) {
      await this.loadBook(this.$route.params.id)
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

    async loadBook(id) {
      try {
        const book = await bookService.getBookById(id)
        this.book = book

        // Populate form
        this.form.title = book.title
        this.form.subject = book.subject ? book.subject.text : ''
        this.form.totalPages = book.totalPages
        this.form.isbn = book.isbn
        this.form.publishedDate = book.publishedDate
        this.form.desc_txt = book.desc_txt
        this.form.cover = book.cover

        // Handle authors
        if (book.authors && book.authors.length > 0) {
          this.form.authors = book.authors.map(a => ({
            firstName: a.firstName,
            lastName: a.lastName
          }))
        }
      } catch (error) {
        console.error('Error loading book:', error)
        this.error = "Impossible de charger les informations du livre."
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

        const subject =
          this.form.subject && this.form.subject.trim()
            ? { text: this.form.subject.trim() }
            : null

        const createdBook = {
          title: this.form.title.trim(),
          authors: cleanedAuthors,
          subject,
          totalPages: this.form.totalPages,
          isbn: this.form.isbn,
          publishedDate: this.form.publishedDate,
          desc_txt: this.form.desc_txt,
          cover: this.form.cover
        }

        if (this.isEditMode) {
          await bookService.updateBook(this.$route.params.id, createdBook)
          this.successMessage = 'Le livre a été modifié avec succès.'

          setTimeout(() => {
            this.$router.push({ name: 'book-detail', params: { id: this.$route.params.id } })
          }, 1500)
        } else {
          await bookService.addBook(createdBook)
          this.successMessage = 'Les informations du livre ont bien été enregistrées.'

          setTimeout(() => {
            this.$router.push({ name: 'catalog' })
          }, 1500)
        }
      } catch (error) {
        console.error('Erreur lors de la création/modification du livre :', error)
        this.error = this.isEditMode ? "Impossible de modifier le livre." : "Impossible d'ajouter le livre."
      } finally {
        this.saving = false
      }
    }
  }
}
</script>
