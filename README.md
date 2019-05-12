V2Gdecoder
==========

V2Gdecoder is a tool aimed to encode and decode V2G messages that commonly use EXI to compress exchanged XML files between a car Power-Line Communication module and a charging station.

This tool is based on [RISE V2G shared library](https://github.com/V2GClarity/RISE-V2G) to easily parse messages.

How to use
----------

V2Gdecoder supports many methods to encode XML/decode EXI data as follows:

```
$ java -jar V2Gdecoder.jar -h                                                           1 ↵
Unrecognized option: -h
usage: V2GEXI Helper
 -e,--exi            EXI format
 -f,--file <arg>     input file path
 -o,--output         output file in a dedicated path
 -s,--string <arg>   string to decode
 -w,--web            Webserver
 -x,--xml            XML format
```

