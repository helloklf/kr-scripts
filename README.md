## 简介

### 功能描述
- 这是一个简单的框架，让你通过xml + shell脚本就能组织起一个工具软件
- 只要你会写shell脚本，这将都会很简单
- 解压apk包，替换assets中的静态文件，即可完成功能修改
- 而不需要繁琐的反编译回编译操作！
- **注意：本说明仅适用于3.0.0及以后的版本！**

### 主要组成

| 定义 | 作用 | 描述 |
| :-: | :-: | :- |
| page | 页面 | 一个xml配置文件，就是一个页面，里面可以包含action和switch |
| action | 动作 | 定义在页面中的具体功能项，表现为：点击后执行某些操作 |
| switch | 开关 | 定义在页面中的具体功能项，表现为：点击后切换开关状态 |
| picker | 单选 | 比switch能提供更多可选项，表现为：点击后弹出单选列表 |
| text | 文本 | 用于显示文本文字 |
| group | 分组 | 用于包裹其它(action、switch等功能节点)实现分组效果 |
| resource | 资源 | 定义小容量的静态资源文件，解析配置时自动提取 |

## Page 节点
- 指定一个配置文件 `[config]` 或网页 `[html]` 作为一个子页面入口

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<pages>
    <page title="原生专属"
        desc="越接近AOSP越适用的选项"
        config="file:///android_asset/config_xml/for_aosp.xml" />
    <page title="Flyme专属"
        desc="用于Meizu Flyme系统的选项"
        html="https://www.lanzous.com/b838135" />
</pages>
```

- 属性说明

| 属性 | 名称 | 用途 |
| :-: | :-: | :- | :-: |
| title | 标题 | 功能主标题，建议不要为空 |
| desc | 描述 | 显示在标题下的小字，可以不设置 |
| support | 是否支持 | `support` 属性用法会在后面单独介绍 |
| config | 配置 | 指定另一个配置文件作为子页面的内容 |
| html | 网页 | 指定一个网页作为子页面的内容 |

- **注意：** config只支持读取应用assets下的文件，其中`file:///android_asset/`是固定前缀，也可以省略不写，就像这样：

```xml
<page title="标题文本"
    desc="描述文本"
    config="config_xml/for_aosp.xml" />
```

### 补充说明
- 为与action、switch定义语法保持一致，title、desc也可以作为page下的节点定义

```xml
<page config="file:///android_asset/config_xml/for_miui.xml">
  <title>MIUI专属</title>
  <desc>用于Xiaomi MIUI的选项</desc>
</page>
```

## Action

### 用途
- 点击后执行某些动作
- 允许通过参数定义，在执行前让用户输入或进行一些选择

### 入门 Hello world！
- 先从最简单的开始，显示一个标题和描述文本
- 点击后执行一段脚本（输出Hello world！）

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<page>
    <action>
        <title>Hello world！</title>
        <desc>点击我，用脚本输出Hello world！</desc>
        <set>
            echo 'Hello world！'
        </set>
    </action>
</page>
```

### action 的 属性
| 属性 | 作用 | 有效值 | 必需 | 示例 |
| - | - | - | :-: | :- |
| confirm | 是否在执行操作前让用户确认，默认`false` | `true`、`false` | 否 | `false` |
| support | 自定义一段脚本，用于检测该功能是否支持 | 由脚本输出 `1` 或 `0` | 否 | `echo '1'` |
| interruptible | 是否允许中断脚本执行，默认`true` | `true`、`false` | 否 | `false` |
| auto-off | 脚本执行完后，是否执行完自动关闭日志输出界面，默认`false` | `true`、`false` | 否 | `false` |


### 动态 desc
- 如果你希望执行一段脚本，将输出内容作为文本显示
- 那么，desc的`sh`属性就能满足需要

```xml
<action>
    <title>标题</title>
    <desc sh="echo '这是描述信息'"></desc>
