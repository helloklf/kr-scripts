## 路径
- PIO中经常需要访问`磁盘文件`和安装包`Assets`中的资源
- 例如：Action、Switch、Picker、Page 的`logo` `icon`，Page的 `config`，Resource的 `file` `dir`

### Resource 的路径
- `resource` 节点的作用非常纯粹，就是提取 `assets` 中的文件
- 所以，无论完整的写 `file:///android_asset/dir/sample.zip`，还是简略的写 `dir/sample.zip`
- 都会直接去往 `assets` 查找文件

#### Assets 路径
- 当你定义的路径是以`file:///android_asset/`开头时，
- PIO一定会去assets里找该文件，因此它绝对可靠

  ```xml
  <action
    icon="file:///android_asset/samples/icons/icon-test.png">
    <title>Assets路径的图标</title>
  </action>
  ```

### 不可确定的路径
- 但如果你企图把上面距离的 `assets` 路径简写成下面的样子，就可能会遇到问题

  ```xml
  <action
    icon="amples/icons/icon-test.png">
    <title>Assets路径的图标</title>
  </action>
  ```

#### 路径匹配
> 在非 `resource` 节点里，PIO遇到非 `file:///android_asset` 开头的路径时<br />
> PIO会优先在磁盘上寻找此路径的文件，查找磁盘文件时又有分两种处理方式<br />
> 以 `/` 开头的认为是**绝对路径**，例如：`/system/build.prop`<br />
> 非 `/` 开头的认为是**相对路径**，例如：`./build.prop` 和 `build.prop`

- 查找绝对路径的文件时逻辑很简单，就像这样：
  ```sh
  # 示例代码，仅供展示流程

  if [[ -e $target_path ]]; then
    return true
  else
    return false
  fi
  ```

> 而查找相对路径的逻辑，就比较复杂了<br />
- 如果当前页面配置文件`XML`来源于`assets`，去`assets`找
  ```sh
  # 示例代码，仅供展示流程

  if [[ -f $PAGE_CONFIG_DIR/$target_path ]]; then
    return true
  else if [[ -f "file:///android_asset/$target_path" ]]; then
    return true
  else
    return false
  fi
  ```

- 否则（当前页面配置文件`XML`来源于磁盘文件），则去磁盘上找
  ```sh
  # 示例代码，仅供展示流程

  if [[ -f $PAGE_WORK_DIR/$target_path ]]; then
    return true
  else if [[ -f $START_DIR/$target_path ]]; then
    return true
  else
    return false
  fi
  ```

#### 建议
- PIO解析和处理相对路径的过程相当冗长，如果你希望它工作更加稳定
- 引用资源文件路径时，建议尽可能用`绝对路径`，即：
  > 引用`assets`时，路径以 `file:///android_asset/` 开头 <br />
  > 引用磁盘上的文件时，路径应从系统根目录开始（以 `/` 开头）

## 相关说明
- `PAGE_WORK_DIR`、`PAGE_CONFIG_DIR`、`START_DIR` 的详细定义，
- 可以参考 [Script](#/doc?doc=/docs/Script.md) 中 **参数变量** 部分