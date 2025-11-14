<template>
  <div class="flex flex-col gap-3 p-3 bg-background w-full h-screen">
    <AppHeader />

    <main class="w-full flex-1 flex justify-center items-center gap-3">
      <div class="bg-box border border-border rounded-2xl w-56 h-full flex items-center justify-center">
        <span>MenuBar</span>
      </div>

      <div class="bg-box border border-border rounded-2xl flex-1 h-full flex flex-col gap-4 p-6">
        <div class="w-full h-fit flex flex-col gap-1">
          <span class="text-primary-text font-semibold text-2xl">Catalogue</span>
          <span class="text-secondary-text font-normal text-l">Explorer notre catalogue de plus de 70 livres</span>
        </div>

        <div class="relative bg-background border border-border rounded-full">
          <Search class="absolute top-1/2 -translate-y-1/2 left-3 z-10 text-secondary-text" :size="24" />
          <!-- ajouté une action à la recherche -->
          <input type="text" name="search" id="search" placeholder="ex. : Harry Potter" class="px-4 py-2 pl-11 w-full rounded-full font-normal">
        </div>
      </div>

      <div class="bg-box border border-border rounded-2xl w-56 h-full flex items-center justify-center">
        <span>Filtres</span>
      </div>
    </main>
  </div>
</template>

<script>
import AppHeader from '@/components/AppHeader.vue';
import { Search } from 'lucide-vue-next';

export default {
  name: 'Catalog',
  components: {
    AppHeader,
    Search,
  }
}
</script>