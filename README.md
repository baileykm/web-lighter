---
title: Web-lighter 简介
date: 2018-06-01 02:54:04
tags: [Web, Web-lighter]
categories: Web开发
---

web-lighter 是笔者实现的一个小型 Java Web 应用程序的服务端封装, 曾在多个项目中应用, 对于小型 Web 项目开发而言, 实践证明确实可以省不少事. 因此, 将其共享出来, 若有需要, 拿去用便是. 

<!--more-->

<!-- toc -->

取名 web-lighter 有 2 个意图:

> 1. 作为一个称谓, 便于文中叙述.
>2. 此项目的初衷想为 Java Web 项目开发添把火, 力图让小型的 Web 项目开发变得更轻松一些, 故名web-lighter.
> 

web-lighter 项目源代码可至 https://github.com/baileykm/web-lighter 下载.

> 本文内容基于 web-lighter 1.1.0 版本



##  web-lighter 能做什么? 
- 分发 HTTP Request :  接收Http请求并分发给用户自定义的 _Action-Method_ 进行处理, 并将处理结果发回前端
- HTTP Request 参数的自动解析与注入( 支持 text / json )
- _Action_ 类自动实例化与执行 ( _Action_ 为用户自定义逻辑的封装 )
- 基于Java注解 ( Annotation ) 的注入配置
- 多文件上传支持
- 文件下载支持 ( 可添加下载鉴权逻辑 )



## 先来个例子

为了尽快能对 web-lighter 有一个初步的认识, 我们举个简单的例子, 了解一下 web-lighter 的基本用法, 以及它的基本功能.

> 为了让例子变得尽可能地简单, 其间省略了部分安装与配置 web-lighter 的过程, 如果看完本例仍有兴趣继续尝试使用 web-lighter 请继续阅读本文后续内容, 否则, 直接关闭本页面便是.

OK, 开始吧...

1. 使用你喜欢的 IDE 创建一个动态 Java Web 项目 (步骤略)

2. 将 Web-lighter 的 Jar 文件及其依赖添加到项目构建路径 (参见下节 "Web-lighter 安装", 支持 Maven)

3. 创建一个 Java 类 (`ActionExample.java`), 代码如下:

   ```java
   import com.bailey.web.lighter.action.ActionResult;
   import com.bailey.web.lighter.action.ActionSupport;
   import com.bailey.web.lighter.annotation.Param;
   import com.bailey.web.lighter.annotation.Request;
   
   import java.util.List;
   
   public class ActionExample extends ActionSupport {        // 继承 ActionSupport, 后文称这样的类为 Action 类
   
       @Request(url = "/doSomeThing")                        // Request注解标注此方法可以接收的 url, 后文称这样的方法为 Action 方法 (Action-Method)
       public ActionResult doSomeThing(
               @Param(name = "intParam") Integer id,         // 上行参数, 整型
               @Param(name = "strParam") String str,         // 上行参数, 字符串类型
               @Param(name = "voParam") VO vo,               // 上行参数, 值对象, 可用于接收前端传来的对象数据
               @Param(name = "voArrParam") List<VO> voArr    // 上行参数, List<VO>, 可用于接收前端传来的数组数据
       ) {
           try {
               System.out.println("intParam = " + id);
               System.out.println("strParam = " + str);
               System.out.println("vo = {id : " + vo.getId() + ", name : " + vo.getName() + "}");
   
               for (int i = 0; i < voArr.size(); i++) {
                   System.out.println("voArr[" + i + "] = {id : " + voArr.get(i).getId() + ", name : " + voArr.get(i).getName() + "}");
               }
   
               // ...	执行其它业务
   
               // 成功, 直接将前端传来的voParam回传
               return ActionResult.success(vo);                  
           } catch (Exception ex) {
               // 抛出异常时向前端返回错误信息
               return ActionResult.failure("Something wrong");    
           }
       }
   }
   ```

   上述代码中涉及的 VO 类可根据实际业务需要定义为一个普通的 *JavaBean* 类, 例如:

   ```java
   public class VO {
     private Integer id;
     private String  name;
     
     public void setId(Integer id) { this.id = id;}
     public void getId() { return id;}
     public void setName(String name) { this.name = name;}
     public void getName() { return name;}
   }
   ```

