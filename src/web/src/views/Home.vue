<template>
  <div class="home">
    <guide-group></guide-group>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import HelloWorld from '@/components/HelloWorld.vue';
import Hero from '@/components/Hero.vue';
import GuideGroup from '@/components/GuideGroup.vue'; // @ is an alias to /src

@Component({
  components: {
    HelloWorld,
    Hero,
    'guide-group': GuideGroup,
  },
})
export default class Home extends Vue {}
</script>