</action>
```

> 请不要在`sh`属性里写大段的shell脚本，推荐方式请参考后文**脚本使用**部分

### 简单的action示例
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<page>
    <action>
        <title>脚本执行器测试</title>
        <desc>测试脚本执行器，执行内嵌的脚本文件</desc>
        <set>file:///android_asset/scripts/test.sh</set>
    </action>
    <action>
        <title>脚本执行器测试</title>
        <desc>测试脚本执行器，直接执行代码段</desc>
        <set>
            echo '现在，开始执行脚本了！';
            testvalue='1'
            echo 'testvalue=$testvalue';
            echo '好了，代码执行完毕！';
        </set>
    </action>
</page>
```

### Action 定义参数
- 用于需要用户“输入内容” 或 “作出选择”的场景

#### param 属性

| 属性 | 用途 | 必需 | 示例 |
| - | - | :-: | - |
| name | 参数名，不可重复 | 是 | `param0` |
| value | 初始值 | 否 | ` ` |
| value-sh | 使用脚本通过echo输出设置参数初始值 | 否 | ` ` |
| options-sh | 使用脚本通过echo输出来生成 option | 否 | ` ` |
| title | 参数的标题，显示在输入框顶部 | 否 | `任意提示文字` |
| label | 参数的标题，显示在输入框左侧 | 否 | `任意提示文字` |
| desc | 参数的描述，显示在输入框下方 | 否 | `任意提示文字` |
| type | 输入类型，具体见下文 | 否 | `int` |
| readonly | 设为readonly表示只读，阻止输入 | 否 | `readonly` | 
| maxlength | 输入长度限制（位）适用于文本输入 | 否 | `10` |
| min | 输入的最小值，适用于数字输入和seekbar | 否 | `10` |
| max | 输入的最大值，适用于数字输入和seekbar | 否 | `100` |
| required | 是否为必填参数，可配置为`true`、`false` | 否 | `true` |

> param 的`type`列举如下：

| 类型 | 描述 | 取值 |
| - | :- | - |
| int | 整数输入框，可配合`min`、`max`属性适用 | `min`和`max`之间的整数 |
| number | 带小数的数字输入框，可配合`min`、`max`属性适用 | `min`和`max`之间的数字 |
| checkbox | 勾选框 | `1`或`0` |
| switch | 开关 | `1`或`0` |
| seekbar | 滑块，**必需**配合`min`、`max`属性适用 | `min`和`max`之间的整数 |
| file | 文件路径选择器 | 选中文件的绝对路径 |
| color | 颜色输入和选择界面 | 输入形如`#445566`或`#ff445566`的色值 |
| text | 任意文本输入（默认） | 任意自定义输入的文本 |


> 请不要在`value-sh`、`options-sh` 属性里写大段的shell脚本，推荐方式请参考后文**脚本使用**部分

> param的type设为`seekbar`时，必需设置`min`和`max`属性！！

- 基本示例：

```xml
<action>
    <title>自定义DPI</title>
    <desc>允许你自定义手机DPI，1080P屏幕推荐DPI为400~480，设置太高或太低可能导致界面崩溃！</desc>
    <set>wm density $dpi;</set>
    <!--通过params定义脚本执行参数-->
    <params>
        <param name="dpi" desc="请输入DPI" type="int" max="96" min="160" value="480" />
    </params>
</action>
```

#### param 的 value-sh属性
- 例如，你需要在用户输入前动态获取当前已设置的值

```xml
<action>
    <title>自定义DPI</title>
    <desc>允许你自定义手机DPI，1080P屏幕推荐DPI为400~480，设置太高或太低可能导致界面崩溃！</desc>
    <set>wm density $dpi;</set>
    <!--通过params定义脚本执行参数-->
    <params>
        <param name="dpi" desc="请输入DPI" type="int" value-sh="echo '480'" />
    </params>
</action>
```


#### param > option
- 通过在param 下定义 option，实现下拉框候选列表

```xml
<action>
    <title>切换状态栏风格</title>
    <desc>选择状态栏布局，[时间居中/默认]</desc>
    <!--可以在script中使用定义的参数-->
    <set>
        echo "mode参数的值：$mode"
        if [ "$mode" = "time_center" ]; then
            echo '刚刚点了 时间居中'
        else
            echo '刚刚点击了 默认布局'
        fi;
    </set>
    <!--params 用于在执行脚本前，先通过用户交互的方式定义变量，参数数量不限于一个，但不建议定义太多-->
    <params>
        <param name="mode" value="default" desc="请选择布局">
            <!--通过option 自定义选项
                [value]=[当前选项的值] 如果不写这个属性，则默认使用显示文字作为值-->
            <option value="default">默认布局</option>
            <option value="time_center">时间居中</option>
        </param>
    </params>
</action>
```


