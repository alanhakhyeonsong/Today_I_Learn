<template>
  <div>
      <div v-for="item in fetchedAsk">{{ item.title }}</div>
  </div>
</template>

<script>
import { mapState, mapGetters } from "vuex";
// import { fetchAskList } from "../api/index.js";

export default {
  computed: {
    // #3
    ...mapGetters({
      fetchedAsk: 'fetchedAsk'
    })

    // #4
    // ...mapGetters([
    //   'fetchedAsk'
    // ]),
    // #2
    // ...mapState({
    //   ask: state => state.ask
    // })
    // #1
    // ask() {
    //   return this.$store.state.ask;
    // }
  },
  created() {
    this.$store.dispatch('FETCH_ASK');
  }
}
</script>

<style>

</style>