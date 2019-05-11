web-lighter 是笔者实现的一个小型 Java Web 应用程序的服务端封装, 曾在多个项目中应用, 对于小型 Web 项目开发而言, 实践证明确实可以省不少事. 因此, 将其共享出来, 若有需要, 拿去用便是. 

##  web-lighter 能做什么? 
- 分发 HTTP Request :  接收Http请求并分发给用户自定义的 _Action-Method_ 进行处理, 并将处理结果发回前端
- HTTP Request 参数的自动解析与注入( 支持 text / json )
- _Action_ 类自动实例化与执行 ( _Action_ 为用户自定义逻辑的封装 )
- 基于Java注解 ( Annotation ) 的注入配置
- 多文件上传支持
- 文件下载支持 ( 可添加下载鉴权逻辑 )


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

## Web-lighter 使用方法

请移步 [Web-lighter 介绍](https://baileykm.github.io/2018/06/01/Web-lighter-一个小型的-Java-Web-服务器端封装)

