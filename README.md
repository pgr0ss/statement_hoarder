# Statement Hoarder

Statement Hoarder automates downloading PDF statements from websites. It saves you the effort of downloading them one at a time.

## Supported Sites

* American Express
* AT&T
* Blue Cross Blue Shield of Illinois
* ComED
* RCN

## Usage

Copy `config.yml.example` to `config.yml`. Edit this file and set the `statement-path` to where you want statements to go. For each site that you want to download, set the `username`. Remove sites that you do not use.

Now run:

```bash
lein deps
lein trampoline run config.yml
```

This will prompt for all passwords and then spin up a browser to download all statements.

## License

Copyright (C) 2012 Paul Gross

Distributed under the [MIT license](http://www.opensource.org/licenses/MIT).