#### param 输入长度限制

```xml
<action>
    <title>自定义DPI</title>
    <desc>允许你自定义手机DPI，1080P屏幕推荐DPI为400~480，设置太高或太低可能导致界面崩溃！</desc>
    <set>
        wm density $dpi;
        wm size ${width}x${height};
    </set>
    <params>
        <param name="dpi" desc="请输入DPI，推荐值：400~480" type="int" value="440" maxlength="3" />
        <param name="width" desc="请输入屏幕横向分辨率" type="int" value="1080" maxlength="4" />
        <param name="height" desc="请输入屏幕纵向向分辨率" type="int" value="1920" maxlength="4" />
    </params>
</action>
```

#### param > option 动态列表
- 现在允许更灵活的定义Param的option列表了，通过使用脚本代码输出内，即可实现
- 脚本的执行过程中的输出内容，每一个each将作为一个选项，如 echo '很小'; echo '适中';
- 如果你需要将选项的value（值）和label（显示文字）分开
- 用“|”分隔value和label即可，如：echo '380|很小'

```xml
<action>
    <title>调整DPI</title>
    <desc sh="echo '快速调整手机DPI，不需要重启，当前设置：';echo `wm density`;" polling="2000">快速调整手机DPI，不需要重启</desc>
    <set>
        wm size reset;
        wm density $dpi;
        busybox killall com.android.systemui;
    </set>
    <params>
        <param name="dpi" value="440" options-sh="echo '380|很小';echo '410|较小';echo '440|适中';echo '480|较大';" />
    </params>
</action>
```


## Switch
### 用途
- 简洁的开关选项

### Switch 属性（与Action相同）
| 属性 | 作用 | 有效值 | 必需 | 示例 |
| - | - | - | :-: | :- |
| confirm | 配置是否在运行脚本前弹出确认提示框，默认`false` | `true`、`false` | 否 | `false` |
| support | 自定义脚本使用echo输出1或0，用于决定该action要不要显示 | 脚本代码 | 否 | `echo '1'` |
| interruptible | 是否允许中断执行，默认`true` | `true`、`false` | 否 | `false` |
| auto-off | 脚本执行完后，是否执行完自动关闭日志输出界面，默认`false` | `true`、`false` | 否 | `false` |

### 添加Switch到页面
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<page>
    <switch>
        <title>流畅模式</title>
        <desc>在正常负载的情况下优先使用大核，大幅提高流畅度，但会降低续航能力</desc>
        <get>file:///android_asset/switchs/booster_get.sh</get>
        <set>file:///android_asset/switchs/booster_set.sh</set>
    </switch>
    <switch>...</switch>
    ...
</page>
```

### switch > desc
- 配置方式与 Action 的 desc相同，不再重复描述

### switch > get
- 自定义一段脚本，通过echo输出结果，（1或0，1表示选中），如 echo '1'

### switch > set
- 自定义一段脚本，用户切换选中状态时传入1或0，并执行代码
- 你可以通过参数 $state 来获取当前是否选中（1或0，1表示选中）

### 示例

```xml
<switch>
    <title>模拟全面屏</title>
    <desc>显示设置中的“全面屏”选项</desc>
    <get>
        if [ `grep qemu\.hw\.mainkeys= /system/build.prop|cut -d'=' -f2` = 1 ]; then
            echo 0;
        else
            echo 1;
        fi;
    </get>
    <set>
        busybox mount -o remount,rw -t auto /system;
        if [ $state == 1 ];then
            sed -i 's/^qemu\.hw\.mainkeys.*/qemu.hw.mainkeys=0/g' /system/build.prop
            echo '全面屏手势开启成功'
        else
            sed -i 's/^qemu\.hw\.mainkeys.*/qemu.hw.mainkeys=1/g' /system/build.prop
            echo '全面屏手势关闭成功'
        fi
        sync;
        echo '重启后生效！'
    </set>
