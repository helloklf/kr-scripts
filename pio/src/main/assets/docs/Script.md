
## 脚本使用
- 本章说明适用于，所有支持写 `脚本` 的配置项和属性
- 你将可以通过以下几种方式来安排脚本代码：

### 如何在XML中填写脚本

#### 1. 在xml中直接写脚本代码

```xml
<action>
  <title>示例代码</title>
  <desc sh="echo -n '当前设备型号：'; getprop ro.product.device;" />
  <set>
    echo '啊啊啊，你点我干啥！'
  </set>
</action>
```

#### 2. 将脚本作为单独的文件
- 显然在XML里很难写大量的逻辑，既不美观，语法也经常收到影响
- 试着使用`resource`将放到`assets`的脚本文件提取出来，并在`action`里使用
- 例如：

```xml
<!--假设我们的脚本放在assets下一个叫scripts的文件夹-->
<resource dir="file:///android_asset/scripts" />

<action>
  <title>执行脚本文件</title>
  <!--
    $START_DIR/ 不是非必要的，大多数情况下即使你
    简写成 <set> scripts/script_file.sh</set>
    也能正确的访问到提取出来的脚本文件
  -->
  <set>sh $START_DIR/scripts/script_file.sh</set>
</action>
```

#### 3. 在xml中写脚本所在位置（`不再推荐`）
- 当配置解析器检测到你所写的文本内容以`file:///android_asset/`开头
- 就会自动到apk的assets目录下去寻找对应文件，
- 虽然目前你依然可以这么用，但不再建议这样用
- 例如：

```xml
<action>
  <title>示例代码</title>
  <desc sh="file:///android_asset/test.sh" />
</action>
```

- 此时，你只需要把脚本内容写入到一个名为`test.sh`的文件中，放在apk的`assets`目录下即可
- 解析器会自动帮你提取并运行这个脚本


### 参数变量
- PIO会将 **由用户输入的参数** 封装为 `全局变量` 供脚本读取
- 除了你自定义的参数，和 `switch`、`picker` 固定的 `$state`参数
- PIO还提供了一些框架级的特殊变量可供使用

#### 全局(固定)变量
- 占位符定义在`executor.sh`中，PIO解析`kr-script.conf`时

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

#### 特殊(动态)变量
- 这几个较为特殊的变量是在3.9.2版本中新增的
- 用于定位XML配置来源路径和释放路径

| 参数名 | 说明 |
| - | - |
| PAGE_CONFIG_DIR | 页面(Page)的配置文件源目录 |
| PAGE_CONFIG_FILE | 页面(Page)的配置文件源文件 |
| PAGE_WORK_DIR | 页面(Page)的配置文件提取目录 |
| PAGE_WORK_FILE | 页面(Page)的配置文件提取路径 |

> 有何作用？<br />
> 两个源路径可以帮助你判断PIO是否加载正确的配置文件<br />
> 而后两个`WORK`变量，则是为了尽可能减少写死的固定路径，便于逻辑脚本和配置xml一同迁移目录


## 相关说明
> 如果你在脚本里创建了目录，建议将所有者设置为 `$APP_USER_ID` <br/>
> 原因和用法见 [resource](#/doc?doc=/docs/Resource.md) 的 **警告** 部分

