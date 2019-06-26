<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png">
    <hero></hero>
    <feature-group></feature-group>
    <guide-group></guide-group>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import HelloWorld from '@/components/HelloWorld.vue';
import Hero from '@/components/Hero.vue';
import FeatureGroup from '@/components/FeatureGroup.vue';
import GuideGroup from '@/components/GuideGroup.vue'; // @ is an alias to /src

@Component({
  components: {
    HelloWorld,
    Hero,
    'feature-group': FeatureGroup,
    'guide-group': GuideGroup,
  },
})
export default class Home extends Vue {}
</script>
