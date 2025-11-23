<template>
  <div class="w-full h-fit flex flex-col gap-2">
    <div class="flex items-center justify-between gap-2">
      <span class="text-sm font-medium text-secondary-text">{{ label }}</span>
      <span class="text-sm font-semibold text-accent">{{ percentage }}%</span>
    </div>

    <div class="w-full h-2.5 bg-background border border-border rounded-full overflow-hidden relative">
      <div
        class="h-full rounded-full transition-all duration-500 ease-out relative overflow-hidden"
        :style="{ width: `${percentage}%` }"
        :class="progressColorClass"
      ></div>
    </div>

    <div v-if="showDetails" class="flex items-center justify-between text-xs text-primary-text">
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
      default: 'Progression',
    },
    currentValue: {
      type: Number,
      required: true,
    },
    maxValue: {
      type: Number,
      required: true,
    },
    unit: {
      type: String,
      default: 'pages',
    },
    showDetails: {
      type: Boolean,
      default: true,
    },
    color: {
      type: String,
      default: 'primary',
      validator: (value) => ['primary', 'success', 'warning', 'danger'].includes(value),
    },
  },
  computed: {
    percentage() {
      if (this.maxValue === 0) return 0;
      return Math.min(Math.round((this.currentValue / this.maxValue) * 100), 100);
    },
    remainingValue() {
      return Math.max(this.maxValue - this.currentValue, 0);
    },
    progressColorClass() {
      const colorMap = {
        primary: 'bg-accent',
        success: 'bg-green-500',
        warning: 'bg-yellow-500',
        danger: 'bg-red-500',
      };
      return colorMap[this.color] || colorMap.primary;
    },
  },
};
</script>