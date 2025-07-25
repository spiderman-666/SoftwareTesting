<!-- PostEditor.vue -->
<script lang="ts">
import { API_BASE_URL } from '@/config/api'
import { computed, defineComponent, onMounted, ref } from 'vue'

export default defineComponent({
  name: 'PostEditor',
  setup() {
    const images = ref<string[]>([])
    const title = ref('')
    const content = ref('')
    const username = ref('')
    const userAvatar = ref('')

    onMounted(() => {
      username.value = uni.getStorageSync('username') || '匿名用户'
      userAvatar.value = uni.getStorageSync('userAvatar') || ''
    })

    /**
     * 选择并上传图片
     */
    const chooseImages = () => {
      uni.chooseImage({
        count: 9, // 可根据需求调整最大选择数量
        sizeType: ['original', 'compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          images.value.push(...res.tempFilePaths)
        },
        fail: () => {
          uni.showToast({
            title: '选择图片失败',
            icon: 'none',
          })
        },
      })
    }

    /**
     * 移除已上传的图片
     * @param index 图片索引
     */
    const removeImage = (index: number) => {
      images.value.splice(index, 1)
    }

    // 返回逻辑
    const handleBack = () => {
      // 实现返回逻辑，例如跳转到上一页
      uni.navigateBack()
    }

    /**
     * 发布帖子
     */
    const publishPost = async () => {
      if (!title.value.trim()) {
        uni.showToast({ title: '标题不能为空', icon: 'none' })
        return
      }
      if (!content.value.trim()) {
        uni.showToast({ title: '正文不能为空', icon: 'none' })
        return
      }
      if (title.value.length < 3) {
        uni.showToast({ title: '标题不能少于3个字符', icon: 'none' })
        return
      }

      if (content.value.length < 10) {
        uni.showToast({ title: '正文不能少于10个字符', icon: 'none' })
        return
      }

      try {
        uni.showLoading({
          title: '发布中...',
          mask: true,
        })

        const formData = {
          username: username.value,
          title: title.value.trim(),
          content: content.value.trim(),
        }
        const getDefaultAvatarPath = async () => {
          return new Promise<string>((resolve) => {
            const ctx = uni.createCanvasContext('avatarCanvas')
            ctx.fillStyle = '#007bff'
            ctx.fillRect(0, 0, 100, 100)
            ctx.fillStyle = '#ffffff'
            ctx.font = 'bold 50px sans-serif'
            ctx.setTextAlign('center') // 修正：使用方法调用，而非赋值
            ctx.setTextBaseline('middle') // 修正：使用方法调用，而非赋值
            ctx.fillText(username.value.charAt(0).toUpperCase(), 50, 50)
            ctx.draw(false, () => {
              uni.canvasToTempFilePath({
                canvasId: 'avatarCanvas',
                success: (res) => {
                  resolve(res.tempFilePath)
                },
                fail: () => {
                  resolve(`https://placehold.co/100x100/007bff/ffffff?text=${username.value.charAt(0).toUpperCase()}`)
                },
              })
            })
          })
        }

        const avatarPath = userAvatar.value || await getDefaultAvatarPath()

        await uni.uploadFile({
          url: `${API_BASE_URL}/forum/post/new`,
          filePath: avatarPath,
          name: 'userAvatar',
          formData,
          header: {
            Authorization: uni.getStorageSync('token'),
          },
          files: images.value.map(path => ({
            name: 'files',
            uri: path,
          })),
          success: (uploadRes) => {
            if (uploadRes.statusCode === 200) {
              uni.hideLoading()
              uni.showToast({
                title: '发布成功',
                icon: 'success',
                success: () => {
                  images.value = []
                  title.value = ''
                  content.value = ''

                  setTimeout(() => {
                    uni.navigateBack()
                  }, 1500)
                },
              })
            }
            else {
              throw new Error('发布失败')
            }
          },
          fail: (error) => {
            uni.hideLoading()
            uni.showToast({ title: '发布失败', icon: 'none' })
            console.error('上传失败:', error)
          },
        })
      }
      catch (error) {
        uni.hideLoading()
        uni.showToast({ title: '发布失败', icon: 'none' })
        console.error('发布失败:', error)
      }
    }

    const justifyClass = computed(() => {
      return images.value.length >= 3 ? 'justify-between' : 'justify-start'
    })

    return {
      images,
      title,
      content,
      chooseImages,
      removeImage,
      publishPost,
      handleBack,
      justifyClass,
      username,
      userAvatar,
    }
  },
})
</script>

<template>
  <!-- Back Button -->
  <BackButton @back="handleBack" />
  <view class="flex flex-col frosted-glass">
    <canvas canvas-id="avatarCanvas" style="width: 100px; height: 100px; position: absolute; left: -1000px;" />
    <view class="flex-1 p-4 space-y-4">
      <!-- Image Upload Section -->
      <view class="flex flex-col">
        <view class="flex flex-wrap gap-2" :class="[justifyClass]">
          <!-- Display Uploaded Images -->
          <view
            v-for="(image, index) in images"
            :key="index"
            class="relative w-23"
          >
            <image :src="image" class="h-23 w-23 rounded object-cover" />
            <view
              class="i-ci:close-sm absolute right-0 top-0 flex cursor-pointer items-center justify-center rounded-full bg-yellow"
              @click="removeImage(index)"
            />
          </view>
          <!-- Upload Button -->
          <view
            class="h-23 w-23 flex cursor-pointer items-center justify-center border-2 rounded border-dashed"
            @click="chooseImages"
          >
            <view class="i-mynaui:upload text-3xl" />
          </view>
        </view>
      </view>

      <!-- Title Input -->
      <view class="flex flex-col">
        <input
          v-model="title"
          type="text"
          placeholder="请输入标题 (最多18字)"
          :maxlength="18"
          class="rounded-lg border-dashed px-3 py-4 text-left"
        >
        <text class="mt-1 text-left text-sm">
          {{ title.length }}/18
        </text>
      </view>

      <!-- Content Editor -->
      <view class="h-50 flex flex-col">
        <textarea
          v-model="content"
          placeholder="请输入正文内容..."
          class="w-full border border-gray-300 rounded-lg px-3 py-1 text-left focus:border-yellow focus:outline-none"
          rows="6"
          style="box-sizing:border-box"
        />
      </view>

      <!-- Publish Button -->
      <button
        class="w-full rounded-lg bg-yellow px-4 py-2 text-lg font-bold font-semibold"
        @click="publishPost"
      >
        发布帖子
      </button>
    </view>
  </view>
</template>

<style scoped>
/* 可根据需要添加额外样式 */
</style>

<route lang="json">
{
  "layout": "default"
}
</route>
