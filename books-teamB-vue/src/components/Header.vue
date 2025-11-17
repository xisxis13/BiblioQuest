<template>
  <header class="bg-white border-b border-gray-200 py-4 px-6 fixed top-0 left-0 right-0 z-20">
    <div class="flex items-center justify-center gap-3">
      <div class="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center border-2 border-orange-200">
        <img src="@/assets/BCO.png" alt="BiblioQuest Logo" class="h-7 w-7 object-contain" />
      </div>
      <span class="text-2xl font-bold text-[#8B4513]">BiblioQuest</span>
    </div>
  </header>
</template>

<script>
export default {
  name: 'Header'
}
</script>
