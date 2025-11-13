<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" @click.self="$emit('close')">
    <div class="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto animate-scaleIn">
      <!-- Header -->
      <div class="sticky top-0 bg-white border-b px-6 py-4 flex justify-between items-center">
        <h2 class="text-2xl font-bold text-gray-900">
          {{ isEditing ? 'Modifier le livre' : 'Ajouter un livre' }}
        </h2>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 transition-colors">
          <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
          </svg>
        </button>
      </div>

      <!-- Body -->
      <form @submit.prevent="handleSubmit" class="p-6 space-y-6">
        <!-- Titre -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Titre *</label>
          <input v-model="form.title" type="text" required class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" placeholder="Le Seigneur des Anneaux" />
          <p v-if="errors.title" class="text-red-500 text-sm mt-1">{{ errors.title }}</p>
        </div>

        <!-- Auteur -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Auteur *</label>
          <input v-model="form.author" type="text" required class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" placeholder="J.R.R. Tolkien" />
          <p v-if="errors.author" class="text-red-500 text-sm mt-1">{{ errors.author }}</p>
        </div>

        <!-- ISBN et Pages -->
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">ISBN</label>
            <input v-model="form.isbn" type="text" class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" placeholder="978-3-16-148410-0" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Pages</label>
            <input v-model.number="form.pages" type="number" min="1" class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" placeholder="350" />
          </div>
        </div>

        <!-- Genre -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Genre</label>
          <select v-model="form.genre" class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent">
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
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Description</label>
          <textarea v-model="form.description" rows="4" class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none" placeholder="Résumé du livre..."></textarea>
        </div>

        <!-- URL de couverture -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">URL de la couverture</label>
          <input v-model="form.coverUrl" type="url" class="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent" placeholder="https://example.com/cover.jpg" />
          <p class="text-xs text-gray-500 mt-1">Lien vers l'image de couverture</p>
        </div>

        <!-- Statut -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Statut</label>
          <div class="flex gap-3">
            <label class="flex items-center cursor-pointer">
              <input v-model="form.status" type="radio" value="TO_READ" class="mr-2" />
              <span class="text-sm">À lire</span>
            </label>
            <label class="flex items-center cursor-pointer">
              <input v-model="form.status" type="radio" value="IN_PROGRESS" class="mr-2" />
              <span class="text-sm">En cours</span>
            </label>
            <label class="flex items-center cursor-pointer">
              <input v-model="form.status" type="radio" value="COMPLETED" class="mr-2" />
              <span class="text-sm">Terminé</span>
            </label>
          </div>
        </div>

        <!-- Progression (si en cours) -->
        <div v-if="form.status === 'IN_PROGRESS'">
          <label class="block text-sm font-medium text-gray-700 mb-2">Progression ({{ form.progress }}%)</label>
          <input v-model.number="form.progress" type="range" min="0" max="100" class="w-full" />
        </div>

        <!-- Actions -->
        <div class="flex gap-3 pt-4 border-t">
          <button type="button" @click="$emit('close')" class="flex-1 px-4 py-2 bg-gray-100 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-200 transition-colors">
            Annuler
          </button>
          <button type="submit" class="flex-1 px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors shadow-sm">
            {{ isEditing ? 'Mettre à jour' : 'Ajouter' }}
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
