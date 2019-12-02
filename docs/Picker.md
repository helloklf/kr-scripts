
## Picker (3.4.5+)
- `picker`是在3.4.5版本后增加的一个新功能，即加强版的`switch`
- `picker`和`switch`一样，通过`get`读取当前状态，通过`set`保存状态
- 其它配置项也和`switch`一致
- `picker`需要你自己定义选项(`option`)，就像这样

### 属性

- 公共属性（Action、Switch、Picker共有）

| 属性 | 作用 | 有效值 | 示例 |
| - | - | - | :- |
| id | 如果允许长按添加到桌面快捷，必需设置ID | 当前配置文件中必需唯一 | `a0001` |
| desc | 显示在标题下的小字，可以不设置 | 文本内容 | `这是描述` |
| desc-sh | 动态设置desc内容的脚本 | `脚本代码` | `echo '自定义的说明信息'` |
| summary | 高亮显示的摘要信息 | 文本内容 | `这是摘要` |
| summary-sh | 动态设置summary内容的脚本 | `脚本代码` | `echo '自定义的摘要信息'` |
| confirm | 点击时是否弹出确认框，默认`false` | `true`、`false` | `false` |
| visible | 自定义脚本，输出1或0，决定该功能项是否显示 | 脚本代码 | `echo '1'` |
| interruptible | 是否允许中断执行，默认`true` | `true`、`false` | `false` |
| auto-off | 执行完脚本后是否自动关闭日志界面，默认`false` | `true`、`false` | `false` |
| auto-finish | 是否在关闭日志界面后关闭当前页面 | `true`、`false` | `false` |
| icon | 作为快捷方式添加到桌面时使用的图标（目前仅支持assets路径） | `true`、`false` | `false` |
| reload | 执行完脚本后要执行的刷新操作 | `page` 、具体体功能`id` | `page` |
| bg-task | 后台运行而不是显示日志输出界面，默认`false` | `true` `false` | `true` |


> `id` 属性建议配合 `auto-off`、`auto-finish`、`icon` 使用

```xml
<picker>
    <title>单选列表</title>
    <desc>测试单选列表</desc>
    <options>
        <option value="a1">选项1</option>
        <option value="a2">选项1</option>
    </options>
    <get>getprop xxx.xxx.xxx</get>
    <set>setprop xxx.xxx.xxx $state</set>
</picker>
```

- **动态选项**
- picker也允许使用`options-sh`属性来设置输出下拉选项的脚本
- 用法和action的param一样，如：

```xml
<picker options-sh="echo 'a|选项A'; echo 'b|选项B'">
    <title>测试单选界面</title>
    <desc>测试单选界面</desc>
    <get>getprop xxx.xxx.xxx3</get>
    <set>setprop xxx.xxx.xxx3 "$state"</set>
</picker>
```

- **多选模式**
- 在picker节点上增加`multiple="true"`属性来标识允许多选
- 例如：

```xml
<picker options-sh="echo 'a|选项A'; echo 'b|选项B'" value-sh="echo 'a'; echo 'b';">
    <title>测试单选界面</title>
    <get>getprop xxx.xxx.xxx4</get>
    <set>setprop xxx.xxx.xxx4 "$state"</set>
</picker>
```


#

---

> 相关说明

- 由于在xml中写大量的shell代码非常不方便，也不美观，
- 建议参考 [`脚本使用`](./Script.md) 中的说明，
- 将段落较长的脚本代码，写在单独的文件中。
