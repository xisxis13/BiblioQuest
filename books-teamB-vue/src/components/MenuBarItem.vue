<template>
  <router-link
    :to="to"
    class="flex items-center w-full px-4 py-3 gap-3 rounded-2xl transition-colors duration-200"
    :class="[
      isActive 
        ? 'bg-accent text-white font-medium' 
        : 'text-secondary-text hover:bg-accent/10 hover:text-primary-text'
    ]"
  >
    <component :is="icon" class="w-5 h-5" />
    <span>{{ label }}</span>
  </router-link>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

export default {
  name: 'MenuBarItem',
  props: {
    to: {
      type: String,
      required: true
    },
    label: {
      type: String,
      required: true
    },
    icon: {
      type: [Object, Function, String],
      required: true
    }
  },
  setup(props) {
    const route = useRoute()
    
    const isActive = computed(() => {
      return route.path === props.to
    })

    return {
      isActive
    }
  }
}
</script>