## Action
- Action是在PIO里最基本也是最复杂的组件
- 它的基本用法是：设置一个用户点击以后会执行一段脚本的节点
- 它的拓展用法是：在执行脚本前允许用户先输入或做一些选择

### 入门 Hello world！
- 例如，这个例子就是它最简单的用法

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
- 其中，首行 `<?xml version="1.0" encoding="UTF-8" ?>` 是XML文档固定格式
- 而根节点 `page`，其实PIO并不关心根节点，也就是说，你写成下面这样也无所谓

    ```xml
        <?xml version="1.0" encoding="UTF-8" ?>
        <xxxxx>
            <action>
            <!--此处省略若干...-->
            </action>
        </xxxxx>
    ```


### Action的属性

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

#### Action 定义参数(param)
- 用于需要用户“输入内容” 或 “作出选择”的场景
- 例如，这里有个最简单的例子，在执行脚本前，需要用户输入自己的姓名

```xml
<action>
    <title>你叫什么名字？</title>
    <set>echo '我知道了，你叫：' $first_name</set>
    <param
        name="first_name"
        placeholder="请在这里输入您的姓名"
        value="张三" />
</action>
```

##### param 的属性
- 当然，`param` 的交互方式并不仅限于输入简单文本，来看看都可以设置什么

| 属性 | 用途 | 示例 |
| - | - | - |
| name | 参数名，不可重复 `必需！` | `param0` |
| value | 初始值 | ` ` |
| value-sh | 使用脚本通过echo输出设置参数初始值 | ` ` |
| options-sh | 使用脚本通过echo输出来生成 option | ` ` |
| title | 参数的标题，显示在输入框顶部 | `任意提示文字` |
| label | 参数的标题，显示在输入框左侧 | `任意提示文字` |
| placeholder | 显示在文本输入框的水印文字，可用作输入为空时的提示 | `请在此处输入文字` |
| desc | 参数的描述，显示在输入框下方 | `任意提示文字` |
| `type` | 输入类型，具体见下文 | `int` |
| readonly | 设为readonly表示只读，阻止输入 | `readonly` | 
| maxlength | 输入长度限制（位）适用于文本输入 | `10` |
| min | 输入的最小值，适用于数字输入和seekbar | `10` |
| max | 输入的最大值，适用于数字输入和seekbar | `100` |
| required | 是否为必填参数，可配置为`true`、`false` | `true` |
| suffix | 限制可选择的文件后缀，仅限`type=file`时使用 | `zip` |
| mime | 限制可选择的文件MIME类型，仅限`type=file`时使用 | `application/zip` |
| multiple | 是否允许多选(设置了options或type=app时可用) | `true` |
| separator | 多选模式下多个值的分隔符，默认为换行符 | `,` |
| editable | 是否允许用户手动输入路径（type为`file`或`folder`可用） | `true` |

- type设为`file` `folder` 时，用户只能调用路径选择器进行选择，
- 此时你可以通过将`editable`属性设为`true`来允许用户手动输入路径，
- 但这就意味着用户可能输入无效或并不存在的路径，你需要自行校验！


> param 的`type`列举如下：

| 类型 | 描述 | 取值 |
| - | :- | - |
| int | 整数输入框，可配合`min`、`max`属性使用 | `min`和`max`之间的整数 |
| number | 带小数的数字输入框，可配合`min`、`max`属性使用 | `min`和`max`之间的数字 |
| checkbox | 勾选框 | `1`或`0` |
| switch | 开关 | `1`或`0` |
| seekbar | 滑块，**必需**配合`min`、`max`属性使用 | `min`和`max`之间的整数 |
| file | 文件路径选择器，建议配合`suffix`或`mime`属性使用 | 选中文件的绝对路径 |
| folder | 目录选择器 | 选中目录的绝对路径 |
| color | 颜色输入和选择界面 | 输入形如`#445566`或`#ff445566`的色值 |
| app | 应用选择器（可配置为多选） | 选取的应用包名 |
| packages | 包名选择器（可配置为多选） | 选取的应用包名 |
| text | 任意文本输入（默认） | 任意自定义输入的文本 |

> param的type设为`seekbar`时，必需设置`min`和`max`属性！！<br />
> param的`type=packages`和`type=app`的区别在于，前者会显示用户未安装的应用选项（只要是包含在你指定的`option`列表里），而后者只会显示用户已安装的应用

##### param 示例
- 可配置的属性颇多，但不一定都用的上。来看看几个经典用法的示例

###### value-sh属性
- 比如，我们在显示一个开关按钮时，通常希望它默认的状态取决于上次的设置，而不是一个默认值
- 所以，我们希望用一段脚本去读取上次设置的记录，此时就需要用到 `value-sh`
- 例如：

```xml
<action>
    <title>显示触摸位置</title>
    <set>settings put system pointer_location $p0</set>
    <param
        name="p0"
        label="显示触摸位置"
        type="switch"
        value-sh="settings get system pointer_location" />
</action>
```

##### param 下拉类型
- param的下拉类型不由`type`指定，
- 而是判断是否有定义`option`子节点或`option-sh`属性来决定是否显示为下拉框
- `<option>` 只有一个`value`属性
- 例如：

```xml
<action>
    <title>切换状态栏风格</title>
    <set>
        if [ "$mode" = "time_center" ]; then
            echo '刚刚点了 时间居中'
        else
            echo '刚刚点击了 默认布局'
        fi;
    </set>
    <param name="mode" value="default">
        <!--通过option 自定义选项
            [value]=[当前选项的值] 如果不写这个属性，则默认使用文字内容作为值-->
        <option value="default">默认布局</option>
        <option value="time_center">时间居中</option>
    </param>
</action>
```

- 使用`option-sh`属性，你还可以用脚本输出选项，从而实现不同设备上显示不同的选项
- 在脚本中每一行输出会作为一个选项显示
- 如果你需要将选项的value（值）和label（显示文字）分开
- 用 `|` 分隔value和label即可，如：echo '380|很小'
- 如果没添加 `|` 分隔符，则整行输出作为显示文本，同时也作为值使用

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
        <param
            name="dpi"
            value-sh="wm density"
            options-sh="echo '380|很小';echo '410|较小';echo '440|适中';echo '480|较大';" />
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

- 默认设置下，多选列表的各个选中值用换行分隔，得到的数据是这样的
    ```sh
    value="
    aaa
    bbb
    "
    ```
- 其实只要在`param`节点添加 `separator="@"` 就能得到 `value="aaa@bbb"` 这样的数据

#

---

> 相关说明

- 由于在xml中写大量的shell代码非常不方便，也不美观，
- 建议参考 [`Script`](#/doc?doc=/docs/Script.md) 中的说明，
- 将`visible`属性需要执行的代码，写在一个单独的文件中。

> 文件选择器的类型限制说明
- 通过`suffix(后缀)`限制文件选择类型，并不是Android原生的机制，因此为了实现该目的，PIO自带了文件选择器来实现此目的。
- 而通过`mime`类型限制选择文件类型则符合Android本身的设定，但遗憾一般的文件浏览器只识别极少的`mime`类型。
