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
| group | 分组 | 定义在页面中，用于对action和switch进行分组 |
| resource | 资源 | 定义小容量的静态资源文件，解析配置时自动提取 |

## 页面列表
- 如果你不修改程序源码，框架将默认从解析`pages.xml`开始
- pages.xml的配置格式如：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<pages>
    <page title="原生专属"
        desc="越接近AOSP越适用的选项"
        config="file:///android_asset/config_xml/for_aosp.xml" />
    <page title="Flyme专属"
        desc="用于Meizu Flyme系统的选项"
        config="file:///android_asset/config_xml/for_flyme.xml" />
</pages>
```

- 属性说明

| 属性 | 名称 | 用途 | 是否可空 |
| :-: | :-: | :- | :-: |
| title | 标题 | 功能主标题，建议不要为空 | 是 |
| desc | 描述 | 显示在标题下的小字，可以不设置 | 是 |
| config | 配置 | 页面具体功能的配置文件路径 | 否 |
| support | 自定义脚本使用echo输出1或0，用于决定该action要不要显示 | 脚本代码 | 否 | `echo '1'` |

- **注意：** config只支持读取应用assets下的文件，其中`file:///android_asset/`是固定前缀，也可以省略不写，就像这样：

```xml
<page title="标题文本"
    desc="描述文本"
    config="config_xml/for_aosp.xml" />
```

> support 是个共通属性，适用于page、action、switch，将在后文单独介绍


### 补充说明
- 为了更友好的设置较长的desc内容，这里做了额外兼容，page节点也允许写成这样
```xml
<page>
  <title>MIUI专属</title>
  <desc>用于Xiaomi MIUI的选项</desc>
  <config>file:///android_asset/config_xml/for_miui.xml</config>
</page>
```


## 页面内容
- page的定义格式大体如下，只需要**囫囵吞枣**瞄一眼
- action和switch的具体配置会在后文介绍

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<page>
    <!--定义一个点击后执行的动作-->
    <action>
        <!-- ... 此处省略action的功能细节 -->
    </action>

    <!--定义一个开关选项-->
    <switch>
        <!-- ... 此处省略action的功能细节 -->
    </switch>
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
        <title>功能标题</title>
        <desc>这是描述信息</desc>
        <script>
            echo 'Hello world！'
        </script>
    </action>
</page>
```

### action 的 属性
| 属性 | 作用 | 有效值 | 必需 | 示例 |
| - | - | - | :-: | :- |
| confirm | 是否在执行操作前让用户确认，默认`false` | `true`、`false` | 否 | `false` |
| support | 自定义脚本使用echo输出1或0，用于决定该action要不要显示 | 脚本代码 | 否 | `echo '1'` |
| interruptible | 是否允许中断执行，默认`true` | `true`、`false` | 否 | `false` |
| auto-off | 脚本执行完后，是否执行完自动关闭日志输出界面，默认`false` | `true`、`false` | 否 | `false` |

> 示例
```xml
<action confirm="true">
    <!-- 此处省略 -->
</action>
```
> support 是个共通属性，适用于page、action、switch，将在后文单独介绍


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
        <script>file:///android_asset/scripts/test.sh</script>
    </action>
    <action>
        <title>脚本执行器测试</title>
        <desc>测试脚本执行器，直接执行代码段</desc>
        <script>
            echo '现在，开始执行脚本了！';
            testvalue='1'
            echo 'testvalue=$testvalue';
            echo '好了，代码执行完毕！';
        </script>
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
| desc | 参数的描述（标题） | 否 | `请选择` |
| type | 输入类型，默认为普通文本，可配置为`int`(数字) `bool`(勾选框) | 否 | `int` |
| readonly | 设为readonly表示只读，阻止输入 | 否 | `readonly` | 
| maxlength | 输入长度限制（位） | 否 | `10` |

> 请不要在`value-sh`、`options-sh` 属性里写大段的shell脚本，推荐方式请参考后文**脚本使用**部分

- 基本示例：

```xml
<action>
    <title>自定义DPI</title>
    <desc>允许你自定义手机DPI，1080P屏幕推荐DPI为400~480，设置太高或太低可能导致界面崩溃！</desc>
    <script>wm density $dpi;</script>
    <!--通过params定义脚本执行参数-->
    <params>
        <param name="dpi" desc="请输入DPI" type="int" value="480" />
    </params>
</action>
```

#### param 的 value-sh属性
- 例如，你需要在用户输入前动态获取当前已设置的值

```xml
<action>
    <title>自定义DPI</title>
    <desc>允许你自定义手机DPI，1080P屏幕推荐DPI为400~480，设置太高或太低可能导致界面崩溃！</desc>
    <script>wm density $dpi;</script>
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
    <script>
        echo "mode参数的值：$mode"
        if [ "$mode" = "time_center" ]; then
            echo '刚刚点了 时间居中'
        else
            echo '刚刚点击了 默认布局'
        fi;
    </script>
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
    <script>
        wm density $dpi;
        wm size ${width}x${height};
    </script>
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
    <script>
        wm size reset;
        wm density $dpi;
        busybox killall com.android.systemui;
    </script>
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
        <getstate>file:///android_asset/switchs/booster_get.sh</getstate>
        <setstate>file:///android_asset/switchs/booster_set.sh</setstate>
    </switch>
    <switch>...</switch>
    ...
</page>
```

### switch > desc
- 配置方式与 Action 的 desc相同，不再重复描述

### switch > getstate
- 自定义一段脚本，通过echo输出结果，（1或0，1表示选中），如 echo '1'

### switch > setstate
- 自定义一段脚本，用户切换选中状态时传入1或0，并执行代码
- 你可以通过参数 $state 来获取当前是否选中（1或0，1表示选中）

### 示例

```xml
<switch>
    <title>模拟全面屏</title>
    <desc>显示设置中的“全面屏”选项</desc>
    <getstate>
        if [ `grep qemu\.hw\.mainkeys= /system/build.prop|cut -d'=' -f2` = 1 ]; then
            echo 0;
        else
            echo 1;
        fi;
    </getstate>
    <setstate>
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
    </setstate>
</switch>
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
        <script>
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
        </script>
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
- 适用场景：所有定义脚本的位置（sh、value-sh、script、options-sh、getstate、setstate 等）

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
- 定义在page、action、switch以及action中的param节点
- 你需要写一段脚本，输出`0`或者`1`，用来表示该功能是否支持（支持则显示，否则隐藏）
- 例如，下面的例子，通过一个名叫`is_huawei_mphone.sh`的脚本文件来判断`action`是否要显示

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<items>
    <action support="file:///android_asset/is_huawei_mphone.sh">
        <title>行为1</title>
        <desc>行为1的说明</desc>
        <script>
            echo '脚本执行了呢'
        </script>
    </action>
</items>
```

- 又或者，直接在定义pege的时候就加上support属性
- 例如：
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<pages>
    <page
        support="file:///android_asset/is_huawei_mphone.sh"
        title="华为专用"
        desc="只适用于华为手机的选项"
        config="file:///android_asset/config_xml/for_huawei.xml" />
</pages>
```

