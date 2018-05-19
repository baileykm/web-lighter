# Web-lighter
___Web-lighter___ 是一个小型的 _Java Web_ 服务器端封装.

## Web-lighter 能做什么? 
- 分发 _HTTP Request_ : 接收 _Http_ 请求并分发给用户自定义的 _Action-Method_ 进行处理, 并将处理结果发回前端
- _HTTP Request_ 参数的自动解析与注入( 支持 _text / json_ )
- _Action_ 自动实例化与调用 ( _Action_ 为用户自定义逻辑的封装 )
- 基于 _Java Annotation_ ( Java注解 ) 的注入配置
- 多文件上传支持


## 使用方法概要
1. 将 web-lighter_xxx.jar 及依赖资源添加至项目构建路径
2. 定义 `Action` 类, 以封装你的业务逻辑. ( _Action_ 类须继承 _com.pr.web.lighter.action.ActionSupport_ )
3. 在 _Action_ 类中添加必要的方法 ( method )  
    3.1 在方法上添加 [`@Request`](#Request) 注解, 以标注该方法可以响应的特定的 HTTP 请求  
    3.2 若需要上传文件, 可同时在方法上添加 [`@Upload`](#Upload) 注解  
    3.3 为方法形参表中的参数添加 [`@Param`](#Param) 注解 或  [`@Inject`](#Inject) 注解, 以说明参数值来源  
    3.4 在方法体中书写你的业务处理代码, 并最终返回 1 个 [`ActionResult`](#ActionResult) 对象  
> 参见["使用示例"](#simple-example)


## 依赖
web-lighter 1.0.0 依赖于如下第三方资源 

___marvon pom.xml___
```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>3.0.1</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.7</version>
</dependency>
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.4</version>
</dependency>
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.3.3</version>
</dependency>
```
如果你使用 _marvon_, 可将上述代码复制到 _pom.xml_ 文件中的 ```<dependencies>...</dependencies>```节

或者也可以直接下载上述第3方资源, 复制到 Web 项目的 _WEB-INF/lib_ 下, 并将其添加至项目构建路径.


## <a id="simple-example">使用示例</a>
### 纯数据 _Request_
___-- Java Code --___
```java
public class ActionExample extends ActionSupport {
    @Request(uri = "/doSomeThing")                         // 标注此方法可以接收的 url
    public ActionResult doSomeThing(
        @Inject ServiceExample service,                    // Service - Action中需要使用到的Servie对象, 自动实例化并注入
        @Param(name = "intParam") Integer id,              // 上行参数, Integer
        @Param(name = "strParam") String str,              // 上行参数, String
        @Param(name = "voParam") VO vo,                    // 上行参数, VO (值对象, 用于封装数据的值对象, 可以是标准的 JavaBean)
        @Param(name = "voArrParam") List<VO> voArr         // 上行参数, List<VO>
    ){
            
        try {
            // ...
            return ActionResult.success(vo);                    // 成功, 直接将前端传来的voParam回传
        } catch (Exception ex) {
            return ActionResult.failure("Some thing wrong");    // 操作失败
        }
     }
}
```

___-- HTTP Request --___

- POST http://localhost:8080/wl/doSomeThing
- Content-Type: application/json
```json
{
    "intParam"  : 1,
    "str"       : "Some words...",
    "voParam"   : {
        "id"    : 999,
        "name"  : "Peter"
    },
    "voArrParam": [{
        "id"    : 10,
        "name"  : "John"
    },{
        "id"    : 20,
        "name"  : "Joanna"
    }]
}
```
__或__

- POST http://localhost:8080/wl/doSomeThing
- Content-Type: application/x-www-form-urlencoded
```text
intParam=1&str=Some words...&voParam={"id" : 999, "name" : "Peter" }&voArrParam=[{ "id": 10, "name": "John"},{"id": 20, "name": "Joanna"}]}
```
> __注意__: _Request URL_ 中的路径前缀 __"/wl"__

___-- HTTP Response --___

成功:
```json
{"code":0, "result":{"id":999,"name":"Peter"}}
```
失败:
```json
{"code":-1, "message":"Some thing wrong"}
```


### 带文件上传的 _Request_
___-- Java Code --___
```java
public class ActionExampleWithFile extends ActionSupport {
    @Request(uri = "/doSomeThingWithFile")                 // 标注此方法可以接收的 url
    @Upload                                                // 标注此方法可支持文件上传
    public ActionResult doSomeThingWithFile(
        @Inject ServiceExample service,                    // Service - Action中需要使用到的Servie对象, 自动实例化并注入
        @Param(name = "intParam") Integer id,              // 上行参数, Integer
        @Param(name = "strParam") String str,              // 上行参数, String
        @Param(name = "cbValue") String[] cbValue,         // 上行参数, 复选框的值
        @Param(name = "voParam") VO vo,                    // 上行参数, VO (值对象, 用于封装数据的值对象, 可以是标准的 JavaBean)
        @ParamFileInfo List<FileInfo> fileInfos,           // 多文件上传时注入所有成功上传的文件信息
        @ParamFileInfo FileInfo firstFile                  // 成功上传的第1个文件的信息
    ){
            
        try {
            // ...
            return ActionResult.success(vo);                    // 成功, 直接将前端传来的voParam回传
        } catch (Exception ex) {
            return ActionResult.failure("Some thing wrong");    // 操作失败
        }
     }
}
```

___-- 文件上传表单 --___
```html
<form method="post" action="/wl/doSomeThingWithFile" enctype="multipart/form-data">
    intParam: <input type="text" name="intParam" value="1" /><br/>
    strParam: <input type="text" name="strParam" value="Some words..." /><br/>
    checkbox: <input type="checkbox" name="cbValue" value="cbValue1" />cbValue1
              <input type="checkbox" name="cbValue" value="cbValue2" />cbValue2
              <br/>
    vo: <input type="text" name="voParam" value='{"id" : 999, "name" : "Peter" }' /><br/>
    uploadFile1: <input type="file" name="uploadFile1" /><br/>
    uploadFile2: <input type="file" name="uploadFile2" /><br/>
    <input type="submit" value="SUBMIT" />
</form>
```
> __注意__: form 的 enctype 属性应为 __"multipart/form-data"__

___-- HTTP Response --___

成功:
```json
{"code":0, "result":{"id":999,"name":"Peter"}}
```
失败:
```json
{"code":-1, "message":"Some thing wrong"}
```

## Web-lighter 配置与使用详述
### **web-lighter.xml**  _<small>( 可选, 并非必需 )</small>_  
此文件为 _Web-lighter_ 的主配置文件, 可自定义关于 _Web-lighter_ 的一些通用配置.  
web-lighter.xml 中可配置的信息包括:  

参数 | 默认值 | 取值 | 说明
----- | -------|------|------
urlPrefix | `/wl` | String     | **url 前缀**<br/> web-lighter 按路径匹配方式拦截需要处理的请求, 即默认状态下, web-lighter 将拦截所有 url 以 "/wl" 开头的 HTTP 请求.<br/> 因此, 编写前端代码时应注意为 url 加上前缀, 例如: http://localhost:8080/wl/doSomething
dateFormat | `yyyy-MM-dd hh:mm:ss` |    | 日期型数据序列化/反序列化格式.

> 你可以直接创建一个 XML 文件或从 web-lighter_xxx.jar 中复制一份放到 _src_ 根目录即可. web-lighter.xml 文件格式如下:
```xml
<?xml version="1.0" encoding="utf-8"?>
<configuration>
    <urlPrefix>/wl</urlPrefix>
    <dateFormat>yyyy-MM-dd hh:mm:ss</dateFormat>
</configuration>
```

> web-lighter.xml 配置文件并非必需, 也就是说, 若上述默认配置已满足你的需求, 则可省去 web-lighter.xml .
  
### <a id="Request">**@Request**</a>
___@Request___ 注解应用于 _Action_ 类中的 HTTP Request 处理方法上, 以标注该方法用于接收并处理 HTTP 请求  
> _Action_ 为用户自定义 HTTP Request 处理逻辑的封装, 应继承 _com.pr.web.lighter.action.ActionSupport_   

参数 | 默认值 | 取值 | 说明
----- | -------|------|------
url |  | String     | 可接收并处理的请求 url<br/>支持通配符和参数, 如: /{param1}/*.action/{param2}. "*" 代表匹配任意个任意字符, {param1} 表示此为参数占位, 其中的 param1为参数名<br/>** 注意 **<br/>- web-lighter 使用路径匹配方式拦截前端请求, 默认情况下, 若请求的 url 匹配模式 "/wl/*" 时将被 web-lighter 拦截并处理. 若需要更改拦截匹配模式, 请在 web-lighter.xml 配置文件中进行设置.<br/>- 前端访问路径记得添加路径前缀 ( 默认为"/wl" ), 如: http://localhost:8080<strong style="color:red">/wl</strong>/doSomething.action<br/>- 本注解的 url 参数无需添加路径前缀, 如: /doSomething, 运行时 web-lighter 将会匹配 /wl/doSomething<br/>- 虽然 url 中支持类似 RESTful Web 风格的参数, 但 web-lighter 暂未完全支持 RESTful Web 的标准方法
format | `ParamFormat.json` |  `ParamFormat.json`<br/>`ParamFormat.text`  | HTTP 请求中参数的格式, 默认为 JSON 格式<br/>Content-Type = "application/json" 时此参数无效 ( 始终被理解为JSON 格式数据)

### <a id="Upload">**@Upload**</a>
___@Upload___ 注解应用于 _Action_ 类中的 HTTP Request 处理方法上, 以标注该方法可支持文件上传 ( 单个 / 多个文件 )  

参数 | 默认值 | 取值 | 说明
----- | -------|------|------
uploadDir | upload | String     | 文件上传后的保存路径. 默认为网站根目录下的 upload 子目录
maxFileSize | `1024 * 1024 * 40` |  `int`  | 单个文件的最大字节数. 默认40M
maxRequestSize | `1024 * 1024 * 50` |  `int`  | 请求的最大字节数. 默认50M
> 注意: HTML 中文件上传 form 的 enctype 属性应为 __"multipart/form-data"__

### <a id="Param">**@Param**</a>
___@Param___ 注解应用于 _Action_ 类中的 HTTP Request 处理方法的形参, 以说明该形参对应 HTTP 请求中的哪一个参数 ( 属性 ) 

参数 | 默认值 | 取值 | 说明
----- | -------|------|------
name | data | String     | HTTP 请求中的参数名

> _web-lighter_ 会自动从 HTTP 请求中获取参数值并在方法调用时自动注入.  
若 HTTP 请求中参数为 JSON 格式, 同时 _@Request_ 的 _format_ 取值为 `ParamFormat.json` ( 默认值 ), 则将自动解析此 JSON 数据, 以封装为形参所需要的对象. 

> 若 _@Request_ 中 url 设置带有参数占位, 则调用 Action 方法时亦将同时注入从 HTTP 请求的 url 中解析得到的参数.  
> 例如:   

`` | ``
----- | -------
_@Request_  `url` | `/{p1}/doSomething/{p2}`
HTTP 请求 `url` | `http://localhost:8080/test/doSomething/999`
方法声明 | `public ActionResult doSomething( @Param(name = "p1") String str, @Param(name = "p2") Integer id)) { ... }`

> 此时，在 `doSomething` 方法内参数 `str` 和 `id` 的值分别为 "test" 和 999

### <a id="Inject">**@Inject**</a>
___@Inject___ 注解应用于 _Action_ 类中的 HTTP Request 处理方法的形参.  
此注解可用于在方法执行时, 将其它参数注入.  
例如, 如下代码可在调用 `doSomething` 方法时自动实例化1个 `Service` 对象, 并注入.
```java
public ActionResult doSomething( @Inject Service service ) { ... } 
```


### <a id="ActionResult">**ActionResult**</a>
每一个带有 _@Request_ 的方法均应 return 一个 `ActionResult` 类型的对象, 其中封装了欲向前端回传的数据. ActionResult 对象将最终被序列化为 JSON 格式, 并返回前端.  

ActionResult 包含如下属性: 
属性 | 数据类型 | 含意
----- | ---- | -------
code | int | 状态码, 标识业务处理结果. 默认情况下, 正确 ( 成功 ) 为 0, 错误 ( 失败 ) 为 -1
result | Object | 回传的业务数据
message | String | 附加消息, 通常用于存储提示信息, 如: 出错原因
total | Long | 全部记录数. 通常用于分页查询时返回符合条件的记录总数

> 上述属性均为私有 ( private ) 属性, ActionResult 类定义了数个静态方法 ( static ), 可用于生成 ActionResult 实例.

返回前端的 JSON 格式示例: 
```json
{ 
    "code": 0, 
    "result": [{
        "id": 1, 
        "name": "Peter"
        }, {
        "id": 2, 
        "name": "John"
        },
        ...
        ],
    "message": "success",
    "total": 99
}
```

-----
