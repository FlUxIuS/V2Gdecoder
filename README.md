# V2Gdecoder

V2Gdecoder is a tool aimed to encode and decode V2G messages that commonly use EXI to compress exchanged XML files between a car Power-Line Communication module and a charging station.

This tool is based on [RISE V2G shared library](https://github.com/V2GClarity/RISE-V2G) to easily parse messages.

## How to use

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

### Using the web service

The tool can be runned as follows:
```
$ java -jar V2Gdecoder_test.jar
```
You can then use your favorite tool/interpreter to send EXI data to get the reply as follows:
```
In [1]: import requests
In [2]: r = requests.post("http://localhost:9000", headers={"Format":"EXI"}, data="809802107f860d7bae65d
    ...: d8a891a1d1d1c0e8bcbddddddcb9dcccb9bdc99cbd5148bd8d85b9bdb9a58d85b0b595e1a50d5a1d1d1c0e8bcbdddddd
    ...: cb9dcccb9bdc99cbcc8c0c0c4bcc0d0bde1b5b191cda59cb5b5bdc9948d958d91cd84b5cda184c8d4d9002b4b2189062
    ...: 3696431024687474703a2f2f7777772e77332e6f72672f54522f63616e6f6e6963616c2d6578694852d0e8e8e0745e5e
    ...: eeeeee5cee665cdee4ce5e646060625e60685ef0dad8cadcc646e6d0c2646a6c84165aa773adf12a841e302f171698e9
    ...: c4d1e6bb2afdac13826f13ba6a09532c82a2841400000000000501c030a0161005696431001000100240880e20108120
    ...: 3840110260a88032940000081030c08018503f03102400c0c3010031039804461800080")
In [3]: r.text
Out[3]: u'<?xml version="1.0" encoding="UTF-8"?><ns7:V2G_Message xmlns:ns7="urn:iso:15118:2:2013:MsgDef" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:ns3="http://www.w3.org/2001/XMLSchema" xmlns:ns4="http://www.w3.org/2000/09/xmldsig#" xmlns:ns5="urn:iso:15118:2:2013:MsgBody" xmlns:ns6="urn:iso:15118:2:2013:MsgDataTypes" xmlns:ns8="urn:iso:15118:2:2013:MsgHeader"><ns7:Header><ns8:SessionID>41FE1835EEB99776</ns8:SessionID><ns4:Signature><ns4:SignedInfo><ns4:CanonicalizationMethod Algorithm="http://www.w3.org/TR/canonical-exi"/><ns4:SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"/><ns4:Reference Id="id1" URI="#id1"><ns4:Transforms><ns4:Transform Algorithm="http://www.w3.org/TR/canonical-exi"/></ns4:Transforms><ns4:DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256"/><ns4:DigestValue>stU7nW+JVCDxgXi4tMdOJo812VftYJwTeJ3TUEqZZBU=</ns4:DigestValue></ns4:Reference></ns4:SignedInfo><ns4:SignatureValue/></ns4:Signature></ns7:Header><ns7:Body><ns5:ChargeParameterDiscoveryRes><ns5:ResponseCode>OK</ns5:ResponseCode><ns5:EVSEProcessing>Finished</ns5:EVSEProcessing><ns6:SAScheduleList><ns6:SAScheduleTuple><ns6:SAScheduleTupleID>1</ns6:SAScheduleTupleID><ns6:PMaxSchedule><ns6:PMaxScheduleEntry><ns6:RelativeTimeInterval><ns6:start>0</ns6:start><ns6:duration>7200</ns6:duration></ns6:RelativeTimeInterval><ns6:PMax><ns6:Multiplier>3</ns6:Multiplier><ns6:Unit>W</ns6:Unit><ns6:Value>11</ns6:Value></ns6:PMax></ns6:PMaxScheduleEntry></ns6:PMaxSchedule><ns6:SalesTariff ns6:Id="id1"><ns6:SalesTariffID>1</ns6:SalesTariffID><ns6:SalesTariffEntry><ns6:RelativeTimeInterval><ns6:start>0</ns6:start></ns6:RelativeTimeInterval><ns6:EPriceLevel>1</ns6:EPriceLevel></ns6:SalesTariffEntry><ns6:SalesTariffEntry><ns6:RelativeTimeInterval><ns6:start>1800</ns6:start></ns6:RelativeTimeInterval><ns6:EPriceLevel>4</ns6:EPriceLevel></ns6:SalesTariffEntry><ns6:SalesTariffEntry><ns6:RelativeTimeInterval><ns6:start>3600</ns6:start></ns6:RelativeTimeInterval><ns6:EPriceLevel>2</ns6:EPriceLevel></ns6:SalesTariffEntry><ns6:SalesTariffEntry><ns6:RelativeTimeInterval><ns6:start>5400</ns6:start></ns6:RelativeTimeInterval><ns6:EPriceLevel>3</ns6:EPriceLevel></ns6:SalesTariffEntry></ns6:SalesTariff></ns6:SAScheduleTuple></ns6:SAScheduleList><ns6:DC_EVSEChargeParameter><ns6:DC_EVSEStatus><ns6:NotificationMaxDelay>0</ns6:NotificationMaxDelay><ns6:EVSENotification>None</ns6:EVSENotification><ns6:EVSEIsolationStatus>Valid</ns6:EVSEIsolationStatus><ns6:EVSEStatusCode>EVSE_Ready</ns6:EVSEStatusCode></ns6:DC_EVSEStatus><ns6:EVSEMaximumCurrentLimit><ns6:Multiplier>0</ns6:Multiplier><ns6:Unit>A</ns6:Unit><ns6:Value>32</ns6:Value></ns6:EVSEMaximumCurrentLimit><ns6:EVSEMaximumPowerLimit><ns6:Multiplier>3</ns6:Multiplier><ns6:Unit>W</ns6:Unit><ns6:Value>63</ns6:Value></ns6:EVSEMaximumPowerLimit><ns6:EVSEMaximumVoltageLimit><ns6:Multiplier>0</ns6:Multiplier><ns6:Unit>V</ns6:Unit><ns6:Value>400</ns6:Value></ns6:EVSEMaximumVoltageLimit><ns6:EVSEMinimumCurrentLimit><ns6:Multiplier>0</ns6:Multiplier><ns6:Unit>A</ns6:Unit><ns6:Value>16</ns6:Value></ns6:EVSEMinimumCurrentLimit><ns6:EVSEMinimumVoltageLimit><ns6:Multiplier>0</ns6:Multiplier><ns6:Unit>V</ns6:Unit><ns6:Value>230</ns6:Value></ns6:EVSEMinimumVoltageLimit><ns6:EVSEPeakCurrentRipple><ns6:Multiplier>0</ns6:Multiplier><ns6:Unit>A</ns6:Unit><ns6:Value>0</ns6:Value></ns6:EVSEPeakCurrentRipple></ns6:DC_EVSEChargeParameter></ns5:ChargeParameterDiscoveryRes></ns7:Body></ns7:V2G_Message>'
```
