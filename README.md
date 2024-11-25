# TimeOperate-Expansion
Time operating expansion for PlaceholderAPI

# Install

* Download jar file from [releases](https://github.com/MrXiaoM/TimeOperate-Expansion/releases).
* Put the jar file to `/plugins/PlaceholderAPI/expansions/`
* Execute command `/papi register <filename>` or `/papi reload` or restart server.

# Usage

```
// Arguments: <Required> [Optional]

%timeoperate_<time>_<display format>[_<time override>]%
```
Type `{placeholder}` or `(placeholder)` if you want to use some placeholders in arguments.

> I am not a native English speaker. There are maybe some typo errors.

## time
| value                                                                                                    | detail                 | example                    |
|----------------------------------------------------------------------------------------------------------|------------------------|----------------------------|
| now                                                                                                      | now time               | `now`                      |
| timestamp                                                                                                | timestamp (in seconds) | `1675353600`               |
| [Formatter](http://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)&#124;value | specific format time   | yyyy-MM-dd&#124;2023-02-03 |

## display format

Use `unix` for returning timestamp (in seconds).

Or read the [DateTimeFormatter](http://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html) Javadoc page about possible formats.  
e.g. `yyyy-MM-dd` (2024-08-10), `HH:mm:ss` (11:45:14)

## time override
| value              | detail                                                                                                                                                                                                                                                                                                    | example                                                                 |
|--------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------|
| +DURATION          | plus time, needed number and unit (ignore case), <ul><li>`d` means **day(s)**</li><li>`h` means **hour(s)**</li><li>`m` means **minute(s)**</li><li>`s` means **second(s)**</li></ul>                                                                                                                     | `+1d2h`                                                                 |
| -DURATION          | minus time, needed number and unit (ignore case), <ul><li>`d` means **day(s)**</li><li>`h` means **hour(s)**</li><li>`m` means **minute(s)**</li><li>`s` means **second(s)**</li></ul>                                                                                                                    | `-1d2h`                                                                 |
| unit=value         | set specific time, needed unit and number (case sensitivity), <ul><li>`y` or `year` means **year**</li><li>`M` or `month` means **month**</li><li>`d` or `day` means **date**</li><li>`h` or `hour` means **hour**</li><li>`m` or `minute` means **minute**</li><li>`s` or `second` means **second**</ul> | `y=2023`                                                                |
| hour:minute:second | set hour, minute and second in a more simple way, type `~` means not modify                                                                                                                                                                                                                               | `4:0:0`, `6:0` (full `06:00:00`), `20:05` (full `20:05:00`), `11:45:14` |
| w+num              | set date to next *num* weeks' Monday. If *num* equals 0, it just set date to the Monday of that week.                                                                                                                                                                                                     | `w+1` (`2024-11-15 Friday` -> `2024-11-18 Monday`)                      |
| w-num              | set date to previous *num* weeks' Monday. The same as `w+num`                                                                                                                                                                                                                                             | `w-1` (`2024-11-15 Friday` -> `2024-11-04 Monday`)                      |

Support multi overrides, connect them with `_`.

# Examples

```
// Get timestamp (in second) of tomorrow 6:00
%timeoperate_now_unix_+1d_6:00%

// Get time of tomorrow but minute and second are 0 with custom format
%timeoperate_now_yyyy/MM/dd HH:mm:ss_+1d_~:0:0%
```

# Usage Examples

```
// give player temp permission by LuckPerms, expire in tomorrow 4:00
/lp user %player_name% permission settemp your.perm.here true %timeoperate_now_unix_+1d_4:00%
```
