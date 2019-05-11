web-lighter 是一个小型 Java Web 应用程序的服务端封装, 曾在多个项目中应用, 对于小型 Web 项目开发而言, 实践证明确实可以省不少事. 因此, 将其共享出来, 若有需要, 拿去用便是. 

##  web-lighter 能做什么? 
- 分发 HTTP Request :  接收Http请求并分发给用户自定义的 _Action-Method_ 进行处理, 并将处理结果发回前端
- HTTP Request 参数的自动解析与注入( 支持 text / json )
- _Action_ 类自动实例化与执行 ( _Action_ 为用户自定义逻辑的封装 )
- 基于Java注解 ( Annotation ) 的注入配置
- 多文件上传支持
- 文件下载支持 ( 可添加下载鉴权逻辑 )


## 先来个例子

为了尽快能对 web-lighter 有一个初步的认识, 我们举个简单的例子, 了解一下 web-lighter 的基本用法, 以及它的基本功能.

> 为了让例子变得尽可能地简单, 其间省略了部分安装与配置 web-lighter 的过程, 如果看完本例仍有兴趣继续尝试使用 web-lighter 请移步查看[Web-lighter 文档](https://baileykm.github.io/2018/06/01/Web-lighter-一个小型的-Java-Web-服务器端封装).

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

- 通过上例可以看到 web-lighter 可通过简单地继承 `com.bailey.web.lighter.action.ActionSupport` 并配合必要的注解即可将一个普通的 Java 类转化为可接收并处理前端请求的 *Action* 类.  这也是 web-lighter 的基本功能.

- web-lighter 可将前端发来的数据作为 Action 方法的形参注入, 以便使用. (若上行数据为 *JSON* 格式则自动进行解析, 并注入)
- web-lighter 可自动将服务器端需要反馈给前端的数据封装后返回前端 (通过在 *Action* 方法中返回 *ActionResult* 的实例 ). 若有必要将自动序列化为 *JSON* 格式字符串.

------

## 使用文档
[Web-lighter 文档](https://baileykm.github.io/2018/06/01/Web-lighter-一个小型的-Java-Web-服务器端封装).
