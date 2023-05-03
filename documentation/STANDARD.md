# 代码标准及规范

- 所有信息发送必须通过 [Message](../common/src/main/java/team/floracore/common/locale/Message.java) 类，非必要时需包含
  prefix。
- 信息主体颜色使用 AQUA，子参数使用 GREEN，非必要时不使用其它颜色。
- 信息内容必须加入[翻译文件](../common/src/main/resources/floracore_zh_CN.properties)中。
- 所有可 TabComplete 的命令必须加上 suggestion。
- 命令注册格式必须统一避免命令框架无法识别。(见附录)
- 权限信息以顺序（首字母）加入 [plugin.yml](../plugin/loader/src/main/resources/plugin.yml)。
- 命令注册格式和事件监听必须一致。
- 未测试和异常的命令需在 [CommandManager](../common/src/main/java/team/floracore/common/command/CommandManager.java)
  标记以便后续处理。
- 开发者应了解框架，不熟悉代码不得修改。
- 开发者需在新分支(或已存在的分支)上编写代码，避免影响主分支。
- 版本发布由指定人员([@冬花ice](https://github.com/flowerinsnowdh)和[@花花](https://github.com/xLikeWATCHDOG/))完成。

# 附录

## 无法识别的命令格式

- 下面是一个无法被识别的命令注册形式，注意避免。

```
    @CommandMethod("air setmax <value>")
    // TODO 其它注解
    public void setOwnMax(@NotNull Player s, @Argument("value") int value) {
        //TODO 代码实现
    }

    @CommandMethod("air setmax <target> <value>")
    // TODO 其它注解
    public void setOtherMax(@NotNull CommandSender s, @NotNull @Argument("target") Player target, @Argument("value") int value, @Nullable @Flag("silent") Boolean silent) {
        // TODO 代码实现
    }
```