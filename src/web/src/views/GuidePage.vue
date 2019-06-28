<template>
  <div class="guide">
    <h1 v-html="this.guide.title"></h1>
      <div v-html="this.guide.excerpt"/>
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