</switch>
```

## Picker (3.4.5+)
- `picker`是在3.4.5版本后增加的一个新功能，即加强版的`switch`
- `picker`和`switch`一样，通过`get`读取当前状态，通过`set`保存状态
- 其它配置项也和`switch`一致
- `picker`需要你自己定义选项(`option`)，就像这样

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

- 或者，如果你还希望通过脚本动态生成选项，picker也提供了`options-sh`属性
- 用法和action的param一样，如：

```xml
<picker options-sh="echo 'a|选项A'; echo 'b|选项B'">
    <title>测试单选界面</title>
    <desc>测试单选界面</desc>
    <getstate>getprop xxx.xxx.xxx3</getstate>
    <setstate>setprop xxx.xxx.xxx3 $state</setstate>
</picker>
```

## Text (3.4.5+)
- `text`是在3.4.5版本后增加的一个新功能，用于自定义纯文本节点
- `title` 和 `desc` 的显示样式会和其它功能节点保持一致
- 支持通过`support`属性判断是否**显示\隐藏**
- 如果你不需要自定义文本格式，那么按下面这种方式使用就行了

```xml
<text>
    <title>标题文本</title>
    <desc>小文本</desc>
</text>  
```

#### Text > Slice
- 除了支持`title`和`desc`，Text还单独增加了`slice`节点
- 如果你需要定义个性化的文本样式，`slice`节点有一些简单的样式属性可以使用

| 属性 | 说明 | 有效值 |
| - | - | - |
| bold **(简写: `b`)** | 是否加粗 | `true`、`false` |
| italic **(简写: `i`)** | 是否倾斜 | `true`、`false` |
| underline **(简写: `u`)** | 是否显示下划线 | `true`、`false` |
| break | 是否换行后显示 | `true`、`false` |
| size | 字体大小(dp) | 整数值 例如：`20` |
| align | 文字对齐 | `normal`、`center`、`right`、`left` |
| color | 文字颜色| #开头的十六进制色，如：`#445566` |
| background **(简写: `bg`)** | 文字背景色 | #开头的十六进制色，如：`#000000` |
| link **(或者: `href`)** | 文本链接，点击后打开网页 | 如 `http://vtools.omarea.com/` |
| activity **(简写: `a`)** | activity，点击后打开Activity | 如 `android.settings.APN_SETTINGS` |


> 注意：`align`属性的`left`、`right`目前只支持`Android P`及更高版本系统

- 使用示例
```xml
<text>
    <slice bold="true">显示为加粗</slice>
    <slice italic="true">显示为斜体</slice>
    <slice break="true">换行</slice>
    <slice bold="true" italic="true">显示为粗斜体</slice>
    <slice size="20">字体显示为20dp</slice>
    <slice color="#ff0000">显示为红色</slice>
    <slice link="http://vtools.omarea.com/">Scene 官网</slice>
    <slice activity="android.settings.APN_SETTINGS">打开APN设置</slice>
</text>
```


## 静态资源
- 在page功能配置文件的任意位置，通过resource标签定义公共资源
- 如果，你需要将一些公共的函数提取到单独的脚本中
- 或者，你的某个脚本需要一些静态资源文件，而你希望将静态资源集成到apk中
- 那你可以试试这个，例如：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<page>
    <resource file="file:///android_asset/resource/common.sh" />
    <resource file="file:///android_asset/resource/test_file.zip" />
    <action>
        <title>测试使用resource</title>
        <desc>试试使用resource导入公共函数库，并使用静态文件</desc>
        <set>
            # 点击action后执行的脚本
            # source是Linux shell自身语法，并非本应用所定制，因此其工作原理也不会有什么不同
            source ./resource/common.sh

            if [[ -f './resource/test_file.zip' ]]
            then
                echo '已找到所需的静态资源文件'
                # cp './resource/test_file.zip' /system/....
            else
                echo '资源文件丢失...'
            fi

            echo '>>> 测试完毕'
        </set>
    </action>
