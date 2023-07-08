# TimeOperate-Expansion
Time operating expansion for PlaceholderAPI

# Install

* Download jar file from [releases](https://github.com/MrXiaoM/TimeOperate-Expansion/releases).
* Put the jar file to `/plugins/PlaceholderAPI/expansions/`
* Execute command `/papi register <filename>` or `/papi reload` or restart server.

# Usage

```
// Arguments: <Required> [Optional]

%timeoperate_<time>_<format>[_<time override>]%
```
Type `{placeholder}` or `(placeholder)` if you want to use some placeholders in arguments.

> I am not a native English speaker. There are maybe some typo error.

## Time
| value          | detail | example                |
|----------------| --- |------------------------|
| now           | now time | `now`                  |
| timestamp     | timestamp (in seconds) | `1675353600`           |
| DateTimeFormatter&#124;value | specific format time | yyyy-MM-dd&#124;2023-02-03 |

## Format

Use `unix` for returning timestamp (in seconds).

Or read the [DateTimeFormatter](http://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html) Javadoc page about possible formats.

## Time Override
| value | detail | example |
| --- | --- | --- |
| +DURATION | plus time, needed number and unit (ignore case), `d` means `day(s)`, `h` means `hour(s)`, `m` means `minute(s)`, `s` means `second(s)` | `+1d2h` |
| -DURATION | minus time, needed number and unit (ignore case), `d` means `day(s)`, `h` means `hour(s)`, `m` means `minute(s)`, `s` means `second(s)` | `-1d2h` |
| unit=value | set specific time | `y=2023` |

Support multi overrides, connect them with `_`.

### Specific Time Unit
| value | detail |
| --- | --- |
| `y`, `year` | Year |
| `M`, `month` | Month |
| `d`, `day` | Day |
| `h`, `hour` | Hour |
| `m`, `minute` | Minute |
| `s`, `second` | Second |

# Examples

```
// Get timestamp (in second) of tomorrow 6:00
%timeoperate_now_unix_+1d_h=0_m=0_s=0%
// Get time of tomorrow but minute and second are 0 with custom format
%timeoperate_now_yyyy/MM/dd HH:mm:ss_+1d_m=0_s=0%
```