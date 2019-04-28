## 简介
1. 通过编写Xml，配置一个动作列表，点击选项时执行指定的shell脚本
2. 为了方便第三方ROM修改爱好者快速定制自己的个性化选项功能
3. 通过界面交互的方式，允许用户在执行脚本前进行一些输入或选择操作
4. 加入Get Set机制（分别执行两段脚本），从而在界面上显示Switch开关

## **注意：本说明仅适用于2.0.4 及以后的版本！**


## 使用脚本
- 在配置文件中你有两种方式来定义脚本
- 适用场景：所有定义脚本的位置（sh、value-sh、script、options-sh、getstate、setstate 等）

> 1 脚本内嵌（不推荐）
```
echo '1'
```
- 不适合大段的代码

> 2 脚本文件（推荐）
```
file:///android_asset/test.sh
```
- 将你的脚本独立保存为 test.sh，放到apk 的 assets目录下
- 应用启动时会自动提取，并在需要时执行

## Action
- 点击执行

### 定义Action
- 在assets下添加，actions.xml，格式如下：
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<actions>
    <action>
        <title>行为1</title>
        <desc>行为1的说明</desc>
        <script># 点击行为1要执行的脚本</script>
    </action>
    <action>...</action>
    ...
</actions>
```

### 入门：Hello world！

```xml
<!--action
        [confirm]=[true/false]，操作是否需要二次确认，避免误操作，默认为false
        [start]=[dir]，执行脚本时的起始位置，将在执行script前cd到起始位置，默认位置为/cache
-->
<action confirm="false">
    <title>功能标题</title>
    <desc>功能说明</desc>
    <!--script 点击后要执行的脚本（可以直接要执行的文件路径、或要执行的代码）
            内容将支持两种方式
            1.要执行的代码内容 如：echo 'hello world！'; echo '执行完毕！';
            2.assets内嵌资源文件，路径以file:///android_asset开头，如：file:///android_asset/test.sh，执行时会自动从 apk文件 的assets目录自动提取-->
    <script>echo 'hello world！'</scripts>
</action>
```

### 入门 action 属性
| 属性 | 作用 | 有效值 | 必需 | 示例 |
| - | - | - | :-: | :- |
| confirm | 配置是否在运行脚本前弹出确认提示框，默认`false` | `true`、`false` | 否 | `false` |
| start | 执行脚本的起始位置(相当于运行脚本前执行 `cd $start`，默认为工具箱的数据目录) | 任意路径 | 否 |`/cache` |
| support | 自定义脚本使用echo输出1或0，用于决定该action要不要显示 | 脚本代码 | 否 | `echo '1'` |

> 示例
```xml
<action confirm="true" start="/cache">
    <!-- 此处省略 -->
</action>
```

#### 入门 action > title
- 设置Action的标题，不支持动态值

> 示例
```xml
<action>
    <title>标题</title>
</action>
```

#### 入门 action > desc
- 设置Action的描述
- 方式1：静态值，示例
```xml
<action>
    <title>标题</title>
    <desc>这是描述信息</desc>
</action>
```

- 方式2：动态值，示例
```xml
<action>
    <title>标题</title>
    <desc sh="echo '这是描述信息'"></desc>
</action>
```

- 方式3：动态值，示例
```xml
<action>
    <title>标题</title>
    <desc sh="file:///android_asset/MyScript.sh"></desc>
</action>
```
> 将要执行的脚本单独写入一个脚本文件，这很适合需要执行大段代码的场景
> 如上例子，你只需将脚本MyScript.sh 放在apk文件 的assets目录下


#### 入门 action > script
- 设置Action被点击时要执行的脚本
- 方式1：直接在配置文件里写脚本
```xml
<action>
    <title>标题</title>
    <desc>这是描述信息</desc>
    <script>
        echo '我被执行了'
    </script>
