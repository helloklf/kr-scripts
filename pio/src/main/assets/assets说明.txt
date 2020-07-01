## 以下文件为框架依赖的必要文件
- kr-script.conf            基础配置信息       （固定配置文件路径不可更改）
- kr-script/executor.sh     脚本执行器入口     （由 kr-script.conf 指定）
- kr-script/favorites.xml   收藏夹页面功能配置 （由 kr-script.conf 指定）
- kr-script/more.xml        "全部"界面列表配置 （由 kr-script.conf 指定）
- kr-script/toolkit         工具包目录，放置 [busybox、zip] 之类的linux程序（由 kr-script.conf 指定）

未在上述列表中提及，且不在[kr-script]目录之内的文件，
则并非框架运行所必需的文件，仅作为示例代码展示
