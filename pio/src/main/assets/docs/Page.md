
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

- 公共属性（大部分与功能节点共有）

| 属性 | 作用 | 有效值 | 示例 |
| - | - | - | :- |
| id | 设置ID后，该页面可被长按添加为桌面快捷方式 | 当前XML文件里不重复即可 | `a0001` |
| desc | 描述 | 文本内容 | 显示在标题下的小字，可以不设置 |
| desc-sh | 动态设置desc内容的脚本 | `脚本代码` | `echo '自定义的说明信息'` |
| summary | 高亮显示的摘要信息 | 文本内容 | `这是摘要` |
| summary-sh | 动态设置summary内容的脚本 | `脚本代码` | `echo '自定义的摘要信息'` |
| visible | 自定义脚本，输出1或0，决定该功能项是否显示 | 脚本代码 | `echo '1'` |
| logo | 作为快捷方式添加到桌面时使用的图标 | 文件路径 |  |
| icon | 显示在功能左侧的图标。如果未设置logo属性，它也同时会被作为logo使用 | 文件路径 |  |

> 注意：仅限定义了config、config-sh的page才能成为一个有效的快捷方式！

- 特有属性

| 属性 | 名称 | 用途 |
| :-: | :-: | :- |
| config | 配置 | 指定另一个配置文件作为子页面的内容 |
| html | 网页 | 指定一个网页作为子页面的内容 |
| activity | 活动名称 | 点击后要打开的activity，格式为`action`或`packageName/activityClass` |
| link | 链接地址 | 点击后要打卡的网页地址(将调用浏览器) |
| config-sh | 动态配置 | 写一段脚本，输出配置文件所在路径或完整内容 |
| before-load | 读取配置前 | 指定在加载配置文件前要执行的代码（仅限使用config、config-sh的page定义有效） |
| after-load | 读取配置后 | 指定配置文件读取完成后要执行的代码（仅限使用config、config-sh的page定义有效） |

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
<page config-sh="echo '/sdcard/text.xml'">
    <title>测试config-sh【路径输出】</title>
    <desc>通过config-sh输出配置页所在位置</desc>
</page>

<!--输出配置文件内容（框架识别到输出内容以“<?xml”开头 且以“>”结尾，按照此模式解析）-->
<page config-sh="cat /sdcard/text.xml">
    <title>测试config-sh【全文输出】</title>
    <desc>通过config-sh输出配置页内容</desc>
</page>
```

- **activity** 示例
```xml
<!--格式之一：activity=[action] -->
<page activity="com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS">
    <title>开发者选项</title>
</page>

<!--格式之二：activity=[packageName/activityClass] -->
<page activity="com.android.settings/.Settings$MemorySettingsActivity">
    <title>内存使用情况</title>
</slice>
```

### 页面的菜单
- 你可以为`子页面`定制右上角菜单、悬浮按钮
- 并单独设置一个脚本`handler`来处理菜单和按钮点击事件
- 为了区别多个菜单按钮，你可以为`menu`指定`id`
- 例如：

```xml
<page config="samples/configs/empty.xml">
    <title>在页面右上角显示菜单</title>
    <menu type="default" id="AAA">自定义 2</menu>
    <menu type="file" id="BBB" style="fab">选择文件</menu>
    <menu type="refresh">刷新界面</menu>
    <menu type="finish">关闭页面</menu>
    <menu type="file">选择文件</menu>
    <handler>
        echo '点击的菜单项' $menu_id
        echo '选择的文件' $file
    </handler>
</page>
```

#### 为page定义menu节点
> PIO做了一些语法兼容，所以下面几种写法都可以识别且没有区别

```xml
<menu>菜单选项</menu>
<menu-item>菜单选项</menu-item>
<option>菜单选项</option>
<page-option>菜单选项</page-option>
```

#### menu的属性

| 属性 | 作用 | 有效值 | 示例 |
| :- | :- | :- | :-: |
| auto-off、auto-close | 执行完handler代码以后关闭日志输出 | `true` `false` | `true` |
| auto-finish | 执行完handler代码以后关闭当前页面 | `true` `false` | `true` |
| reload-page | 执行完handler代码以后刷新当前页面 | `true` `false` | `true` |
| interruptible | 是否可中断handler执行 | `true` `false` | `true` |
| id | 用于区别多个menu的标识 | 任意不重复的id | `A001` |
| type | 用于区别多个menu的标识 | 详见下方说明 | `default` |
| style | 菜单显示方式，设为`fab`时将显示未悬浮按钮 | `default` `fab` | `fab` |
| icon | 悬浮按钮的图标文件路径 | 文件路径 | samples/icons/icon-test.png |

- 注意：目前的版本，同一个Page只能设置一个`style=fab`的菜单按钮！

##### menu的type属性

| 取值 | 作用 |
| :- | :- |
| default | 默认的按钮类型，点击后将menu的id传给handler处理 |
| refresh、reload | 点击后刷新当前子页面，不会执行handler |
| exit、finish、close | 点击后关闭当前子页面，不会执行handler |
| file | 点击后需要用户选取文件，再将文件路径和menu的id传给handler |

#### handler 处理菜单点击事件
> PIO做了一些语法兼容，所以下面几种写法都可以识别且没有区别

```xml
<handler>echo $menu_id</handler>

<!--你可以和picker、switch一样统一使用set节点来写处理脚本
并且可以直接使用state变量-->
<set>echo $state</set>

<script>echo $menu_id</script>
```

- handler会收到 `menu_id`、`state` 和 `file`（其中`menu_id`值`state`是相同的）
- 例如：

```xml
<handler>
    echo '点击的菜单项' $menu_id
    echo '选择的文件' $file
</handler>
```


### 补充说明
- 为与action、switch定义语法保持一致，title、desc也可以作为page下的节点定义

```xml
<page config="file:///android_asset/config_xml/for_miui.xml">
  <title>MIUI专属</title>
  <desc>用于Xiaomi MIUI的选项</desc>
</page>
```
