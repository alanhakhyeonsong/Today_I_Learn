<template>
  <div>
      <div v-for="job in this.$store.state.jobs">{{ job.title }}</div>
  </div>
</template>

<script>
// import { fetchJobsList } from "../api/index.js";

export default {
  created() {
    this.$store.dispatch('FETCH_JOBS');

    // fetchJobsList()
    //   .then(response => this.jobs = response.data)
    //   .catch(error => console.log(error));
  }
}
</script>

<style>

</style>