<template>
  <div class="guide">
      <h1 v-html="title"></h1>
      <div v-html="excerpt"> </div>
  </div>
</template>

<script lang="ts">

import {Component, Prop, Vue} from 'vue-property-decorator';

@Component({
    name: 'guide',
})
export default class Guide extends Vue {
  @Prop({default: 'Guide Title'})
  private title!: string;

  @Prop({default: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam' +
            ' nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam'})
  private excerpt!: string;

  @Prop({default: ''})
  private id!: string;
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .guide {
    border-bottom: gray solid 1px;
  }
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
  
}
</style>
