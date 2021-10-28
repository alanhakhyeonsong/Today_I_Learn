<template>
  <div>
      <div v-for="user in this.$store.state.news">{{ user.title }}</div>
  </div>
</template>

<script>
// import { fetchNewsList } from "../api/index.js";

export default {
  created() {
    this.$store.dispatch('FETCH_NEWS');

    // var vm = this;
    // fetchNewsList()
    //   .then(function(response) {
    //     console.log(response);
    //     vm.users = response.data;
    //   })
    //   .catch(function(error) {
    //     console.log(error);
    //   })
  }
}
</script>

<style>

</style>