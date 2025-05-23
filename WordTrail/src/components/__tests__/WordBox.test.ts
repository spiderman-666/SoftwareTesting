import WordBox from '@/components/WordBox.vue'
import { mount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'

describe('wordBox.vue', () => {
  const mockWordData = {
    word: 'testword',
    language: 'en',
    phonetics: [
      { ipa: 'tɛst', audio: 'audio1.mp3' },
      { ipa: 'twɜːd', audio: 'audio2.mp3' },
      { ipa: 'tɛstwɜːd', audio: 'audio3.mp3' },
      { ipa: 'extra', audio: 'audio4.mp3' }, // 应该被截断
    ],
    partOfSpeechList: [
      {
        type: 'noun',
        definitions: ['a thing used to test', 'an experiment'],
        gender: 'n/a',
      },
      {
        type: 'verb',
        definitions: ['to run a test'],
      },
    ],
  }

  it('正确渲染单词和音标', () => {
    const wrapper = mount(WordBox, {
      props: { wordData: mockWordData },
    })
    expect(wrapper.text()).toContain('testword')
    expect(wrapper.text()).toContain('[tɛst]')
    expect(wrapper.text()).toContain('[twɜːd]')
    expect(wrapper.text()).toContain('[tɛstwɜːd]')
    expect(wrapper.text()).not.toContain('[extra]') // 限制了最多3个
  })

  it('正确渲染词性和定义', () => {
    const wrapper = mount(WordBox, {
      props: { wordData: mockWordData }
    })
    expect(wrapper.text()).toContain('noun')
    expect(wrapper.text()).toContain('a thing used to test; an experiment')
    expect(wrapper.text()).toContain('verb')
    expect(wrapper.text()).toContain('to run a test')
  })

  it('点击时触发 click 事件', async () => {
    const onClick = vi.fn()
    const wrapper = mount(WordBox, {
      props: { wordData: mockWordData },
      attrs: { onClick }, // 监听组件的 $emit('click')
    })
    await wrapper.find('.cursor-pointer').trigger('click')
    expect(onClick).toHaveBeenCalled()
  })
})
