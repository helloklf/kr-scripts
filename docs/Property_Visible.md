
## visible 属性
- 如果你有一个功能选项，只有在满足特定条件的手机上才显示
- 那你一定用的上`visible`属性
- `visible`可以用于 `功能节点`、`外观节点`、`资源节点`，以及action的`param 节点`
- `visible`需要你定义一段脚本，在脚本中输出 `1` 或 `0` 来决定是否显示

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<group>
    <action visible="echo 1">
        <title>测试功能</title>
        <desc>只有visible定义的脚本输出内容为 1 ，我才会被显示</desc>
    </action>
</group>
```

---

> 相关说明

- 由于在xml中写大量的shell代码非常不方便，也不美观，
- 建议参考 [`脚本使用`](./Script.md) 中的说明，
- 将`visible`属性需要执行的代码，写在单独的文件中。
