<template>
  <router-link
    :to="to"
    :class="[
      'flex items-center w-full py-3 gap-3 rounded-2xl transition-colors duration-200',
      collapsed ? 'px-2 justify-center' : 'px-4',
      isActive 
        ? 'bg-accent text-white font-medium' 
        : 'text-secondary-text hover:bg-accent/10 hover:text-primary-text'
    ]"
    :title="collapsed ? label : ''"
  >
    <component :is="icon" class="w-5 h-5" />
    <span v-if="!collapsed">{{ label }}</span>
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
    },
    collapsed: {
      type: Boolean,
      default: false
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