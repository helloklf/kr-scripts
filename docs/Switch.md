
## Switch

### Switch 属性

- 公共属性（功能节点共有）

| 属性 | 作用 | 有效值 | 必需 | 示例 |
| - | - | - | :-: | :- |
| desc | 描述 | 文本内容 | 否 | 显示在标题下的小字，可以不设置 |
| desc-sh | 动态设置desc内容的脚本 | `脚本代码` | 否 | `echo '自定义的说明信息'` |
| confirm | 点击时是否弹出确认框，默认`false` | `true`、`false` | 否 | `false` |
| visible | 自定义脚本，输出1或0，决定该功能项是否显示 | 脚本代码 | 否 | `echo '1'` |


- 特有属性

| 属性 | 作用 | 有效值 | 必需 | 示例 |
| - | - | - | :-: | :- |
| interruptible | 是否允许中断执行，默认`true` | `true`、`false` | 否 | `false` |
| auto-off | 执行完脚本后是否自动关闭日志界面，默认`false` | `true`、`false` | 否 | `false` |
| reload-page | 执行完脚本后是否刷新页面，默认`false` | `true` `false` | 否 | `true` |
| bg-task | 是否作为后台任务执行，默认`false` | `true` `false` | 否 | `true` |

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
- 建议参考 [`脚本使用`](./Script.md) 中的说明，
- 将段落较长的脚本代码，写在单独的文件中。