4. 创建 HTML 页面 `exmaple.html ` , 引入 *jQuery*, 并嵌入如下 *javascript* 脚本:

   ``` javascript
   var params = {
     "intParam"  : 1,
     "strParam"  : "This is a string.",
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
   };
   
   $.ajax({
     type : "post",
     url : "wl/doSomeThing",           // 注意 url 前缀 "wl"
     contentType: "application/json",  // 注意contentType取值
     dataType : 'json',
     data : JSON.stringify(params),    // 将 js 对象转为 JSON 字符串上行
     success : function(data){
       alert(JSON.stringify(data));    // 输出服务器返回的信息
     }, 
     error : function() {
       alert('error!');
     }
   });
   ```
   
5. 启动 Web 项目, 在浏览器中打开 `exmaple.html`, 如: 在地址栏中输入 

6. 运行结果:

   - 服务器控制台输出:

     ```
     intParam = 1
     strParam = This is a string.
     vo = {id : 999, name : Peter}
     voArr[0] = {id : 10, name : John}
     voArr[1] = {id : 20, name : Joanna}
     ```

   - 前端浏览器输出:

     ```bash
     {"code":0,"result":{"id":999,"name":"Peter"}}
     ```



------

#### 小结

- 通过上例可以看到 web-lighter 可通过简单地继承 `com.bailey.web.lighter.action.ActionSupport` 并配合必要的注解即可将一个普通的 Java 类转化为可接收并处理前端请求的 *Action* 类.  这也是 web-lighter 的基本功能.

- web-lighter 可将前端发来的数据作为 Action 方法的形参注入, 以便使用. (若上行数据为 *JSON* 格式则自动进行解析, 并注入)
- web-lighter 可自动将服务器端需要反馈给前端的数据封装后返回前端 (通过在 *Action* 方法中返回 *ActionResult* 的实例 ). 若有必要将自动序列化为 *JSON* 格式字符串.

------

> 如前所述, 上例仅简单展示了 web-lighter 的基本功能, 关于 web-lighter 的安装与详细使用方法, 请继续阅读后文.





## 安装 Web-lighter 

可使用 *maven* 方式安装 web-lighter, 或手动安装:

- **Maven 安装**  
 web-lighter  已发布至 _Maven Central Repository_ ,  如果你的项目使用 _Maven_ 则可将下面的代码复制到 _pom.xml_ 文件中的 `<dependencies>...</dependencies>` 一节即可.

```xml
<dependency>
    <groupId>com.github.baileykm</groupId>
    <artifactId>web-lighter</artifactId>
    <version>1.1.0</version>
</dependency>
```

- **手动安装**:

  下载 *web-lighter.jar* 及第3方资源, 复制到 Web 项目的 _WEB-INF/lib_ 下, 并将其添加至项目构建路径.  

  web-lighter 依赖于如下第3方资源:  

  javax.servlet-api-3.0.1.jar  

  commons-lang3-3.7.jar  

  commons-io-2.2.jar  

  commons-logging-1.2.jar  

  commons-fileupload-1.3.3.jar  

  gson-2.8.4.jar  

  UserAgentUtils-1.2.4.jar  



## Web-lighter 使用方法概要

1. 安装 web-lighter 及依赖资源
2. 定义 *Action* 类, 以封装你的业务逻辑. ( _Action_ 类须继承 `com.bailey.web.lighter.action.ActionSupport` )
3. 在 _Action_ 类中添加必要的 HTTP Request 处理方法 *( Action方法 )*  
   3.1 在 _Action方法_ 上添加 `@Request` 注解, 以标注该方法可以响应的特定的 HTTP 请求  
   3.2 为 _Action方法_ 形参表中的参数添加 `@Param`注解 或  `@Inject`注解, 以说明参数值的来源  
   3.3 在 _Action方法_ 体中编写你的业务处理代码, 并最终返回 `ActionResult`实例  
4. 若需要支持文件上传或下载, 可同时在 _Action方法_ 上添加 `@Upload`和`@Download` 注解



## Web-lighter 配置与使用详述

### web-lighter.xml
此文件为 web-lighter 的主配置文件, 可自定义关于 _web-lighter_ 的一些通用配置.

> web-lighter.xml 配置文件并非必需, 也就是说, 若上述默认配置已满足你的需求, 则可省去 web-lighter.xml .

