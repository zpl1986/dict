<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>上传txt文件</title>
</head>
<body>
    <header>
    </header>
    <div id="container">
    
        <nav>
            <a href="index.jsp">回到首页</a>
        </nav>
        <br />
        <section>
            <form action="/dict/api/upload.json" enctype="multipart/form-data" method="post">
            
			    <input type="file" name="txt">
			    <br />
            	<input name="level" type="checkbox" value="precollege" />高中
				<input name="level" type="checkbox" value="cet4" checked />cet-4
				<input name="level" type="checkbox" value="cet6" checked/>cet-6
				<input name="level" type="checkbox" value="ky" checked/>考研
				<input name="level" type="checkbox" value="tem4" checked/>tem-4
				<input name="level" type="checkbox" value="tem8" checked/>tem-8
				<br />
			    <input type="submit">
			</form>
 
        </section>
    </div>
</body>
</html>