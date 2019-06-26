<template>
  <div class="guide">
      <h3>{{title}}</h3>
      <p>{{excerpt}}</p>
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
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .guide {
    border: black dotted thin;
    min-height: 200px;
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
  color: #42b983;
}
</style>
