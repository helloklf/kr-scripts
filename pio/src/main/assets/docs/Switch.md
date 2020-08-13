
## Switch

### Switch 属性

- 公共属性（Action、Switch、Picker共有）

| 属性 | 作用 | 有效值 | 示例 |
| - | - | - | :- |
| id | 设置ID后，该功能可被长按添加为桌面快捷方式 | 当前XML文件里不重复即可 | `a0001` |
| desc | 显示在标题下的小字，可以不设置 | 文本内容 | `这是描述` |
| desc-sh | 动态设置desc内容的脚本 | `脚本代码` | `echo '自定义的说明信息'` |
| summary | 高亮显示的摘要信息 | 文本内容 | `这是摘要` |
| summary-sh | 动态设置summary内容的脚本 | `脚本代码` | `echo '自定义的摘要信息'` |
| confirm | 点击时是否弹出确认框，默认`false` | `true`、`false` | `false` |
| visible | 自定义脚本，输出1或0，决定该功能项是否显示 | 脚本代码 | `echo '1'` |
| interruptible | 是否允许中断执行，默认`true` | `true`、`false` | `false` |
| auto-off | 执行完脚本后是否自动关闭日志界面，默认`false` | `true`、`false` | `false` |
| auto-finish | 是否在关闭日志界面后关闭当前页面 | `true`、`false` | `false` |
| logo | 作为快捷方式添加到桌面时使用的图标 | 文件路径 |  |
| icon | 显示在功能左侧的图标。如果未设置logo属性，它也同时会被作为logo使用 | 文件路径 |  |
| reload | 执行完脚本后要执行的刷新操作 | `page` 、具体体功能`id` | `page` |
| shell | 执行脚本时的交互界面，可设置为：日志输出(default)、静默执行(hidden)、后台执行(bg-task) | `default` `hidden` `bg-task` | `bg-task` |

> `id` 属性建议配合 `auto-off`、`auto-finish`、`logo` 使用

> `logo`和`icon`除了支持assets文件路径，也支持磁盘文件路径

### 添加Switch到页面
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<page>
    <switch>
        <title>测试开关</title>
        <desc>测试开关功能</desc>
        <get>getprop test.switch.aaa</get>
        <set>setprop test.switch.aaa "$state"</set>
    </switch>
</page>
```

### switch > get
- 自定义一段脚本，输出 `1` 或 `0` 来确定开关当前状态

### switch > set
- 自定义用户点击开关后要执行的代码，开关状态会以`$state`参数传入脚本


#

---

> 相关说明

- 由于在xml中写大量的shell代码非常不方便，也不美观，
- 建议参考 [`Script`](#/doc?doc=/docs/Script.md) 中的说明，
- 将段落较长的脚本代码，写在单独的文件中。
