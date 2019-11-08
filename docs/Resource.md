
## 静态资源提取和使用
- 如果，你需要将一些资源文件放到apk中，并在执行脚本时使用
- 那么，你可以通过 `resource` 来指定要在加载页面时提取的文件


#### 节点配置

| 属性 | 说明 |
| - | - |
| file | 指定assets中的单个文件 |
| dir | 指定assets中的文件或目录，如果是目录，框架会遍历提取 |

#### 代码示例

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<page>
    <!--指定要提取的文件-->
    <resource file="file:///android_asset/test_file.zip" />
    <action>
        <title>测试使用resource</title>
        <desc>试试使用resource导入公共函数库，并使用静态文件</desc>
        <set>
            if [[ -f './test_file.zip' ]]; then
                echo '已找到所需的静态资源文件'
            else
                echo '资源文件丢失...'
            fi
        </set>
    </action>
</page>
```

- 上面的例子中，是通过相对路径来访问解压后的资源
- 你也可以配合全局变量，通过绝对路径来访问，如：`$START_DIR/test_file.zip`


#### 性能优化
- 出于性能考虑，框架不会重复提取同一个资源文件（直到重启），如果你的文件在多处被使用
- `请不要删除或移动已提取的静态资源！`
- 如果 `resource` 被放在带有 `visible` 属性的节点内，并且 `visible` 输出不为 `1`
- 那么 `resource` 所指定的文件，也不会被提取，例如：

```xml
<action visible="echo 0;">
  <resource file="file:///android_asset/aaa.zip" />
  <title>资源文件测试</title>
  <desc>aaa.zip不会被提取，因为这个action的 visible 输出内容为 0</desc>
</action>
```


#### 警告
- 注意，千万不要将你的资源文件，和PIO自带的命令行工具集(`toolkit`)放在一起！！！
- 因为 `toolkit` 目录会在应用启动时提取，并做好相应的权限设置和伪装
- 而如果将静态资源放在 `toolkit` 所在目录，提取文件时可能会破坏运行环境！


#

---

> 相关说明

- `$START_DIR` 是PIO自行添加的全局变量，
- 可以参考 [`脚本使用`](./Script.md) 中的说明，