</page>
```

- 上面的例子中，是通过相对位置来使用resource的
- `resource`默认会提取到`/data/data/com.projectkr.shell/files/private/`目录下

> 当resource定义在action或switch内部，如果执行完support，发现设备并不受支持(输出为'0')，将不会再提取资源
> resource也可以出现在`pagelist.xml`中，而非仅限于单个page的功能配置文件

- 在3.0.3 之后，resource开始支持`dir`属性用于指定资源文件夹
- 框架会帮你提取这个文件夹内的所有文件，例如：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<pages>
    <resource dir="file:///android_asset/resource/common" />
    <page
        title="原生专属"
        desc="越接近AOSP越适用的选项"
        config="file:///android_asset/config_xml/for_aosp.xml" />
</pages>
```


## 分组
- 使用`group`标签的`title`属性，对功能进行分组
- 支持通过`support`属性判断是否**显示\隐藏**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<page>
    <group title="分组">
        <switch>
            <!-- ... 此处省略 switch 的详细定义 -->
        </switch>
        <switch>
            <!-- ... 此处省略 switch 的详细定义 -->
        </switch>
    </group>
</page>
```


## 脚本使用
- 在配置文件中你有两种方式来定义脚本
- 适用场景：所有定义脚本的位置（sh、value-sh、script、options-sh、get、set、support 等）
- 基本上所有能写脚本的属性或节点，都支持`file:///android_asset/...`路径使用assets中的脚本文件

> 1 直接写脚本代码（不推荐）
```
echo '1'
```
- 不适合大段的代码

> 2 写脚本文件路径（推荐）
```
file:///android_asset/test.sh
```
- 将你的脚本独立保存为 test.sh，放到apk 的 assets目录下
- 应用启动时会自动提取，并在需要时执行


## 进度
- 适用于action执行过程
- 通过特定格式的输出(`echo`)来显示进度
- 内容格式为`progress:[当前/总数]`
- 如：`echo "progress:[10/252]"`,表示当前进度为10/252
- `当前`与`总数`相同时，隐藏进度条
- `当前`为`-1`，总数为任意数值时，显示loading动画

```sh
# 进度显示为 10/252
echo "progress:[10/252]"

# 进度为完成（因此进度条）
echo "progress:[15/15]"

# 显示为不确定进度的动画
echo "progress:[-1/0]"
```

## support 属性
- 定义在page、group、action、switch、picker、text 以及 action中的param 节点
- 你需要写一段脚本，输出`0`或者`1`，用来表示该功能是否支持（支持则显示，否则隐藏）
- 例如，下面的例子，通过一个名叫`is_huawei_mphone.sh`的脚本文件来判断`action`是否要显示

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<group>
    <action support="echo 1">
        <title>华为专用功能</title>
        <desc>我这是华为手机专用的功能，如果不是华为手机则不可见</desc>
        <set>
            echo '脚本执行了呢'
        </set>
    </action>
</group>
```


### 插入网页作为Page
- 为了实现更加丰富的界面，在`3.3`版本中，新加入了HTML页面支持
- 通过类似于下面例子的方式，即可定义一个网络地址作为一个页面在软件里打开

```xml
<page title="在线页面测试" html="http://www.baidu.com/" />
```

- 当然，你也可以直接将html文件放入assets中，通过`file:///android_asset/`来访问

```xml
<page title="在线页面测试" html="file:///android_asset/example/index.html" />
```

#### 用于网页的api
- 在网页中，你可以通过以下接口来执行`shell`脚本，并获取结果
- 就像执行`support` `desc-sh` `option-sh` 部分的脚本一样
- 会按调用的先后顺序在同一个进程中执行
- 这些接口都定义在`KrScriptCore`对象下

##### KrScriptCore.extractAssets
- 用于提取assets下的资源文件
- 如果提取成功则返回提取后的磁盘路径
- 否则返回null
- 例如：

```javascript
var outputPath = KrScriptCore.extractAssets('file:///android_asset/kr-script/resource_test.txt')
if (outputPath != null) {
    alert('文件已提取到：' + outputPath)
} else {
    alert('所需的资源文件已丢失！')
}
```

