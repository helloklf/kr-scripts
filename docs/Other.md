## 其它说明

### 语法通融
- 在写`xml`时，框架会对一些配置属性的值做一些兼容

1. 属性语法
  > `readonly="readonly"` `readonly="true"` `readonly="1"`，这三种写法效果是一致的。
  > 当然还是建议使用后两种写法。

2. 节点修饰
  > 框架只识别自己所关心的节点，对于多余的内容，会略过。
  > 利用此规则，`xml`代码可以产生下面所展示的变化
  ```xml

  <!--正常写法-->
  <action>
    <title>测试功能</title>
    <param name="param_aaa">
      <option value="123">选项123</option>
      <option value="124">选项124</option>
    </param>
  </action>

  <!--变化写法-->
  <action>
    <title>测试功能</title>
    <param name="param_aaa">
      <options>
        <option value="123">选项123</option>
        <option value="124">选项124</option>
        <abcdefg>我甚至可以在这里随手写点废话</abcdefg>
      </options>
    </param>
  </action>
  ```
  > 最终运行可发现，两种写法实际效果并没有区别

3. 语法变迁
- 随着框架的更新，可能有一些节点和属性已经产生了变化
- 例如：`[support] -> [visible]`
- 甚至，示例代码中都还能看到旧的语法
- 框架更新时会基本会兼容旧的语法，无需急于重构代码