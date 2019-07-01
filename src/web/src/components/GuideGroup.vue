<template>
  <div>
    <b-container>
      <h1>GUIDES</h1>
      <b-col v-for="(guide) in guides">
          <router-link :to="{ name: 'guides', params: { guideId: guide.id }} ">
            <guide v-bind:title="guide.title"  v-bind:description="guide.description" v-bind:id="guide.id" ></guide>
          </router-link>
      </b-col>
    </b-container>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Guide from '@/components/Guide.vue';
import GuideService from '@/services/guide.service';
import Util from '@/util'
import VueRouter from 'vue-router';

@Component({
  name: 'guide-group',
  components: {
    Guide,
  },
})
export default class GuideGroup extends Vue {

  private guides: any[][] = [];

public mounted() {
    GuideService.get()
            .then( (response) =>{
              console.log("response: " +response)
              this.guides = response
            }
              );
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