##### KrScriptCore.rootCheck
- 检查是否已经获取root权限
- 返回 `true / false`
- 例如：

```javascript
var hasRoot = KrScriptCore.rootCheck()
if (hasRoot) {
    alert('用户已授予ROOT权限')
} else {
    alert('未获得ROOT权限')
}
```

##### KrScriptCore.executeShell
- 执行脚本，获取输出日志（不包括错误信息）
- 例如：

```javascript
// 执行shell代码，并立即返回结果（输出内容，不包含错误信息）
var result = KrScriptCore.executeShell("echo 'hello world!'")
alert('输出内容：' + result)
```

##### KrScriptCore.executeShellAsync
- 执行脚本，并以回调的方式返回输出日志
- `executeShellAsync`执行脚本时，会开启一个新的进程
- 就像执行`action`的`set`或`switch`的`set`部分一样
- 调用格式： `KrScriptCore.executeShellAsync([要执行的脚本], [日志回调函数名], [字符串化的参数对象])`
- 调用返回：是否调用成功（只表示是否成功启动进程，不表示执行代码时是否出现错误）
- 例如：

```javascript
var shellScript = "echo 'hello world!'\n sleep 1\n echo '执行完成咯~'"

window.callbackMethod = function (log) {
    // 在这里，你需要对 log.type 进行判断，确定输出类型
    // log.type 列举如下：
    // 2: 普通输出日志
    // 4: 异常输出日志
    // -2: 执行已结束
    // 当 log.type 为 -2 的回调时，就表示执行过程已经结束了
}

var successful = KrScriptCore.executeShellAsync(shellScript, "window.callbackMethod")
if (successful == true) {
    // 通常情况下，用户授予了root权限，就是调用成功的
} else {
    alert('执行脚本失败，请检查是否已经授予ROOT权限！')
}
```

- 再例如，通过`KrScriptCore.executeShellAsync`执行脚本时传入参数

```javascript
var params = JSON.stringify({
    param_one: '张飞'
});
var shellScript = "echo 我的名字叫：$param_one"

window.callbackMethod = function (log) {
    // 在这里，你需要对 log.type 进行判断，确定输出类型
    // log.type 列举如下：
    // 2: 普通输出日志
    // 4: 异常输出日志
    // -2: 执行已结束
    // 当 log.type 为 -2 的回调时，就表示执行过程已经结束了
}

var successful = KrScriptCore.executeShellAsync(shellScript, "window.callbackMethod", params)
if (successful == true) {
    // 通常情况下，用户授予了root权限，就是调用成功的
} else {
    alert('执行脚本失败，请检查是否已经授予ROOT权限！')
}
```

##### KrScriptCore.fileChooser
- 调用文件路径选择器

```javascript

window.fileChooserCallback = function (result) {
if (result.absPath) {
    alert('选中文件：' + result.absPath)
} else {
    alert('文件已丢失')
}
}
function chooseFile() {
KrScriptCore.fileChooser('window.fileChooserCallback')
}
```

##### 其它说明
> 通过 `executor.sh`
- `KrScriptCore`在底层依然会通过`executor.sh`执行脚本代码

> 支持 `file:///android_asset`
- `executeShell`和`executeShellAsync`除了执行正常的脚本代码，也支持调用assets中定义的脚本文件
- 如：

```javascript
var shellFile = "file:///android_asset/kr-script/test/var.sh"
var result = KrScriptCore.executeShell(shellFile)
alert('输出内容：' + result)
```

##### 代码迁移
- 举个例子来看看，如何在网页中还原`action`的`set`执行过程

> 通过`action`定义调用

```xml
<action>
    <title>新界面测试</title>
    <params>
        <param name="param_one" label="普通变量 param_one" value="张飞" />
    </params>
    <set>file:///android_asset/kr-script/test/var.sh</set>
</action>
```

> 通过网页调用

```javascript
var shellFile = "file:///android_asset/kr-script/test/var.sh"
window.callbackMethod = function (log) {
    /*...此处省略日志输出处理过程...*/
}
KrScriptCore.executeShellAsync(shellFile, "window.callbackMethod", JSON.stringify({
    param_one: "张飞"
}))
```
