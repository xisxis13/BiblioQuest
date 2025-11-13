<template>
  <div class="bg-white rounded-lg shadow-sm hover:shadow-md transition-all p-3">
    <!-- Couverture du livre -->
    <div class="relative mb-3 overflow-hidden rounded-md bg-gray-100 aspect-[2/3]">
      <img v-if="book.coverUrl" :src="book.coverUrl" :alt="book.title" class="w-full h-full object-cover" />
      <div v-else class="w-full h-full flex items-center justify-center bg-gradient-to-br from-blue-500 to-cyan-600">
        <svg class="w-10 h-10 text-white opacity-50" fill="currentColor" viewBox="0 0 20 20">
          <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 715.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z"/>
        </svg>
      </div>
      
      <!-- Badge de statut -->
      <div class="absolute top-1.5 right-1.5">
        <span :class="statusBadgeClass" class="px-1.5 py-0.5 text-xs font-medium rounded">
          {{ statusLabel }}
        </span>
      </div>
    </div>

    <!-- Informations du livre -->
    <div>
      <h3 class="text-sm font-semibold text-gray-900 mb-0.5 line-clamp-2">{{ book.title }}</h3>
      <p class="text-xs text-gray-600 mb-2">{{ book.author }}</p>

      <!-- Progression -->
      <div v-if="book.status === 'IN_PROGRESS' && book.progress" class="mb-2">
        <div class="flex justify-between text-xs text-gray-500 mb-1">
          <span class="text-xs">Progression</span>
          <span class="text-xs font-medium">{{ book.progress }}%</span>
        </div>
        <div class="w-full bg-gray-200 rounded-full h-1.5">
          <div class="bg-blue-600 h-1.5 rounded-full transition-all" :style="{ width: book.progress + '%' }"></div>
        </div>
      </div>

      <!-- Métadonnées -->
      <div class="flex items-center gap-2 text-xs text-gray-500 mb-2" v-if="book.pages || book.genre">
        <span v-if="book.pages" class="text-xs">{{ book.pages }}p</span>
        <span v-if="book.genre" class="px-1.5 py-0.5 bg-gray-100 rounded text-xs">{{ book.genre }}</span>
      </div>

      <!-- Actions -->
      <div class="flex gap-1.5">
        <button v-if="book.status === 'IN_PROGRESS'" @click="$emit('start-reading', book)" class="flex-1 px-2 py-1.5 bg-blue-600 text-white text-xs font-medium rounded-md hover:bg-blue-700 transition-colors">
          Continuer
        </button>
        
        <button v-else-if="book.status === 'TO_READ'" @click="updateStatus('IN_PROGRESS')" class="flex-1 px-2 py-1.5 bg-blue-600 text-white text-xs font-medium rounded-md hover:bg-blue-700 transition-colors">
          Commencer
        </button>

        <button v-else-if="book.status === 'COMPLETED'" @click="$emit('start-reading', book)" class="flex-1 px-2 py-1.5 bg-gray-100 text-gray-700 text-xs font-medium rounded-md hover:bg-gray-200 transition-colors">
          Relire
        </button>

        <!-- Menu dropdown -->
        <div class="relative">
          <button @click="toggleMenu" class="p-1.5 bg-gray-100 text-gray-700 rounded-md hover:bg-gray-200 transition-colors">
            <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path d="M10 6a2 2 0 110-4 2 2 0 010 4zM10 12a2 2 0 110-4 2 2 0 010 4zM10 18a2 2 0 110-4 2 2 0 010 4z"/>
            </svg>
          </button>

          <!-- Dropdown menu -->
          <div v-if="showMenu" class="absolute right-0 mt-1 w-40 bg-white rounded-lg shadow-lg border z-10">
            <button @click="$emit('edit', book); closeMenu()" class="w-full text-left px-3 py-1.5 text-xs hover:bg-gray-50 flex items-center">
              <svg class="w-3 h-3 mr-1.5 text-gray-500" fill="currentColor" viewBox="0 0 20 20">
                <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
              </svg>
              Modifier
            </button>
            
            <div class="border-t">
              <button v-if="book.status !== 'TO_READ'" @click="updateStatus('TO_READ')" class="w-full text-left px-3 py-1.5 text-xs hover:bg-gray-50">À lire</button>
              <button v-if="book.status !== 'IN_PROGRESS'" @click="updateStatus('IN_PROGRESS')" class="w-full text-left px-3 py-1.5 text-xs hover:bg-gray-50">En cours</button>
              <button v-if="book.status !== 'COMPLETED'" @click="updateStatus('COMPLETED')" class="w-full text-left px-3 py-1.5 text-xs hover:bg-gray-50">Terminé</button>
            </div>

            <div class="border-t">
              <button @click="$emit('delete', book.id); closeMenu()" class="w-full text-left px-3 py-1.5 text-xs text-red-600 hover:bg-red-50 flex items-center">
                <svg class="w-3 h-3 mr-1.5" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd"/>
                </svg>
                Supprimer
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'BookCard',
  props: {
    book: {
      type: Object,
      required: true
    }
  },
  emits: ['edit', 'delete', 'update-status', 'start-reading'],
  setup(props, { emit }) {
    const showMenu = ref(false)

    const statusConfig = {
      TO_READ: { label: 'À lire', class: 'bg-gray-100 text-gray-700' },
      IN_PROGRESS: { label: 'En cours', class: 'bg-orange-100 text-orange-700' },
      COMPLETED: { label: 'Terminé', class: 'bg-green-100 text-green-700' }
    }

    const statusLabel = computed(() => statusConfig[props.book.status]?.label || 'Non défini')
    const statusBadgeClass = computed(() => statusConfig[props.book.status]?.class || 'bg-gray-100 text-gray-700')

    const toggleMenu = () => {
      showMenu.value = !showMenu.value
    }

    const closeMenu = () => {
      showMenu.value = false
    }

    const updateStatus = (newStatus) => {
      emit('update-status', props.book.id, newStatus)
      closeMenu()
    }

    // Fermer le menu si on clique ailleurs
    const handleClickOutside = (event) => {
      if (!event.target.closest('.relative')) {
        closeMenu()
      }
    }

    return {
      showMenu,
      statusLabel,
      statusBadgeClass,
      toggleMenu,
      closeMenu,
      updateStatus
    }
  }
}
</script>
