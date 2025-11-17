<script setup>
  const props = defineProps({
    items: [{name: String, route: String, icon: String}]
});
</script>
<template>
  <nav class="flex flex-col border border-black items-center justify-center w-200 p-2">
    <ul class="w-max">
      <li v-for="item in items" class="text-center p-1 bg-primay w-max items-center">
        <a :href="item.route" class="items-center w-max" :class="'after:content-[' + item.icon + ']' ">
          {{ item.name }}
        </a>
      </li>
    </ul>
  </nav>
</template>
