<template>
  <div class="progress-bar-container">
    <div class="flex items-center justify-between mb-2">
      <span class="text-sm font-medium text-foreground">{{ label }}</span>
      <span class="text-sm font-semibold text-primary">{{ percentage }}%</span>
    </div>
    
    <div class="progress-bar-track">
      <div 
        class="progress-bar-fill"
        :style="{ width: `${percentage}%` }"
        :class="progressColorClass"
      >
        <div class="progress-bar-shine"></div>
      </div>
    </div>
    
    <div v-if="showDetails" class="flex items-center justify-between mt-2 text-xs text-muted-foreground">
      <span>{{ currentValue }} / {{ maxValue }} {{ unit }}</span>
      <span v-if="remainingValue > 0">{{ remainingValue }} {{ unit }} restants</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProgressBar',
  props: {
    label: {
      type: String,
      default: 'Progression'
    },
    currentValue: {
      type: Number,
      required: true
    },
    maxValue: {
      type: Number,
      required: true
    },
    unit: {
      type: String,
      default: 'pages'
    },
    showDetails: {
      type: Boolean,
      default: true
    },
    color: {
      type: String,
      default: 'primary',
      validator: (value) => ['primary', 'success', 'warning', 'danger'].includes(value)
    }
  },
  computed: {
    percentage() {
      if (this.maxValue === 0) return 0
      return Math.min(Math.round((this.currentValue / this.maxValue) * 100), 100)
    },
    remainingValue() {
      return Math.max(this.maxValue - this.currentValue, 0)
    },
    progressColorClass() {
      const colorMap = {
        primary: 'bg-primary',
        success: 'bg-green-500',
        warning: 'bg-yellow-500',
        danger: 'bg-red-500'
      }
      return colorMap[this.color] || colorMap.primary
    }
  }
}
</script>

<style scoped>
.progress-bar-container {
  @apply w-full;
}

.progress-bar-track {
  @apply w-full h-2.5 bg-secondary rounded-full overflow-hidden relative;
}

.progress-bar-fill {
  @apply h-full rounded-full transition-all duration-500 ease-out relative overflow-hidden;
}

.progress-bar-shine {
  @apply absolute inset-0 bg-gradient-to-r from-transparent via-white/20 to-transparent;
  animation: shine 2s infinite;
}

@keyframes shine {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}
</style>
