## Action

### 用途
- 点击后执行一段代码
- 允许通过参数定义，在执行代码前，让用户进行一些选择

### 入门 Hello world！
- 先从最简单的开始
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

#### Action 定义参数
- 用于需要用户“输入内容” 或 “作出选择”的场景

##### param 属性

| 属性 | 用途 | 示例 |
| - | - | - |
| name | 参数名，不可重复`必需！` | `param0` |
| value | 初始值 | ` ` |
| value-sh | 使用脚本通过echo输出设置参数初始值 | ` ` |
| options-sh | 使用脚本通过echo输出来生成 option | ` ` |
| title | 参数的标题，显示在输入框顶部 | `任意提示文字` |
| label | 参数的标题，显示在输入框左侧 | `任意提示文字` |
| placeholder | 显示在文本输入框的水印文字，可用作输入为空时的提示 | `请在此处输入文字` |
| desc | 参数的描述，显示在输入框下方 | `任意提示文字` |
| type | 输入类型，具体见下文 | `int` |
| readonly | 设为readonly表示只读，阻止输入 | `readonly` | 
| maxlength | 输入长度限制（位）适用于文本输入 | `10` |
| min | 输入的最小值，适用于数字输入和seekbar | `10` |
| max | 输入的最大值，适用于数字输入和seekbar | `100` |
| required | 是否为必填参数，可配置为`true`、`false` | `true` |
| suffix | 限制可选择的文件后缀，仅限`type=file`时使用 | `zip` |
| mime | 限制可选择的文件MIME类型，仅限`type=file`时使用 | `application/zip` |

> param 的`type`列举如下：

| 类型 | 描述 | 取值 |
| - | :- | - |
| int | 整数输入框，可配合`min`、`max`属性使用 | `min`和`max`之间的整数 |
| number | 带小数的数字输入框，可配合`min`、`max`属性使用 | `min`和`max`之间的数字 |
| checkbox | 勾选框 | `1`或`0` |
| switch | 开关 | `1`或`0` |
| seekbar | 滑块，**必需**配合`min`、`max`属性使用 | `min`和`max`之间的整数 |
| file | 文件路径选择器，建议配合`suffix`或`mime`属性使用 | 选中文件的绝对路径 |
| color | 颜色输入和选择界面 | 输入形如`#445566`或`#ff445566`的色值 |
| text | 任意文本输入（默认） | 任意自定义输入的文本 |

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

##### param 的 value-sh属性
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


##### param > option
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


##### param 输入长度限制

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

##### param > option 动态列表
- 现在允许更灵活的定义Param的option列表了，通过使用脚本代码输出内，即可实现
- 脚本的执行过程中的输出内容，每一个each将作为一个选项，如 echo '很小'; echo '适中';
- 如果你需要将选项的value（值）和label（显示文字）分开
- 用“|”分隔value和label即可，如：echo '380|很小'

```xml
<action desc-sh="echo '快速调整手机DPI，不需要重启，当前设置：';echo `wm density`;">
    <title>调整DPI</title>
    <desc>快速调整手机DPI，不需要重启</desc>
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

##### param 多选列表
- 设置了`option`或`option-sh`的情况下，在`param`节点添加`multiple="true"`属性
- 即可将原来的单选模式切换为多选模式，例如：

    ```xml
    <action>
        <title>多选下拉</title>
        <param name="test" label="多选下拉" multiple="multiple">
            <option value="Z">测试一下 Z</option>
            <option value="X">测试一下 X</option>
        </param>
        <set>echo '数值为：' $test</set>
    </action>
    ```

- 默认设置下，多选列表的各个值用换行分隔，得到的参数可能是这样的
    ```sh
    value="
    aaa
    bbb
    "
    ```
- 可有时候，你希望得到的值是 `value="aaa,bbb"` 这样的？
- 其实你可以通过`separator`属性自定义分隔符，例如：
    ```xml
    <action>
        <title>多选下拉</title>
        <param name="test" label="多选下拉" multiple="multiple" separator=",">
            <option value="Z">测试一下 Z</option>
            <option value="X">测试一下 X</option>
        </param>
        <set>echo '数值为：' $test</set>
    </action>
    ```

#

---

> 相关说明

- 由于在xml中写大量的shell代码非常不方便，也不美观，
- 建议参考 [`脚本使用`](./Script.md) 中的说明，
- 将`visible`属性需要执行的代码，写在一个单独的文件中。

> 文件选择器的类型限制说明
- 通过`suffix(后缀)`限制文件选择类型，并不是Android原生的机制，因此为了实现该目的，PIO自带了文件选择器来实现此目的。
- 而通过`mime`类型限制选择文件类型则符合Android本身的设定，但遗憾一般的文件浏览器只识别极少的`mime`类型。
