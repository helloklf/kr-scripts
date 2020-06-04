
## 脚本使用
- 本章说明适用于，所有支持写 `脚本` 的配置项和属性
- 你将可以通过以下两种方式来安排脚本代码：

### 1. 在xml中直接写脚本代码

```xml
<action>
  <title>示例代码</title>
  <desc sh="echo -n '当前设备型号：'; getprop ro.product.device;" />
</action>
```

### 2. 在xml中填写脚本所在位置（**推荐**）
- 当配置解析器检测到你所写的文本内容以`file:///android_asset/`开头
- 就会自动到apk的assets目录下去寻找对应文件，例如：

```xml
<action>
  <title>示例代码</title>
  <desc sh="file:///android_asset/test.sh" />
</action>
```

- 此时，你只需要把脚本内容写入到一个名为`test.sh`的文件中，放在apk的`assets`目录下即可
- 解析器会自动帮你提取并运行这个脚本


### 3. 参数变量
- PIO会将 **由用户输入的参数** 封装为 `全局变量` 供脚本读取
- 除了你自定义的参数，和 `switch`、`picker` 固定的 `$state`参数
- PIO还提供了以下 `全局变量`：

| 参数名 | 说明 |
| - | - |
| EXECUTOR_PATH | 执行器入口文件所在位置 |
| START_DIR | 开始执行脚本的起始位置 |
| TEMP_DIR | 如果你有的脚本要生成临时文件，建议放在这个路径下 |
| ANDROID_UID | Android系统当前登录用户的ID |
| ANDROID_SDK | Android系统当前版本号(SDK Version) |
| SDCARD_PATH | SD卡(本机存储)所在路径 |
| PACKAGE_NAME | PIO框架应用的包名 |
| PACKAGE_VERSION_NAME | PIO框架应用的版本名称 |
| PACKAGE_VERSION_CODE | PIO框架应用的版本号 |
| TOOLKIT | 自带的命令行工具集安装目录 |
| APP_USER_ID | 安卓中每个应用都有一个单独的UserID |
| ROOT_PERMISSION | 是否已经获得ROOT权限，值为true/false |

> 其它说明：<br/>

> 如果你在脚本里创建了目录，建议将所有者设置为 `$APP_USER_ID` <br/>
> 原因和用法见 [resource](./Resource.md) 的 **警告** 部分


### 4. 其它思路
- 觉得通过`file:///android_asset/前缀`逐一指定脚本路径还不够简练？
- 不妨参考一下 [resource](./Resource.md) 章节
- 将可以得出以下例子的用法：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<page>
    <!--假设有个名为scripts目录，专门用于存放脚本，
        通过resource[dir]可以令框架一次性将其全部提取-->
    <resource dir="file:///android_asset/scripts" />
    <action>
        <title>试试通过resource提取的脚本</title>
        <set>
          $START_DIR/scripts/test_script.sh
        </set>
    </action>
</page>
```