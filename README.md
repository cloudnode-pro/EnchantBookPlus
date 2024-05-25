# EnchantBookPlus

[![CodeQL](https://github.com/cloudnode-pro/EnchantBookPlus/actions/workflows/codeql.yml/badge.svg)](https://github.com/cloudnode-pro/EnchantBookPlus/actions/workflows/codeql.yml)
[![Modrinth](https://img.shields.io/badge/Modrinth-%2326292f?logo=modrinth)](https://modrinth.com/plugin/dMOPYb3s/)
[![Version](https://img.shields.io/modrinth/v/dMOPYb3s)](https://modrinth.com/plugin/dMOPYb3s/)
[![Game Versions](https://img.shields.io/modrinth/game-versions/dMOPYb3s)](https://modrinth.com/plugin/dMOPYb3s/)
[![Downloads](https://img.shields.io/modrinth/dt/dMOPYb3s)](https://modrinth.com/plugin/dMOPYb3s/)

Combine enchantment books to achieve levels above vanilla

## How it works
This plugin allows you to combine enchantments, e.g. Efficiency V + Efficiency V = Efficiency VI. This is done by using an anvil.

You can configure which enchantments can be combined above the Vanilla Minecraft levels. You can set the max level for each enchantment, and add XP cost in levels.

## Permissions

> [!NOTE]
> `<echantment>` is the enchantment ID, e.g. `swift_sneak`, `efficiency`, etc. Replace with `*` for all enchantments.
>
> Example: `enchantbookplus.enchant.*` or `enchantbookplus.enchant.unbreaking`

| Permission                              | Description                                                                            |
|-----------------------------------------|----------------------------------------------------------------------------------------|
| `enchantbookplus.enchant.<enchantment>` | Allow enchanting `<enchantment>` above the vanilla level, as configured in the plugin. |
| `enchantbookplus.reload`                | Reload plugin config using `/enchantbookplus reload`                                   |

## Reporting issues
Fixing bugs is the utmost priority for this project. If you find any issue, check the [GitHub Issue Tracker](https://github.com/cloudnode-pro/EnchantBookPlus/issues) to see if it has already been reported. If not, create a new issue.

When creating a new issue, include as many relevant details as possible, e.g.:

 - Minecraft server type and version (e.g. Paper 1.20.1)
 - Plugin version (e.g. 1.0.0)
 - Warnings and errors related to this plugin in the console
 - Steps to reproduce the issue

### Contributing
Anyone is welcome to contribute to this project.

To contribute:

1. [Fork the repository](https://github.com/cloudnode-pro/EnchantBookPlus/fork)
2. Make your changes in your forked repository
3. Create a pull request

Pull requests require approval and changes may be requested before merging to the main branch.

Contributions of any kind are most welcome and are given full credit.

### License
This is libre software, meaning that it is forever free and open source, licensed under the GNU General Public License version 3. You can read the [full license](https://github.com/cloudnode-pro/EnchantBookPlus/blob/main/LICENSE). 
