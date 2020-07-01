
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
- 就像执行`visible` `desc-sh` `option-sh` 部分的脚本一样
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
