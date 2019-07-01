<template>
  <div class="guide">
    <h1 v-html="this.guide.title"></h1>
      <div v-html="this.guide.description"/>
  </div>
</template>

<script lang="ts">
  import GuideService from '@/services/guide.service';
  import Guide from '@/components/Guide.vue'

  export default{
  data(){
      console.log("data")
      return {
        guideId: this.$route.params.guideId,
        guide:Guide
      }
    }, 
    created(){
      GuideService.getGuide(this.guideId).then( (response) =>{
          this.guide = response
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
