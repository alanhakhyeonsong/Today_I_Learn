import Vue from 'vue';
import Vuex from 'vuex';
import mutations from "./mutations.js";
import actions from "./actions.js";

Vue.use(Vuex);

export const store = new Vuex.Store({
  state: {
    news: [],
    jobs: [],
    ask: [],
    user: {},
    item: []
  },
  getters: {
    fetchedAsk(state) {
      return state.ask;
    },
    fetchedNews(state) {
      return state.news;
    },
    fetchedJobs(state) {
      return state.jobs;
    },
    fetchedUser(state) {
      return state.user;
    },
    fetchCommentItem(state) {
      return state.item;
    },
    fetchedItem(state) {
      return state.item;
    }
  },
  mutations,
  actions
})