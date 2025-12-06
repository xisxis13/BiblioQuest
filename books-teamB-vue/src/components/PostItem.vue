<template>
  <div class="bg-box border border-border rounded-3xl w-full h-fit p-4 flex flex- items-start justify-start gap-4">
    <div class="bg-accent w-10 h-10 aspect-square rounded-full flex items-center justify-center">
      <span class="text-white text-lg font-medium">{{ (post?.user || 'Username')[0] }}</span>
    </div>

    <div class="w-full h-fit flex flex-col items-start justify-start gap-3">
      <div class="w-full h-fit flex flex-col">
        <span class="text-primary-text font-semibold text-base">{{ post?.user || 'Username' }}</span>
        <span class="text-secondary-text font-normal text-sm">{{ getRelativeTime(post.createdAt || post.created_at) }}</span>
      </div>

      <span class="text-primary-text">{{ post.content }}</span>
    </div>
  </div>
</template>

<script>
import { getRelativeTime } from '@/utils/dateUtils';

export default {
  name: 'PostItem',
  props: {
    post: {
      type: Object,
      required: true
    },
  },
  setup() {
    return {
      getRelativeTime,
    };
  },
};
</script>