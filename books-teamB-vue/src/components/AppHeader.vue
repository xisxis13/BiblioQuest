<template>
  <div class="w-full flex justify-center items-center gap-2">
    <img :src="biblioQuestIcon" alt="Logo" class="h-14" />
    <span class="text-primary-text font-bold text-2xl">BiblioQuest</span>
  </div>
</template>

<script>
import biblioQuestIcon from '@/assets/BCO.png'

export default {
  name: 'App Header',
  data() {
    return {
      biblioQuestIcon,
    };
  }
}
</script>