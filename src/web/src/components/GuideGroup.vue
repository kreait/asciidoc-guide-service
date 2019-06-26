<template>
  <div>
    <b-container>
      <h2>Simple</h2>
      <p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.
        At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est
        Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna
        aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.</p>
      <b-row v-for="(guideList) in guides">
        <b-col v-for="(guide) in guideList" ><guide title="Guide 1"></guide></b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Guide from '@/components/Guide.vue';
import GuideService from '@/services/guide.service';
import Util from '@/util'

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
            .then( (response) =>
              (this.guides = Util.chunk([response.data,response.data,response.data,response.data,response.data],3)));
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
