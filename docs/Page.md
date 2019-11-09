
## Page 节点
- 指定一个配置文件 `[config]` 或网页 `[html]` 作为一个子页面入口

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<page>
    <page title="原生专属"
        desc="越接近AOSP越适用的选项"
        config="file:///android_asset/config_xml/for_aosp.xml" />
    <page title="Flyme专属"
        desc="用于Meizu Flyme系统的选项"
        html="https://www.lanzous.com/b838135" />
</page>
```

- 公共属性（功能节点共有）

| 属性 | 作用 | 有效值 | 必需 | 示例 |
| - | - | - | :-: | :- |
| id | 如果允许长按添加到桌面快捷，必需设置ID | 当前配置文件中必需唯一 | `a0001` |
| desc | 描述 | 文本内容 | 否 | 显示在标题下的小字，可以不设置 |
| desc-sh | 动态设置desc内容的脚本 | `脚本代码` | 否 | `echo '自定义的说明信息'` |
| confirm | 点击时是否弹出确认框，默认`false` | `true`、`false` | 否 | `false` |
| visible | 自定义脚本，输出1或0，决定该功能项是否显示 | 脚本代码 | 否 | `echo '1'` |


- 特有属性

| 属性 | 名称 | 用途 |
| :-: | :-: | :- |
| config | 配置 | 指定另一个配置文件作为子页面的内容 |
| html | 网页 | 指定一个网页作为子页面的内容 |
| config-sh | 动态配置 | 写一段脚本，输出配置文件所在路径或完整内容 |
| before-load | 读取配置前 | 指定在加载配置文件前要执行的代码 |
| after-load | 读取配置后 | 指定配置文件读取完成后要执行的代码 |

- **注意：** config只能指定应用assets下的文件，其中`file:///android_asset/`是固定前缀，可以省略不写，就像这样：

```xml
<page title="标题文本"
    desc="描述文本"
    config="config_xml/for_aosp.xml" />
```

- **config-sh** 属性需设置为一段脚本，通过`echo`输出配置文件所在位置，或直接输出配置文件完整内容

```xml
<!--输出配置文件绝对路径。支持以 “file:///android_asset” 开头指定assets中的文件-->
<page after-read="sleep 1" before-read="sleep 1"
    config-sh="echo 'file:///android_asset/kr-script/pages/custom_kernel_tuner.xml'">
    <title>测试config-sh【assets路径输出】</title>
    <desc>通过config-sh输出配置页所在位置，它可以是在assets中的文件</desc>
</page>

<!--输出配置文件绝对路径-->
<page config-sh="echo '/sdcard/text_test.xml'">
    <title>测试config-sh【路径输出】</title>
    <desc>通过config-sh输出配置页所在位置</desc>
</page>

<!--输出配置文件内容（框架识别到输出内容以“<?xml”开头 且以“>”结尾，按照此模式解析）-->
<page config-sh="cat /sdcard/text_test.xml">
    <title>测试config-sh【全文输出】</title>
    <desc>通过config-sh输出配置页内容</desc>
</page>
```

### 补充说明
- 为与action、switch定义语法保持一致，title、desc也可以作为page下的节点定义

```xml
<page config="file:///android_asset/config_xml/for_miui.xml">
  <title>MIUI专属</title>
  <desc>用于Xiaomi MIUI的选项</desc>
</page>
```
