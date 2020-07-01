## 图标
- PIO 的功能节点都可以设置图标
- 例如：Action、Switch、Picker、Page 都可以设置在左侧显示`icon`，也可以设置添加到桌面的`logo`
- 那么，这些图标的路径都要怎么配置？

### Assets 路径
- 当你定义的图标路径是`file:///android_asset/`时，
- PIO一定会去assets里找该文件

  ```xml
  <action
    icon="file:///android_asset/samples/icons/icon-test.png">
    <title>Assets路径的图标</title>
  </action>
  ```

### 路径匹配
- 那么，如果我们写的是一个模糊不清的路径呢？
- 例如，把 `file:///android_asset/` 前缀去掉，PIO会如何解析这个路径？

  ```xml
  <action
    icon="samples/icons/icon-test.png">
    <title>Assets路径的图标</title>
  </action>
  ```

- 首先，PIO会把它当做一个绝对路径去访问，它的逻辑就像：
  ```sh
  if [[ -e $icon_path ]]; then
    return 1
    # 如果到这里就找到了，那就不走下面的流程了
  fi
  ```

- 判断路径格式，如果不以 `/` 开头，作为相对路径再解析
  > 相当路径是指相对于当前XML配置文件的提取路径<br />
  > 也就是说，只有你的页面是通过路径指定的XML配置文件才有相对路径可言<br />
  > 如果是通过shell直接输出的页面内容，那就没有相对路径可用了<br />
  > 它的逻辑就像：
  ```sh
  if [[ -e $xml_extract_path/$icon_path ]]; then
    return 1
    # 如果到这里就找到了，那就不走下面的流程了
  fi
  ```
- 到这里，如果还没找到文件，那么PIO会认为可能是权限限制导致无法访问文件
  > 接下来，PIO会以ROOT权限，用`shell`再去重复上两步的逻辑

- 最后，如果用ROOT权限依然没有找到文件，再回来PIO安装包`assets`里找这个文件
  > 众里寻他千百度，蓦然回首……

### 总结
- 可见，PIO在寻找一个不确定意义的路径的图标时，会优先查找磁盘
- 这是因为PIO认为你可能通过在线下载图标文件到磁盘，
- 为了避免磁盘上和安装包里同时存在此图标时，被使用的却是安装包里的旧版资源
