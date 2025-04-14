<script lang="ts">
    let left = -100;
    setInterval(() => {
        left += 100;

        if (left > window.innerWidth + 100) {
            left = -100;
        }
    }, 500);
</script>

<div class="taco">
    <img src="https://q0.itc.cn/q_70/images03/20241114/2be52013001e451d8e885fe8847ffde7.gif" alt="taco no load :(("
         class:transition={left < window.innerWidth + 100 && left > -100} style="left: {left}px">
</div>

<style lang="scss">
  .taco {
    position: relative;
    height: 60px;
    background-color: red;

    img {
      position: absolute;
      height: 100%;

      &.transition {
        transition: linear left 500ms;
      }
    }
  }
</style>