</action>
```

- 方式2：使用单独的脚本文件
```xml
<action>
    <title>标题</title>
    <desc>这是描述信息</desc>
    <script>file:///android_asset/MyScript.sh</script>
</action>
```


#### 入门 简单的action示例
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<actions>
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
</actions>
```

### 参数 定义Action执行的参数
- 用于需要用户“输入内容” 或 “作出选择”的场景

#### action > params > param 属性

| 属性 | 用途 | 必须 | 示例 |
| - | - | :-: | - |
| name | 参数名 | 是 | `param0` |
| value | 初始值 | 否 | ` ` |
| value-sh | 使用脚本通过echo输出设置参数初始值 | 否 | ` ` |
| options-sh | 使用脚本通过echo输出来生成 option | 否 | ` ` |
| desc | 参数的描述（标题） | 否 | `请选择` |
| type | 输入类型，空表示为普通文本（默认），可配置为`int`(数字) `bool`(勾选框) | 否 | `int` |
| readonly | 只读，默认为"" | 否 | `readonly` | 
| maxlength | 最大输入长度（位） | 否 | `10` |

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

#### action > prams > param 动态获取value
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


#### action > params > param > option
- 通过在param 下定义 option，实现单选操作

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


#### action > params > param 文本输入的长度限制

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

### action > params > param > option 动态选项列表
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
- 开关项

### 入门 Switch 属性
| 属性 | 作用 | 有效值 | 必需 | 示例 |
| - | - | - | :-: | :- |
| confirm | 配置是否在运行脚本前弹出确认提示框，默认`false` | `true`、`false` | 否 | `false` |
| start | 执行脚本的起始位置(相当于运行脚本前执行 `cd $start`，默认为工具箱的数据目录) | 任意路径 | 否 |`/cache` |
| support | 自定义脚本使用echo输出1或0，用于决定该action要不要显示 | 脚本代码 | 否 | `echo '1'` |

#### 定义Switch列表
- 在assets目录下，添加 switchs.xml，格式如下
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<switchs>
    <switch>
        <!--开关标题-->
        <title>流畅模式</title>
        <!--开关说明，同样支持动态设置内容，可参考Action的Desc设置-->
        <desc>在正常负载的情况下优先使用大核，大幅提高流畅度，但会降低续航能力</desc>
        <!--在应用启动时获取状态，用于设定开关显示状态，确保执行时间不会太长，避免启动时界面未响应-->
        <getstate>file:///android_asset/switchs/booster_get.sh</getstate>
        <!--设置状态，直接脚本时通过$state读取参数，file:///assets_file 方式的脚本文件，则通过 $1 获取参数-->
        <setstate>file:///android_asset/switchs/booster_set.sh</setstate>
    </switch>
    <switch>...</switch>
    ...
</switchs>
```

#### switch > desc
- 配置方式与 Action 的 desc相同，不再重复描述

#### switch > getstate
- 自定义一段脚本，通过echo输出结果，（1或0，1表示选中），如 echo '1'

#### switch > setstate
- 自定义一段脚本，用户切换选中状态时传入1或0，并执行代码
- 你可以通过参数 $state来获取当前是否选中（1或0，1表示选中）

#### 示例

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

#### 公共资源
- 在actions和switchs下，通过resource标签定义公共资源
- 如果，你需要将一些公共的函数提取到单独的脚本中
- 或者，你的某个脚本需要一些静态资源文件，而你希望将静态资源集成到apk中
- 那你可以试试这个，例如：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<actions>
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
                echo '提取静态资源文件成功'
            else
                echo '资源文件丢失...'
            fi

            echo '>>> 测试完毕'
        </script>
    </action>
</actions>
```

- 上面的例子中，是通过相对位置来使用resource的
- 如果你定义了`action`或`switch`的`[start]`属性，那么你可能需要通过绝对路径来访问`resource`
- `resource`默认会提取到`/data/data/com.projectkr.shell/files/private/`目录下（仅供参考）
- 绝对路径可能不如相对路径稳妥~