web-lighter.xml 中可配置的信息包括:  

参数 | 默认值 | 取值 | 说明
----- | -------|------|------
urlPrefix | /wl | String     | **url 前缀**<br/>web-lighter 将按路径匹配方式拦截需要处理的请求, 即默认状态下, web-lighter 将拦截所有 url 以 "/wl" 开头的 HTTP 请求.<br/> 因此, 编写前端代码时应注意为 url 加上前缀, 例如: http://localhost:8080/wl/doSomething 

> 你可以直接创建一个 XML 文件或从 web-lighter_xxx.jar 中复制一份放到 _src_ 根目录即可. 
>
> web-lighter.xml 文件格式如下:
```xml
<?xml version="1.0" encoding="utf-8"?>
<configuration>
    <urlPrefix>/wl</urlPrefix>
</configuration>
```



### @Request

`@Request` 注解应用于 _Action_ 方法上, 以标注该方法用于接收并处理 HTTP 请求  

> _Action_ 为用户自定义 HTTP 请求处理逻辑的封装, 应继承 _com.pr.web.lighter.action.ActionSupport_   

参数 | 默认值 | 取值 | 说明
----- | -------|------|------
url | null | String     | 可接收并处理的请求 url<br/>支持通配符和参数, 如:<br/>`/{param1}/*.action/{param2}`<br/>其中, `*` 代表匹配任意个任意字符, {param1} 表示此为参数占位, 其中的 param1为参数名<br/><br/>** 注意**<br/>- web-lighter 使用路径匹配方式拦截前端请求, 默认情况下, 若请求的 url 匹配模式 "/wl/" 时将被 web-lighter 拦截并处理. 若需要更改拦截匹配模式, 请在 web-lighter.xml 配置文件中进行设置.<br/>- 前端访问路径记得添加路径前缀 ( 默认为"/wl" ), 如: http://localhost:8080<strong style="color:red">/wl</strong>/doSomething.action<br/>- 本注解的 url 参数无需添加路径前缀, 如: /doSomething, 运行时 web-lighter 将会匹配 /wl/doSomething<br/>- 虽然 url 中支持类似 *RESTful Web* 风格的参数, 但 web-lighter 暂未完全支持 RESTful Web 的标准方法 
format | `ParamFormat.json` |  `ParamFormat.json`<br/>`ParamFormat.text`  | HTTP 请求中参数的格式, 默认为 JSON 格式<br/>Content-Type = "application/json" 时此参数无效 ( 始终被理解为JSON 格式数据)



### @Param

`@Param` 注解应用于  _Action_ 方法的形参, 以说明该形参对应 HTTP 请求中的哪一个参数 ( 属性 ) 

参数 | 默认值 | 取值 | 说明
----- | -------|------|------
name | data | String     | HTTP 请求中的参数名

web-lighter 会自动从 HTTP 请求中获取参数值并在方法调用时自动注入.  
若 HTTP 请求中参数为 *JSON* 格式, 同时 _@Request_ 的 _format_ 取值为 `ParamFormat.json` ( 默认值 ), 则将自动解析此 *JSON* 数据, 并封装为形参所需要的对象. 

特别地, 若 _@Request_ 中 url 设置带有参数占位, 则调用 Action 方法时亦将同时注入从 HTTP 请求的 url 中解析得到的参数. 

例如: 

- *Action* 方法为:  

  ```java
  @Request(url="/{p1}/doSomething/{p2}")
  public ActionResult doSomething( 
    	@Param(name = "p1") String str, 
    	@Param(name = "p2") Integer id)) 
  { ... }
  ```

- 此时若 *HTTP* 请求的 `url` 为: `http://localhost:8080/test/doSomething/999`，则在 `doSomething` 方法内参数 `str` 和 `id` 的值将分别为 "test" 和 999



### @Inject

`@Inject` 注解应用于 _Action_ 方法的形参.  此注解可用于在方法执行时, 将其它对象作为参数注入.  
例如, 如下代码可在调用 doSomething 方法时自动实例化一个 Service 对象, 并注入.

```java
public ActionResult doSomething( @Inject Service service ) { ... } 
```
> *Service*通常用于封装一些具体的业务功能, 而 *Action*主要负责接收前端请求, 实现业务调度.
>
> 使用@Inject时要求 Service类可以通过`new Service()`或 `Service.getInstance()`的方式获得Service对象.

