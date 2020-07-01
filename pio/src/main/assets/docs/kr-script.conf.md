## 应用启动过程

> 在 检测完ROOT权限之后，显示功能列表之前<br />
> PIO会先读取assets中的 `kr-script.conf`<br />
> 在解析 kr-script.conf 的过程中，会完成：<br />
> `executor_core` 的转换处理，和 `toolkit_dir` 目录的提取<br />
> 接下来会执行 `before_start_sh` （如果有配置）<br />
> 再  执行 `favorite_config_sh` 和 `page_list_config_sh` （如果有配置）<br />
> 或  读取 `favorite_config` 和 `page_list_config` 

### kr-script.conf配置格式
- kr-script.conf 以 `key="value"` 的格式配置一些基本信息
- 例如：

  ```sh
  # 核心执行器 core_executor
  # 这是kr-script执行脚本的入口
  executor_core="file:///android_asset/kr-script/executor.sh"

  # 工具包目录（放置busybox等工具）
  toolkit_dir="file:///android_asset/kr-script/toolkit"

  # 页面列表配置文件（可配置为空）
  page_list_config="file:///android_asset/kr-script/more.xml"

  # 收藏的功能配置（可配置为空）
  favorite_config="file:///android_asset/kr-script/favorites.xml"

  # 是否显示首页性能监视器（如果不配置，默认显示）
  allow_home_page="0"
  ```

### 可配置属性

| 属性 | 说明 | 配置内容 |
| - | - | - |
| executor_core | 执行器入口在assets中的位置 | `file:///android_asset/`开头的路径 |
| toolkit_dir | 应用自带工具集在assets中的位置 | `file:///android_asset/`开头的路径 |
| page_list_config | **全部** 页对应的配置文件 | `file:///android_asset/`开头的路径 |
| favorite_config | **收藏夹** 页对应的配置文件 | `file:///android_asset/`开头的路径 |
| allow_home_page | 是否显示应用**首页** | `1` 或 `0` |
| before_start_sh | 进入功能列表前执行的脚本 | `file:///android_asset/`开头的路径 |
| page_list_config_sh | 输出 **全部** 页配置路径的脚本 | `file:///android_asset/`开头的路径 |
| favorite_config_sh | 输出 **收藏夹** 页配置路径的脚本 | `file:///android_asset/`开头的路径 |

#### before_start_sh
- 在解析完`kr-script.conf`之后，会立即执行`before_start_sh` 配置的脚本
- 执行过程中输出的内容和错误信息，会显示在启动屏上
- 你可以利用此脚本，完成在启动前的强制更新

#### page_list_config_sh、favorite_config_sh
- 这两个属于分别用于分别指定 **收藏夹** 和 **全部** 两个页面的配置（路径或具体内容）
- 就像 `page`节点的 `config-sh` 属性一样

## 相关参考
- page_list_config_sh、favorite_config_sh 需要怎么用
- 建议参考 [`Page`](#/doc?doc=/docs/Page.md) 中`config-sh`的说明，
