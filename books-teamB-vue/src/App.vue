<script>
export default {
  data() {
    return {
      text: 'Edit me'
    }
  }
}
</script>


<template>
  <h1>Un retour rapide sur le champ de saisie</h1>
  <input v-model="text">
  <p>Le champ de saisie contient : {{ text }}</p>
</template>

<style>

</style>
