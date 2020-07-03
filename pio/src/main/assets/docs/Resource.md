
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

##### 简略写法
- `resource` 的作用非常纯粹，就是提取 `assets` 而已
- 因此，你完全可以省略掉 `file:///android_asset/` 开头
    ```xml
    <!--完整-->
    <resource file="file:///android_asset/test_file.zip" />

    <!--简略-->
    <resource file="test_file.zip" />
    ```

#### 性能优化
- 出于性能考虑，框架不会重复提取同一个资源文件（直到APP完全重启）
- 如果你的资源文件会被重复使用，`请不要删除或移动已提取的静态资源！`
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
- 这是一些积累的经验

##### 不要将静态资源和(`toolkit`)放在一起

- 注意，千万不要将你的资源文件，和PIO自带的命令行工具集(`toolkit`)放在一起！！！
- 因为 `toolkit` 目录会在应用启动时提取，并做好相应的权限设置和特殊配置
- 而如果将静态资源放在 `toolkit` 所在目录，提取文件时可能会破坏运行环境！

    > 例如：`kr-script/executor.sh` 中有一些变量占位符<br />
    > 会在解析`kr-script.conf`时被替换为最终的数值，并提取到磁盘上<br />
    > 如果你又自行定义了`<resource dir=file:///android_asset/kr-script" />`<br />
    > 会导致PIO运行期间再次提取`executor.sh`，覆盖掉此前经过特殊处理的版本
> 

##### 注意通过脚本创建的目录所有者和权限

- 例如，你在脚本里 `mkdir -p $START_DIR/files` 创建了目录，
- 而同时又需要提取 `file:///android_asset/files` 下的文件，必然会提取失败！
- 框架本身是普通应用层权限，提取文件时无法写入所有者为`root`的`files`目录，从而导致失败
- 如果你真的需要创建与`resource`提取路径相同的文件夹，建议创建完后将所有者和所属组设置为`$APP_USER_ID`
- 示例如：
    ```sh
    if [[ ! -d "$START_DIR/files" ]]; then
        mkdir "$START_DIR/files"
        chown -R $APP_USER_ID "$START_DIR/files"
        chgrp -R $APP_USER_ID "$START_DIR/files"
    fi
    ```

#### 推荐用法示例
- 在KrScript(PIO)还没有加入`resource`节点的早期版本中，
- 我们经常需要通过这样的方式来执行一个单独的脚本文件

    ```xml
    <group>
        <action>
            <title>环境变量</title>
            <set>file:///android_asset/samples/test/var.sh</set>
        </action>
        <action>
            <title>用户ID检测</title>
            <set>file:///android_asset/samples/test/check_root.sh</set>
        </action>
    </group>
    ```
    > 其工作原理是：PIO框架解析xml时，发现脚本内容以`file:///android_asset`开头，<br />
    > 会将其当做一个路径，预先从安装包`assets`中提取该文件。<br />
    > 最终被执行的是被提取后的文件。<br />
    > 可见，这样的写法非常啰嗦，你需要不停重复 `file:///android_asset/`

- ``现在建议的做法是``，通过`resource`节点预先提取相关资源文件，再按正常的shell语法执行脚本文件
- 例如：

    ```xml
    <group>
        <resource dir="file:///android_asset/samples/test" />

        <action>
            <title>环境变量</title>
            <set>sh samples/test/var.sh</set>
        </action>
        <action>
            <title>用户ID检测</title>
            <set>sh samples/test/check_root.sh</set>
        </action>
    </group>
    ```

#

---

> 相关说明

- `$START_DIR` 和 `$APP_USER_ID` 是PIO自行添加的全局变量，
- 可以参考 [Script](#/doc?doc=/docs/Script.md) 中 **参数变量** 部分

