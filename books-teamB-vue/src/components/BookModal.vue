<template>
  <div class="fixed inset-0 bg-background/80 backdrop-blur-sm flex items-center justify-center z-50 p-4" @click.self="$emit('close')">
    <div class="bg-card rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-border animate-scaleIn">
      <!-- Header -->
      <div class="sticky top-0 bg-card px-6 py-4 flex justify-between items-center border-b border-border">
        <h2 class="text-xl font-semibold text-foreground">
          {{ isEditing ? 'Modifier le livre' : 'Ajouter un livre' }}
        </h2>
        <button @click="$emit('close')" class="inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring hover:bg-accent hover:text-accent-foreground h-9 w-9 p-0">
          <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
          </svg>
        </button>
      </div>

      <!-- Body -->
      <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
        <!-- Titre -->
        <div class="space-y-2">
          <label class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">Titre *</label>
          <input v-model="form.title" type="text" required class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2" placeholder="Le Seigneur des Anneaux" />
          <p v-if="errors.title" class="text-sm text-destructive">{{ errors.title }}</p>
        </div>

        <!-- Auteur -->
        <div class="space-y-2">
          <label class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">Auteur *</label>
          <input v-model="form.author" type="text" required class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2" placeholder="J.R.R. Tolkien" />
          <p v-if="errors.author" class="text-sm text-destructive">{{ errors.author }}</p>
        </div>

        <!-- ISBN et Pages -->
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <label class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">ISBN</label>
            <input v-model="form.isbn" type="text" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2" placeholder="978-3-16-148410-0" />
          </div>
          <div class="space-y-2">
            <label class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">Pages</label>
            <input v-model.number="form.pages" type="number" min="1" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2" placeholder="350" />
          </div>
        </div>

        <!-- Genre -->
        <div class="space-y-2">
          <label class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">Genre</label>
          <select v-model="form.genre" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2">
            <option value="">Sélectionner un genre</option>
            <option value="Fiction">Fiction</option>
            <option value="Science-Fiction">Science-Fiction</option>
            <option value="Fantasy">Fantasy</option>
            <option value="Romance">Romance</option>
            <option value="Thriller">Thriller</option>
            <option value="Policier">Policier</option>
            <option value="Biographie">Biographie</option>
            <option value="Histoire">Histoire</option>
            <option value="Philosophie">Philosophie</option>
            <option value="Développement personnel">Développement personnel</option>
            <option value="Autre">Autre</option>
          </select>
        </div>

        <!-- Description -->
        <div class="space-y-2">
          <label class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">Description</label>
          <textarea v-model="form.description" rows="4" class="flex min-h-[80px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 resize-none" placeholder="Résumé du livre..."></textarea>
        </div>

        <!-- URL de couverture -->
        <div class="space-y-2">
          <label class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">URL de la couverture</label>
          <input v-model="form.coverUrl" type="url" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2" placeholder="https://..." />
        </div>

        <!-- Statut et Progression -->
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <label class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">Statut</label>
            <select v-model="form.status" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2">
              <option value="TO_READ">À lire</option>
              <option value="IN_PROGRESS">En cours</option>
              <option value="COMPLETED">Terminé</option>
            </select>
          </div>

          <!-- Progression (si en cours) -->
          <div v-if="form.status === 'IN_PROGRESS'" class="space-y-2">
            <label class="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">Progression (%)</label>
            <input v-model.number="form.progress" type="number" min="0" max="100" class="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2" />
          </div>
        </div>

        <!-- Actions -->
        <div class="flex gap-3 pt-4">
          <button type="button" @click="$emit('close')" class="inline-flex items-center justify-center whitespace-nowrap rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 border border-input bg-background hover:bg-accent hover:text-accent-foreground h-10 px-4 py-2 flex-1">
            Annuler
          </button>
          <button type="submit" class="inline-flex items-center justify-center whitespace-nowrap rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-4 py-2 flex-1">
            {{ isEditing ? 'Enregistrer' : 'Ajouter' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, watch } from 'vue'

export default {
  name: 'BookModal',
  props: {
    book: {
      type: Object,
      default: null
    },
    isEditing: {
      type: Boolean,
      default: false
    }
  },
  emits: ['close', 'save'],
  setup(props, { emit }) {
    const form = ref({
      title: '',
      author: '',
      isbn: '',
      pages: null,
      genre: '',
      description: '',
      coverUrl: '',
      status: 'TO_READ',
      progress: 0
    })

    const errors = ref({})

    // Charger les données du livre si en mode édition
    watch(() => props.book, (newBook) => {
      if (newBook) {
        form.value = {
          title: newBook.title || '',
          author: newBook.author || '',
          isbn: newBook.isbn || '',
          pages: newBook.pages || null,
          genre: newBook.genre || '',
          description: newBook.description || '',
          coverUrl: newBook.coverUrl || '',
          status: newBook.status || 'TO_READ',
          progress: newBook.progress || 0
        }
      }
    }, { immediate: true })

    const validateForm = () => {
      errors.value = {}
      
      if (!form.value.title.trim()) {
        errors.value.title = 'Le titre est requis'
      }
      
      if (!form.value.author.trim()) {
        errors.value.author = "L'auteur est requis"
      }

      return Object.keys(errors.value).length === 0
    }

    const handleSubmit = () => {
      if (validateForm()) {
        emit('save', form.value)
      }
    }

    return {
      form,
      errors,
      handleSubmit
    }
  }
}
</script>
