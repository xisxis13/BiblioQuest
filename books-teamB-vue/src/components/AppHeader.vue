<template>
  <div class="w-full flex justify-start items-center gap-2">
    <button
      @click="uiStore.toggleMenu"
      class="px-3 py-2 w-16 h-full max-h-11 rounded-2xl hover:bg-accent/10 transition-colors flex items-center justify-center"
      title="Toggle menu"
    >
      <Menu class="w-6 h-6 text-primary-text" />
    </button>

    <div class="flex justify-center items-center gap-2">
      <img :src="biblioQuestIcon" alt="BilbioQuest icon" class="h-14">
      <span class="text-primary-text font-bold text-2xl">BiblioQuest</span>
    </div>
  </div>
</template>

<script>
import { Menu } from 'lucide-vue-next';
import { useUiStore } from '@/stores/uiStore';
import biblioQuestIcon from '@/assets/BCO.png';

export default {
  name: 'AppHeader',
  components: {
    Menu
  },
  setup() {
    const uiStore = useUiStore();

    return {
      biblioQuestIcon,
      uiStore
    };
  },
};
</script>