### ActionResult

每一个 _Action_ 方法  (带`@Request`的方法) 均必须返回一个 `ActionResult` 对象, 其中封装了欲向前端回传的数据. `ActionResult` 对象将最终被序列化为 *JSON* 格式, 并返回前端.  

`ActionResult` 包含如下属性: 

| 属性    | 数据类型 | 含意                                                         |
| ------- | -------- | ------------------------------------------------------------ |
| code    | int      | 状态码, 标识业务处理结果. 默认情况下, 正确 ( 成功 ) 为 0, 错误 ( 失败 ) 为 -1 |
| result  | Object   | 回传的业务数据. 若非 `String` 类型则自动序列化为 *JSON* 字符串 |
| message | String   | 附加消息, 用于将提示信息带回前端, 例如: 出错原因             |
| total   | Long     | 全部记录数. 通常用于分页查询时返回符合条件的记录总数         |

上述属性均为私有 ( private ) 属性, `ActionResult` 类定义了数个静态方法 ( static ), 用于返回 ActionResult 实例.  

**例**: 如下为 Action 类代码

```java
public class ExampleAction extends ActionSupport {

    @Request(url = "/doSomething")
    public ActionResult doSomething() {
        List<Map<String, Object>> users = new ArrayList<>();
        Map<String, Object> user;

        user = new HashMap<>();
        user.put("id", 1);
        user.put("name", "Peter");
        users.add(user);

        user = new HashMap<>();
        user.put("id", 2);
        user.put("name", "John");
        users.add(user);

        return ActionResult.success(users, "It's Ok!", 100L);
    }
}
```

前端访问 http://localhost:8080/wl/doSomething 时将收到如下JSON 字符串: 

```json
{ 
    "code": 0, 
    "result": [{
        "id": 1, 
        "name": "Peter"
        }, {
        "id": 2, 
        "name": "John"
        }],
    "message": "It's Ok!",
    "total": 100
}
```



### @Upload

`@Upload` 注解应用于 _Action_ 方法上, 以标注该方法可支持文件上传 ( 单个 / 多个文件 )  

| 参数           | 默认值             | 取值   | 说明                                                         |
| -------------- | ------------------ | ------ | ------------------------------------------------------------ |
| uploadDir      | upload             | String | 上传文件的保存路径. 默认为网站根目录下的 upload 子目录       |
| nameRule       | *                  | String | 文件在服务器端的命名规则<br/>规则中的星号 ( "\*" ) 表示此部分使用 *UUID* 替换<br/>如: " tmp\_\* " 表示使用 " tmp\_ " + 32位UUID 作为文件名<br/>文件扩展名始终与原文件一致 |
| maxFileSize    | `1024 * 1024 * 40` | int    | 单个文件的最大字节数. 默认40M                                |
| maxRequestSize | `1024 * 1024 * 50` | int    | 请求的最大字节数. 默认50M                                    |

> 注意: HTML 中文件上传 form 的 enctype 属性应为 `multipart/form-data`



### @Download

`@Download` 注解应用于 _Action_ 方法上, 以标注该方法用于支持前端文件下载

* 可在 _Action_ 方法中添加必要的逻辑, 以判定是否允许下载指定的资源.

* _Action_ 处理方法应始终返回一个 _ActionResult_ 实例. 

* 若禁止下载指定资源, 应返回标记为"失败"的 _ActionResult_ 实例, 如: 

  `return ActionResult.failure("您无权下载此资源");`

* 若允许下载指定资源, 则应返回标记为"成功"的 _ActionResult_ 实例, 同时将资源信息带回, 例如:  
```java
// ...
File file = new File("C:\\serverFile.docx");        // 待下载的文件. 亦可是 InputStream 
String clientFileName = "你的文档.docx";             // 客户端保存时的默认文件名

// 返回标记为"成功"的 ActionResult 实例
// DownloadFileInfo 对象用于封装待下载文件的信息
// 亦可直接使用输入流构造 DownloadFileInfo 对象
return ActionResult.success(new DownloadFileInfo(file, clientFileName));
```



------

关于 web-lighter 的介绍到这里就结束了, 叙述中有不妥之处欢迎指正. 

当然也欢迎提出宝贵意义或参与到本项目中来, 让我们一直努力将她变得更好.