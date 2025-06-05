// sum.test.ts
import { expect, test } from 'vitest'
import { sum } from './sum'
// 在本例中，expect(sum(1, 2)).toBe(3)表示使用expect方法来测试sum函数是否能正确地将两个数字相加。此处使用 toBe来进行断言，这是Vitest提供的一个匹配器函数，用于比较实际值和期望值是否严格相等。
test('adds 1 + 2 to equal 3', () => {
  expect(sum(1, 2)).toBe(3)
})
