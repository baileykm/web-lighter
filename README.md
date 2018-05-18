# Web-lighter
___Web-lighter___ 是一个小型的 _Java Web_ 服务器端封装.

## Web-lighter 能做什么? 
- 分发 _HTTP Request_ : 接收 _Http_ 请求并分发给用户自定义的 _Action-Method_ 进行处理, 并将处理结果发回前端
- _HTTP Request_ 参数的自动解析与注入( 支持 _text / json_ )
- _Action_ 自动实例化与调用 ( _Action_ 为用户自定义逻辑的封装 )
- 基于 _Java Annotation_ ( Java注解 ) 的注入配置
- 多文件上传支持


## 使用示例
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


## 使用方法概要
1. 将 web-lighter_xxx.jar 及依赖资源添加至项目构建路径
2. 定义 _Action_ 类, 以封装你的业务逻辑. ( _Action_ 类须继承 _com.pr.web.lighter.action.ActionSupport_ )
3. 在 _Action_ 类中添加处理 HTTP Request 的方法, 并在该方法上添加 _@Request_ 注解, 以标注该类可以响应的 HTTP Request. 若同时需要上传文件, 可在该方法上同时添加 _@Upload_ 注解
4. 为 _Action_ 类中的 HTTP Request 处理方法添加必要的形参, 同时为形参添加 _@Param_ 注解
5. 在 HTTP Request 处理方法中书写你的业务处理代码, 并最终返回1个 _ActionResult_ 对象
> 参见前面的 "使用示例"

## Web-lighter 配置详述
- **web-lighter.xml**  _<small>( 可选, 并非必需 )</small>_
此文件为 _Web-lighter_ 的主配置文件, 在此文件中可自定义关于 _Web-lighter_ 的一些通用配置.  
web-lighter.xml可配置的信息包括:
<table width="100%">
    	<tr>
        	<th>参数名</th>
        	<th>默认值</th>
        	<th>可选值</th>
        	<th>说明</th>
    	</tr>
	<tr>
       	<td>urlPrefix</td>
       	<td>```/wl```</td>
       	<td>String</td>
       	<td>**url 前缀**  
			web-lighter 按路径匹配方式拦截需要处理的请求, 即默认状态下, web-lighter 将拦截所有 url 以 "/wl" 开头的 HTTP 请求.   
			因此, 编写前端代码时应注意为 url 加上前缀, 例如: http://localhost:8080/wl/doSomething
		</td>
    	</tr>
	<tr>
       	<td>dateFormat</td>
       	<td>```yyyy-MM-dd hh:mm:ss```</td>
       	<td></td>
       	<td>日期型数据序列化/反序列化格式.</td>
    	</tr>
</table>

你可以直接创建一个 XML 文件或从 web-lighter_xxx.jar 中复制一份放到 _src_ 根目录即可.  
  > web-lighter.xml 配置文件并非必需, 也就是说, 如果 _Web-lighter_ 的默认配置已满足你的需求, 则可省去此配置 web-lighter.xml .
