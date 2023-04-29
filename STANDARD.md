# 代码标准及规范

- 所有信息发送必须通过 [Message](./common/src/main/java/team/floracore/common/locale/Message.java) 类，非必要时需包含
  prefix。
- 信息主体颜色使用 AQUA，子参数使用 GREEN，非必要时不使用其它颜色。
- 所有可 TabComplete 的命令必须加上 suggestion。
- 命令注册格式必须统一避免命令框架无法识别。
- 权限信息以顺序（首字母）加入 [plugin.yml](./plugin/loader/src/main/resources/plugin.yml)。
- 命令注册格式和事件监听必须一致。
- 未测试的命令需在 [CommandManager](./common/src/main/java/team/floracore/common/command/CommandManager.java) 标记以便后续处理。
- 开发者应了解框架，不熟悉代码不得修改。
- 开发者需在新分支上编写代码，避免影响主分支。
- 版本发布由指定人员完成。
