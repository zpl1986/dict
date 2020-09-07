<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>h5</title>
    <script type="text/javascript" src="http://libs.baidu.com/jquery/2.0.0/jquery.min.js"></script>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.form/4.3.0/jquery.form.js"></script>
    
<script type="text/javascript">
	//创建测试实例
	function createInstance() {
		$("#f1").ajaxSubmit({
			 success: function (data) {
					console.log(data.result);
					console.log(typeof(data.result));
					
					 for(let key  in data.result){
						 localStorage.setItem(key, data.result[key]);
					 }
					 alert("已保存");
					 window.location.href="/dict/index.jsp";
			 }
		});
	}

</script>
</head>
<body>
    <header>
        <h4>提交文件框</h4>
    </header>
    <div id="container">
        <nav>
            <a href="index.jsp">回到首页</a>
        </nav>
        <form id="f1" name="f1" action="/dict/api/text.json"
			method="post">
			<div>请输入英文片段：</div>
			<table style="width:100%; height:100%">
				
				<tr>
					<td><textarea type="text" name="content" id="content"
							cols="150" rows="30"></textarea>
					</td>
				</tr>
				<tr>
				<td><input name="level" type="checkbox" value="precollege" />高中
					<input name="level" type="checkbox" value="cet4" checked />cet-4
					<input name="level" type="checkbox" value="cet6" checked/>cet-6
					<input name="level" type="checkbox" value="ky" checked/>考研
					<input name="level" type="checkbox" value="tem4" checked/>tem-4
					<input name="level" type="checkbox" value="tem8" checked/>tem-8
				</td>
				</tr>
				<tr>
				
					<td colspan="2" align="center">
	                     <input	type="button" value="确定" onclick="createInstance();" />
	                     <!-- 
	                     <input	type="button" value="清空" onclick="reset();" />
	                     -->
                     </td>
				</tr>
			</table>
		</form>
    </div>
</body>
</html>