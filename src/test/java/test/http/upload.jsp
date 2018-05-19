<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<h1>文件上传</h1>
<img src="pic.png" alt="">
<form method="post" action="/wl/test/uploadFile/888" enctype="multipart/form-data">
    str: <input type="text" name="str" value="this is String" /><br/>
    int: <input type="text" name="int" value="999" /><br/>
    checkbox:   <input type="checkbox" name="arr" value="arr1" checked />arr1
                <input type="checkbox" name="arr" value="arr2" checked />arr2<br/>
    vo: <input type="text" name="vo" value='{"id":1,"name":"zhang"}' /><br/>
    volist: <input type="text" name="volist" value='[{"id":1, "name":"wang"},{"id":2, "name":"li"}]' /><br/>
    uploadFile: <input type="file" name="uploadFile" /><br/>
    uploadFile2: <input type="file" name="uploadFile2" /><br/>
    <br/><br/>
    <input type="submit" value="SUBMIT" />
</form>
</body>
</html>